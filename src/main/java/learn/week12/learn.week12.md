# 第 12 周：用 Redis 缓存按 id 查询的用户

这一周的 Redis 代码还没改。本机没有 Redis，用 winget 安装 Windows 版 Redis 8.10.2 时，安装包要从 GitHub 下载，下载失败，所以缓存实验留到 Redis 能在 `localhost:6379` 启动之后再做。

第 8 节之后是 Redis 实验搁置后继续学的内容：定时任务、异步方法、接口文档、日志文件。消息队列需要本机没有的中间件，先不写。

对照的现有代码是 `UserService.getUserById`。它现在每次都调用 `userRepository.findById(id)`，没有缓存。

要改的文件：

- `pom.xml`：增加 `spring-boot-starter-data-redis`
- `UserService`：查询先读缓存，修改和删除成功后删掉缓存
- `UserServiceTest`：补上假的 `StringRedisTemplate`，否则原来的 6 条测试会在 `opsForValue()` 处空指针

## 1. 缓存解决什么问题

按 id 查用户，短时间里可能被重复调用。每次都去 MySQL，结果却一样。Redis 把第一次查到的用户放在内存里，下一次用同一个 id 直接拿回来，不再执行 `findById`。

缓存不是数据库的替代。用户被修改或删除后，Redis 里那份旧数据必须删掉，否则下一次还会读到改之前的姓名。

| 时机 | 做什么 | 日志 |
|------|--------|------|
| 缓存里没有这个 id | 查数据库，查到后写入 Redis，10 分钟后过期 | `从数据库读取用户` |
| 缓存里已有这个 id | 把 JSON 转回 `User`，不调用 `findById` | `从缓存读取用户` |
| 修改或删除成功 | 删掉 `user:{id}` | 下一次查询会再次出现「从数据库读取用户」 |
| 数据库里没有这个用户 | 抛 `USER_NOT_FOUND`，不要把「不存在」放进缓存 | 没有缓存日志 |

没查到的用户不要缓存。否则一个人刚被创建，缓存里却还记着「没有」，要等过期才能查到。

这次只包 `getUserById`。`GET /api/v1/users/{id}/addresses` 仍直接查库，不会读这份缓存，里面的地址列表也不会被放进 Redis。

## 2. 先让 Redis 在本机跑起来

Spring Boot 默认连接 `localhost` 的 `6379`。Redis 没启动就去查用户，会得到连接失败，接口不会返回用户。

这台电脑没有 Docker，也没有 WSL。网络能访问 GitHub 时，在 PowerShell 执行：

```powershell
winget install --id taizod1024.redis-windows-fork -e --accept-package-agreements --accept-source-agreements
```

装好后启动 `redis-server`。能在 `6379` 上接受连接之后，再改项目。

## 3. 依赖

在 `pom.xml` 的 `dependencies` 里增加下面这一段。版本不要写，由 `spring-boot-starter-parent` 2.7.18 管理。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

用 `StringRedisTemplate`。键和值都是字符串，值是用户的 JSON。项目里已经有 Fastjson，用 `JSON.toJSONString` 和 `JSON.parseObject` 即可，不必再引入 Jackson。

## 4. 改 `UserService`

构造方法增加一个参数。原来的测试用 `@InjectMocks` 创建 `UserService`，少了这个参数时，Redis 相关调用会是 `null`。

```java
private final UserRepository userRepository;
private final StringRedisTemplate stringRedisTemplate;

public UserService(UserRepository userRepository, StringRedisTemplate stringRedisTemplate) {
    this.userRepository = userRepository;
    this.stringRedisTemplate = stringRedisTemplate;
    log.info("1. UserService 构造器被调用");
}

private String cacheKey(long id) {
    return "user:" + id;
}
```

`getUserById` 改成先读缓存：

```java
public User getUserById(long id) {
    String key = cacheKey(id);
    String cached = stringRedisTemplate.opsForValue().get(key);
    if (cached != null) {
        log.info("从缓存读取用户: {}", id);
        return JSON.parseObject(cached, User.class);
    }
    User user = userRepository.findById(id);
    if (user == null) {
        throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
    }
    stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(user), Duration.ofMinutes(10));
    log.info("从数据库读取用户: {}", id);
    return user;
}
```

