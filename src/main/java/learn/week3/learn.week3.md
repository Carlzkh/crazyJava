# 第 3 周：Bean 和依赖注入

对照代码在 `CrazyJavaApplication`、`UserController`、`UserService`、`UserRepository`、`UserMapper`。

这一周做完的练习：`UserRepository`、`UserService` 从字段上的 `@Autowired` 改成构造方法注入。`UserController` 的改法相同，还没改之前，字段上仍是 `@Autowired private UserService userService`。

## 1. Bean 是 Spring 创建并保管的对象

你在接口里 `new User()`，得到的是一条用户数据。这个对象谁创建、什么时候回收，都由你自己的代码决定。Spring 不管理它。

带有特定注解的类不一样。程序启动时，Spring 自己 `new` 出对象，放进一个容器。容器里的这份对象叫 Bean。请求进来时，Spring 把已经造好的 Bean 交给需要它的类，而不是每个请求都 `new` 一次。

启动入口：

```java
@SpringBootApplication
public class CrazyJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrazyJavaApplication.class, args);
    }
}
```

`SpringApplication.run` 会创建容器，并扫描 `CrazyJavaApplication` 所在的包 `com.example.crazyjava`，以及这个包下面的所有子包。扫描范围之外的类，即使写了 `@Service`，也不会变成 Bean。`learn.week1` 不在这个包下面，所以那里的 `NameUserQuery` 不会被自动创建。

### 例子

下面四个是 Bean：

| 类 | 注解 | 谁补上实现 |
|----|------|------------|
| `UserController` | `@RestController` | 你自己写的类 |
| `UserService` | `@Service` | 你自己写的类 |
| `UserRepository` | `@Repository` | 你自己写的类 |
| `UserMapper` | `@Mapper` | 接口。实现由 MyBatis 按 XML 生成 |

`User` 实体没有这些注解。每次新增用户都是 `new` 出来的普通对象，不是 Bean。

## 2. 交给容器的几种注解

`@Component` 是最基本的标记：这个类请容器创建。另外三个是它的细分，方便看出这一层干什么。Spring 扫描时，这四个都会被创建成 Bean。

| 注解 | 放在哪一层 | 项目里的类 |
|------|------------|------------|
| `@Component` | 不好归类的通用组件 | 日志切面 `LogAspect` 上是 `@Component` |
| `@Service` | 业务 | `UserService` |
| `@Repository` | 数据访问 | `UserRepository` |
| `@Controller` | 返回页面 | 这个项目没用。它返回视图名 |
| `@RestController` | 返回 JSON 或文本 | `UserController`。等于 `@Controller` 再加上「返回值直接写进响应」 |

`@Mapper` 不是 `@Component` 这一家的注解，但 MyBatis 会把它注册进同一个容器。`UserRepository` 的构造方法才能收到 `UserMapper`。

### 例子

`UserController` 类上的 `@RestController` 同时做两件事：把它放进容器，并让 `return Result.success(...)` 变成 JSON，而不是找一个网页模板。

## 3. 依赖关系：谁使用谁，谁先创建

依赖关系是：

```text
UserController → UserService → UserRepository → UserMapper
```

左边使用右边。启动时从右边开始创建，因为左边的构造方法要等右边的对象已经在容器里。

1. `UserMapper`：没有你写的实现类。
2. `UserRepository`：构造方法需要 `UserMapper`。
3. `UserService`：构造方法需要 `UserRepository`。
4. `UserController`：需要 `UserService`。

这四个 Bean 在整个程序里各一份。10 个请求进来，`UserService` 的构造方法也只在启动时执行 1 次。停掉程序再启动，才会再执行 1 次。

这种默认方式叫单例。同一个容器里，一种 Bean 只有一个对象，所有请求共用它。所以 Bean 里不要存放「当前这个请求才有」的数据，例如当前登录用户。那种数据放在方法参数或局部变量里。

### 例子

