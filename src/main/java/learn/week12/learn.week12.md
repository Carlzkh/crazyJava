# 第 12 周：用 Redis 缓存按 id 查询的用户

这一周的代码还没改。本机没有 Redis，用 winget 安装 Windows 版 Redis 8.10.2 时，安装包要从 GitHub 下载，下载失败，所以实验留到 Redis 能在 `localhost:6379` 启动之后再做。

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