`setUserById` 和 `deleteUserById` 在数据库更新成功之后各加一行：

```java
stringRedisTemplate.delete(cacheKey(id));
```

要加的 import：

```java
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
```

键就用 `user:1` 这种形式。10 分钟是为了避免缓存永远留着；修改和删除不能只靠过期，必须马上删除。

## 5. 原来的单元测试要补假 Redis

`UserServiceTest` 不连接真实 Redis。类里增加：

```java
@Mock
private StringRedisTemplate stringRedisTemplate;

@Mock
private ValueOperations<String, String> valueOperations;
```

两条 `getUserById` 测试在调用 Service 之前加上。不写的话，`opsForValue()` 返回 `null`，一调用 `get` 就空指针。

```java
when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
when(valueOperations.get("user:" + userId)).thenReturn(null);
```

`deleteUserById_shouldDelete_whenUserExists` 和 `setUserById_shouldUpdate_whenUserExists` 加上：

```java
when(stringRedisTemplate.delete(anyString())).thenReturn(true);
```

然后执行：

```powershell
mvn "-Dtest=UserServiceTest" test
```

应仍然是 `Tests run: 6`，`BUILD SUCCESS`。这些测试证明的是业务分支，假 Redis 只是为了让构造方法和缓存调用不报错。

## 6. 用接口看三次查询

用户接口要带第 11 周的 token。当前环境是 `prod` 时，MyBatis 的 SQL 日志被关掉了，所以不要靠控制台里有没有 SQL 来判断，看 `UserService` 打出来的两句日志。

```python
import requests

base = "http://localhost:8080/api/v1"
login = requests.post(base + "/login", json={"username": "admin", "password": "123456"})
headers = {"Authorization": login.json()["data"]}

print(requests.get(base + "/users/1", headers=headers).json()["data"]["name"])
print(requests.get(base + "/users/1", headers=headers).json()["data"]["name"])
requests.put(base + "/users/1", json={"name": "缓存已更新", "age": 18}, headers=headers)
print(requests.get(base + "/users/1", headers=headers).json()["data"]["name"])
```

id 为 1 的用户要存在，并且 `is_deleted = 0`。第 8 周的自调用实验可能把他标成已删除，需要先执行：

```sql
UPDATE user SET is_deleted = 0 WHERE id = 1;
```

预期：

| 次序 | 接口 | 日志 | 打印出的姓名 |
|------|------|------|----------------|
| 1 | `GET /users/1` | `从数据库读取用户: 1` | 改之前的姓名，例如 `prod` |
| 2 | 再 `GET /users/1` | `从缓存读取用户: 1` | 与第一次相同 |
| 3 | `PUT` 把姓名改成「缓存已更新」 | 删除键 `user:1` | 无 |
| 4 | 再 `GET /users/1` | 再次 `从数据库读取用户: 1` | `缓存已更新` |

第二次如果仍是「从数据库读取用户」，说明缓存没有写上，或键不一致。第三次如果还是旧姓名，说明修改后没有删除 `user:1`，读到的仍是缓存里的旧 JSON。

## 7. 和前面几周的关系

| 周次 | 和缓存的关系 |
|------|----------------|
| 第 4 周 | Redis 的地址用默认的 `localhost:6379`，不必先写进 `AppProperties` |
| 第 7 周 | 缓存的是 `findById` 的结果，不是带地址列表的那次查询 |
| 第 9 周 | 测试里用假的 `StringRedisTemplate`，不要求本机真有 Redis |
| 第 10、11 周 | 调用户接口要带 `Authorization`。token 仍在内存里，和 Redis 不是同一个存储 |

## 8. 定时任务

启动类上有 `@EnableScheduling` 之后，带 `@Scheduled` 的方法会按时间自己执行，不用接口调用。

`LoginService.logTokenCount`：

```java
@Scheduled(fixedRate = 15000)
public void logTokenCount() {
    log.info("当前有效 token 数量: {}", tokens.size());
}
```

`fixedRate = 15000` 表示上一轮开始 15 秒后开始下一轮。实验里两次日志是：

| 时间 | 线程 | 内容 |
|------|------|------|
| `14:32:27` | `scheduling-1` | `当前有效 token 数量: 0` |
| `14:32:42` | `scheduling-1` | `当前有效 token 数量: 1` |