`UserRepository` 改完之后是：

```java
@Repository
public class UserRepository {

    private final UserMapper userMapper;

    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
```

`final` 表示这个字段只能在构造方法里赋值一次。之后每次 `findAll()` 用的都是当初传进来的那一个 `UserMapper`。

## 4. 字段注入和构造方法注入

两种写法都是把容器里的 Bean 交过来，差别在于什么时候交、缺了会怎样。

**字段注入。** 对象先被无参构造出来，再把依赖填进字段。`UserController` 还是这种：

```java
@Autowired
private UserService userService;
```

类外面仍可以自己 `new UserController()`。这样创建时 Spring 没有参与，`userService` 是 `null`，一调用就会空指针。测试里如果忘记注入，也要等到方法执行才暴露。

**构造方法注入。** 依赖写在构造方法的参数上。没有 `UserRepository`，就 `new` 不出 `UserService`。缺依赖时，程序在启动阶段失败，而不是等到某一次请求。

```java
private final UserRepository userRepository;

public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    log.info("1. UserService 构造器被调用");
}
```

这个类只有一个构造方法时，Spring 能看出来该调用它，构造方法上不用再写 `@Autowired`。

如果类里有两个构造方法，Spring 无法判断用哪一个，必须在要使用的那个上面写 `@Autowired`。`UserService` 改注入时，原来的无参构造方法要删掉，只留带 `UserRepository` 的这一个。

### 例子

| | 字段上的 `@Autowired` | 构造方法参数 |
|--|------------------------|----------------|
| 对象怎么来 | 先无参构造，再填字段 | 构造时就必须传入依赖 |
| 字段能不能是 `final` | 不能。`final` 必须在构造时赋值 | 可以，而且应该写成 `final` |
| 自己 `new` 这个类 | 能 new，依赖是 `null` | 必须自己把依赖传进去，否则编不过 |
| 依赖缺失 | 往往到运行某次请求才空指针 | 启动时就失败 |
| 注解 | 字段上要写 `@Autowired` | 只有一个构造方法时可以不写 |

## 5. Bean 的一生

对照 `UserService` 里已经打好的日志。

1. **构造方法。** Spring 调用 `new UserService(userRepository)`。日志 1 在这里。此时 `userRepository` 已经在参数里。
2. **`@PostConstruct`。** 对象创建完、依赖也齐了，接着执行 `init()`。日志 2 在这里。适合做启动后的检查。
3. **使用中。** 之后才有请求进来，调用 `getUserById`、`createUser`。
4. **`@PreDestroy`。** 程序正常关闭时执行 `destroy()`。日志 3 不在启动时出现。直接杀进程时，这个方法可能来不及执行。

一次启动里，日志 1 和日志 2 会出现。日志 3 要等你停掉程序。

### 例子

```java
public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    log.info("1. UserService 构造器被调用");
}

@PostConstruct
public void init() {
    log.info("2. UserService @PostConstruct 被调用");
}

@PreDestroy
public void destroy() {
    log.info("3. UserService @PreDestroy 被调用");
}
```

`@PostConstruct` 和 `@PreDestroy` 标注的方法不能有参数。一个类里应该只有一个 `@PostConstruct`。Spring 会把标了这个注解的方法都执行一遍。

`testTransaction()` 如果还留着 `@PostConstruct`，它会和 `init()` 一起在每次启动时执行。那个方法里面调用了 `userRepository.deleteById(1L)`，于是每次启动都会去删 id 为 1 的用户。生命周期演示只保留 `init()` 上的注解。

## 6. @SpringBootApplication 里面有三件事

写在启动类上的这一个注解，相当于下面三个叠在一起：

| 包含的注解 | 作用 |
|------------|------|
| `@SpringBootConfiguration` | 这个类自己也是配置类，里面可以用 `@Bean` 方法声明对象 |
| `@EnableAutoConfiguration` | 按 `pom.xml` 里的依赖自动配置。有 `spring-boot-starter-web` 就准备 Web 服务器，有 MyBatis 起步依赖就准备 Mapper 扫描 |
| `@ComponentScan` | 扫描启动类所在的包和子包，创建 Bean |

自动配置不是去读 Spring 源码才能用。你加了一个起步依赖，对应的服务器、数据源、MyBatis 就会按默认方式准备好。端口、数据库地址这些再由 `application.properties` 覆盖。那是第 4 周的内容。

### 例子

`pom.xml` 里有 `mybatis-spring-boot-starter`，所以 `UserMapper` 上一个 `@Mapper` 就能用。没有这个依赖时，容器里不会有 `UserMapper` 这个 Bean，`UserRepository` 的构造方法也拿不到参数，启动失败。

## 7. 用 @Bean 方法自己向容器放一个对象

类上的 `@Service` 适合你自己写的业务类。有些对象来自别人的库，类上不能加注解，就在配置类里写一个方法，用 `@Bean` 把返回值放进容器。

```java
@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
```

谁的构造方法需要 `Clock`，Spring 就会把这个方法的返回值传进去。方法名默认就是 Bean 的名字。

`@Configuration` 本身也是 `@Component`，所以配置类同样会被扫描到。第 4 周把配置文件绑到对象上时，会用到这种类。

### 例子

`UserService` 不需要写成 `@Bean` 方法。它是你自己的类，类上的 `@Service` 就够了。`Clock`、第三方客户端这种改不了源码的类，才用 `@Bean`。

## 8. 常用注解对照