第一次还没有登录。第二次之前登录成功，集合里有了一串 token，所以数量变成 1。`scheduling-1` 不是处理 HTTP 的 `nio-8080-exec-...`。默认只有这一条线程跑定时任务，上一个任务没结束时，下一个会等它。程序关掉，任务也停。

## 9. 异步方法

`@EnableAsync` 也加在启动类上。`LoginAuditService.record` 标了 `@Async`，里面先睡 3 秒再打日志。`LoginService.login` 在 token 放进集合之后调用它，然后马上 `return`。

必须从另一个类调用。在 `LoginAuditService` 里面写 `this.record()`，仍会在当前请求线程里执行，登录接口就要多等这 3 秒。这和事务那周同一个类里自己调用自己的现象一样。

一次登录的实际时间：

| 时间 | 线程 | 发生了什么 |
|------|------|------------|
| `14:46:28.626` | `nio-8080-exec-7` | 接口已经返回 token，切面记录耗时 1 毫秒 |
| `14:46:31.639` | `task-2` | 「登录记录已写完」。大约晚 3 秒 |

`task-2` 既不是请求线程，也不是 `scheduling-1`。调用方没有等 `Thread.sleep(3000)`。

这次请求体里的密码也被 `LogAspect` 打进了日志。练习时能看到即可，正式的登录日志不要记录密码。

## 10. 接口文档

页面地址是 `http://localhost:8080/swagger-ui/index.html`。依赖是已经在 `pom.xml` 里的 `springdoc-openapi-ui`。这些注解不改变接口能不能调用，只改变文档里的分组和标题。

| 类 | 注解 | 文档上的分组 |
|----|------|----------------|
| `UserController` | `@Tag(name = "用户管理")` | 用户管理 |
| `LoginController` | `@Tag(name = "登录")`，方法上 `@Operation(summary = "登录并返回 token")` | 登录 |
| `FileController` | `@Tag(name = "文件")`，方法上 `@Operation(summary = "上传一个文件，返回保存路径")` | 文件 |
| `HelloController` | 没有文档用的 `@Tag` | `hello-controller`，用的是类名 |

`UserController` 上曾经写过 `@Tag("用户管理")`，import 却是 `org.junit.jupiter.api.Tag`。那是测试标记，SpringDoc 不读。文档注解是 `io.swagger.v3.oas.annotations.tags.Tag`，并且要写 `name`。

第一次打开页面报 `Unable to render this definition`，是因为 Fastjson 被放在消息转换器最前面。`/v3/api-docs` 的返回值已经是 OpenAPI JSON 的 `byte[]`，Fastjson 又序列化了一次，文档里就没有 `openapi` 版本号。现在 Fastjson 放在转换器列表最后，`byte[]` 先按原样写出，`Result` 仍由 Fastjson 写。

用户接口要 token，页面上原来没有地方填请求头。`OpenApiConfig` 声明了一个名为 `Authorization` 的请求头。重启后右上角有 **Authorize**。先调登录接口，把返回的 UUID 贴进去，不要加 `Bearer`。之后从文档里试用户接口，会自动带上这个头。不点 Authorize，用户接口仍然是 `401`。

## 11. 日志文件

`@Slf4j` 只提供名为 `log` 的对象，写到哪里由 `src/main/resources/logback-spring.xml` 决定。

| 名字 | 写到哪里 |
|------|----------|
| `CONSOLE` | IDEA 的控制台 |
| `FILE` | 项目目录下的 `logs/crazyjava.log`。按天再切一份，历史文件只留 7 天 |

`com.example.crazyjava` 的级别是 `INFO`。`DEBUG` 不会进这个文件。当前 `prod` 配置里的日志级别也是 `INFO`。两个 appender 都挂在 `root` 上，所以控制台和文件内容相同。

登录一次后，`logs/crazyjava.log` 里有：

```text
15:10:15.474 INFO  [http-nio-8080-exec-1] ... 拦截器：POST /api/v1/login
15:10:18.552 INFO  [task-1] ... 登录记录已写完，用户=admin，线程=task-1
```

文件会留在磁盘上。其中也包括请求体里的密码，所以这份日志不要当成可以随便传的记录。