| 注解 | 解决什么问题 | 贴在哪 | 写上之后做什么 | 不写会怎样 | 什么时候用 |
|------|----------------|--------|----------------|------------|------------|
| `@SpringBootApplication` | 没人扫描 `@Service`、`@RestController`，那些注解不会生效 | 启动类 `CrazyJavaApplication`，一个项目只贴一次 | 建容器，按 `pom.xml` 自动配置，扫描启动类所在包和子包 | 容器是空的，接口起不来 | 只贴在启动类上，不要贴在 `UserService` 上 |
| `@Component` | 这个类要交给 Spring 创建，但说不上是业务、数据库或接口 | 类上，如 `LogAspect` | 启动时创建这一个对象放进容器 | 切面不会被创建，Controller 外面的那层日志包不上去 | 不好归类的组件。能用更具体的注解时就不用它 |
| `@Service` | 业务规则写在 Controller 里会每个接口复制一遍；自己 `new UserService()` 又不受 Spring 管理 | 类上，`UserService` | 启动时创建这一个对象，传给 `UserController` 的构造方法。未满 18 岁的判断写在 `createUser` 里 | 容器里没有它，Controller 要这个参数，启动失败。自己 `new` 能跑，但 `@Transactional` 不会按 Spring 的方式生效 | 类里做判断和流程：抛业务异常、决定查哪个方法、删除前先看用户在不在。不写 SQL，不解析 HTTP |
| `@Repository` | Service 直接调 Mapper 时，SQL 方法名一变，业务判断也要跟着改 | 类上，`UserRepository` | 启动时创建这一个对象，传给 `UserService`。`findById` 再转给 `userMapper.findById` | `UserService` 要它，容器里没有，启动失败 | 方法几乎只是转发。不决定未满 18 岁能不能创建。和 `@Service` 一样都是创建对象，差别是这一层不写业务规则 |
| `@RestController` | 外部 HTTP 请求要有 Java 方法来接，返回的是 JSON 不是网页 | 类上，`UserController` | 启动时创建这一个 Controller。`return Result.success(...)` 写成 JSON | `createUser` 只是普通方法，没有请求会进来 | 这个类的方法是给外部调用的接口。`@Controller` 的返回值会被当成页面名，所以这个项目用 `@RestController` |
| `@Configuration` | 有些类在依赖库里，不能去给那个类加 `@Service` | 你自己写的配置类上 | 这个类放进容器，里面的 `@Bean` 方法会被调用 | `@Bean` 方法不会在启动时执行 | 用来声明「不是我写的类」。`UserService` 继续用 `@Service` |
| `@Bean` | 类的源码改不了，没法在类上加 `@Service` | `@Configuration` 类里面的方法上 | 启动时调用该方法，把返回值放进容器，别的构造方法可以要到它 | 方法只是普通方法，启动时不会被调用 | 对象来自别人的库，或创建前要先读配置。例如返回一个 `Clock` |
| `@Autowired` | `@Service` 只负责创建。另一个类要用它时，还得有人把对象递过来 | 字段上，或构造方法上 | 按类型去容器里找。字段类型是 `UserService`，就把那一个填进来 | 字段注入时字段是 `null`，调用才空指针。只有一个构造方法时可以不写，`UserService(UserRepository)` 就是这样。有两个构造方法时必须写，否则不知道用哪一个 | 这个类需要另一个 Bean。不要注入 `User`，`User` 来自 JSON，用 `@RequestBody`。字段注入不能用 `final`；构造方法注入写成 `private final` |
| `@PostConstruct` | 构造方法刚 new 完，还想在依赖填好之后再做一次启动检查 | 无参方法上，`UserService.init` | 依赖注入完成后调用一次，打日志 2 | 没有这次启动回调，接口仍能用 | 启动后检查一次。不会每个请求都跑。不要在这里删数据。`testTransaction()` 上若还留着它，每次启动都会 `deleteById(1L)`。一个类只标一个，标两个会两个都执行 |
| `@PreDestroy` | 程序关闭时，这个 Bean 占用的资源要放开 | 无参方法上，`UserService.destroy` | 正常停止程序、容器关闭时调用一次，打日志 3 | 没有关闭回调。直接杀进程时，写了也可能来不及跑 | 要关连接或确认生命周期。不在启动时出现 |
| `@Scope` | 默认整个程序只有一个对象。有的对象不能给下一个请求接着用 | 类上。不加就是单例 | `@Scope("prototype")` 表示每次需要就 new 一个，`@PostConstruct` 也会多次执行 | 10 个请求共用同一个 `UserService`，构造方法只在启动时跑 1 次。这个项目就该这样 | 对象里存了某一次计算的临时状态。Service、Repository、Controller 都不要加 |
| `@RequestMapping` | 一组接口如果每个方法都写完整地址，前缀改一次要改很多处 | 类上当前缀，或方法上写完整地址 | 类上 `/api/${api.version}/users` 实际是 `/api/v1/users`，方法上只写后面那段 | 方法上如果也没有路径，这个方法没有地址 | 同一模块共用前缀。用户接口都在 `/api/v1/users` 下面 |
| `@GetMapping` | 读取数据的请求要有方法来接 | 方法上 | `GET /api/v1/users` 进分页，`GET /api/v1/users/3` 按 id 查，`GET /api/v1/users/export` 导出 | 打开这个地址没有方法接，得到 404 | 查询、导出。不要用 GET 新增或删除。它是 `@RequestMapping` 的短写法 |
| `@PostMapping` | 新增的数据在请求体里，要和「查询列表」分开。两者路径都可以是 `/api/v1/users` | 方法上，`createUser` | 只有 POST 进这个方法 | POST 找不到方法，或和 GET 撞在一起 | 新增一份资源 |
| `@PutMapping` | 更新某一条要和新增区分开 | 方法上，`setUserById`，路径 `/{id}` | `PUT /api/v1/users/3` 更新 id 为 3 的用户 | 更新请求找不到方法 | 按 id 修改一整份数据 |
| `@DeleteMapping` | 删除要和查询分开。`GET /users/3` 是查看，`DELETE /users/3` 才是删除 | 方法上，`deleteUser` | `DELETE /api/v1/users/3` 删除这条用户 | 删除请求找不到方法 | 按 id 删除，或 `/batch` 批量删除 |
| `@RequestBody` | 新增、修改时，姓名年龄性别是一整份 JSON，不是地址栏上的几个字 | 方法参数上，`createUser(@RequestBody User user)` | 把 `{"name":"张三","age":20}` 变成 `User`，才能 `user.getAge()` | JSON 不填进 `User`，`user` 往往是 `null`，一调用就空指针 | 请求体里是一整份对象 |
| `@RequestParam` | 页码、每页条数、搜索名字是可选筛选，不适合塞进路径，也不适合当成一份用户 JSON | 方法参数上，分页的 `page`、`size`、`name` | 从 `?page=1&size=10&name=张` 取值。`required = false` 表示可不传；`defaultValue = "1"` 表示没传当 1 | 简单类型有时仍能按参数名取到，但必填、默认值、名字说不清 | 个数不多的筛选条件。不要把整份用户拆成一长串问号参数 |
| `@PathVariable` | 要操作的就是某一条，id 应该出现在地址里 | 方法参数上，配合 `@DeleteMapping("/{id}")` | `DELETE /api/v1/users/3` 时，`id` 是 `3` | 路径里的 `3` 不会进参数 | 查、改、删某一个用户 |
| `@Valid` | 字段上写了规则，但没人触发检查 | Controller 参数上，`createUser(@Valid @RequestBody User user)` | 进方法前按 `@NotNull`、`@Size` 等检查。失败则方法体一行不执行，返回 `code=40000` | 字段上的规则形同虚设，空姓名也能进 Service | 参数是带校验注解的对象，而且要在进方法前拦住 |
| `@NotNull` | 姓名、年龄这些字段没传 | 字段上，`User.name`、`User.age` | 值是 `null` 或 JSON 里没有该字段，校验失败 | 不传姓名也能通过这一关 | 这个值必须有，看字段本身就能判断。挡不住空字符串 `""`，那要靠 `@Size(min = 1)` |
| `@Size` | 字符串太短或太长 | 字段上，姓名 1 到 50 个字符 | 长度不在 `min` 和 `max` 之间就失败。`""` 会被 `min = 1` 拦住 | 空字符串、超长姓名能进 Service | 限制字符串或集合长度。不要用在年龄这种数字上 |
| `@Min` / `@Max` | 数字超出范围 | 字段上，年龄 `@Min(0)`、`@Max(150)` | 比下限小或比上限大就失败。`-1` 进不了方法 | 负数年龄能通过校验 | 范围看字段本身就能定。未满 18 岁不写成 `@Min(18)`，16 通过后再由 Service 抛 `USER_AGE_TOO_YOUNG`。要查库才知道的规则也放在 Service |
| `@RestControllerAdvice` | 每个接口自己 `try-catch` 容易漏，失败格式会不一样。没人接时，异常会变成 Spring 默认的一整页错误 | 类上，`GlobalExceptionHandler` | 所有 Controller 抛出的异常都可以送到这个类，返回值写成 JSON | 类里的 `@ExceptionHandler` 不会被当成全局处理 | 所有接口的失败都要同一种 `Result`。一个项目通常一个这种类 |
| `@ExceptionHandler` | 不同异常要不同的 `code`。未满 18 岁是 `60005`，未知错误才是 `50000` | 方法上，`@ExceptionHandler(BusinessException.class)` | 异常类型匹配时执行该方法，`return Result.error(...)` | 异常继续往外抛，前端看不到你的 `Result`。只有 `RuntimeException` 那个方法时，业务异常也会变成 `50000` | 一种异常一个方法。新错误码继续 `throw new BusinessException(枚举)`，一般不用再加新方法 |
| `@Mapper` | `UserMapper` 是接口，没有方法体，也没写实现类 | 接口上 | MyBatis 生成实现，方法名对上 XML 的 `id`。`findById` 对上 `<select id="findById">` | 又没有 `@MapperScan` 时，容器里没有实现，`UserRepository` 启动失败 | 这个接口的每个方法都对应一段 SQL |
| `@Param` | 方法有多个参数时，XML 不知道 `#{name}` 指哪一个 | Mapper 方法的参数上，`findByName(@Param("name") String name)` | XML 里用这个名字取值 | 参数一多就可能对错位，SQL 取不到值 | 两个及以上参数。只有一个参数、XML 用 `#{id}` 时，有的写法可以省略 |
| `@Options` | 插入成功后数据库生成了主键，但 Java 对象上的 `id` 还是空的 | `insert` 方法上，`useGeneratedKeys = true, keyProperty = "id"` | 把数据库生成的主键写回 `user.id`，返回给前端才有编号 | 插入可以成功，返回的 `id` 仍是 `null` | 主键自增，而且调用方需要拿到这个新 id |
| `@Transactional` | 一个方法里改了几次数据库，删到一半失败时，前面已经删掉的不该留在库里 | 方法上，`deleteUsersByIds`、`testTransaction(Long id)` | 方法正常返回就提交；抛出运行时异常就整段撤销 | 前面的 SQL 已经生效，后面失败不会把前面的改动撤掉 | 两次及以上的增删改必须同时成功。只查一次不必加。要贴在外部调用的方法上；类内部自己调用自己时不会按期望生效 |
| `@Data` | 每个字段都手写 getter、setter、`toString`，类会很长。这是 Lombok 的注解，不把类放进容器 | 类上，`User` | 编译时生成 `getName`、`setName`、`equals`、`toString`。源文件里看不到，但 `user.getAge()` 能调用 | 又没有手写 getter 时，`user.getAge()` 编不过 | 字段要读也要写的普通数据类。加上它，`User` 仍然不是 Bean |
| `@Getter` | 只需要读，不需要改 | 类或字段上，`Gender`、`ResultCodeEnum` | 只生成 getter | 外面拿不到 `code`、`desc`，除非字段是 public | 字段是 `final`，构造之后不再改。不要用 `@Data`，否则还会生成 setter |
| `@Slf4j` | 每个类都手写一行 `LoggerFactory.getLogger(...)` | 类上，`UserService`、`UserController` | 生成一个叫 `log` 的对象，才能写 `log.info(...)` | `log` 这个变量不存在，编译失败 | 这个类要打日志 |
| `@Override` | 重写时方法名写错，编译器会以为你新加了一个方法，父类那个仍然没人实现 | 方法上，`NameUserQuery.match`。编译器看，和 Spring 无关 | 方法名、参数必须和父类或接口一致。写成 `matc` 会编译失败 | 写错名字时变成一个没用的新方法，错误更难看出来 | 实现接口或重写父类方法。`createUser` 不是重写，不用加 |
| `@Tag` | 文档里接口挤在一起，看不出属于哪一块业务 | 类上，`@Tag("用户管理")` | SpringDoc 把这个类的接口归到「用户管理」 | 接口仍能调用，文档里没有这个分组名 | 这一组接口要出现在给前端看的文档里 |
| `@Operation` | 文档标题如果只用方法名，读起来是 `createUser`，不是一句人话 | 方法上 | 给这一个接口写说明，例如「新增用户」 | 接口仍能调用，文档用方法名当标题 | 这个方法要出现在接口文档里。`main`、private 方法不用写 |

## 9. 这一周可以怎样检查

启动程序，在日志里确认两件事：

- 有一行 `1. UserService 构造器被调用`，再有一行 `2. UserService @PostConstruct 被调用`。各出现一次。
- 关闭程序时才出现 `3. UserService @PreDestroy 被调用`。

如果启动日志里还有「事务测试开始」，说明 `testTransaction()` 上的 `@PostConstruct` 还在，每次启动都会去删 id 为 1 的用户。
