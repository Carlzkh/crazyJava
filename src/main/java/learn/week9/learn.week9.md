# 第 9 周：不连数据库的测试

对照代码：

- `src/test/java/com/example/crazyjava/service/UserServiceTest.java`
- `src/test/java/com/example/crazyjava/controller/UserControllerTest.java`

这一周把原来只测查询的 `UserServiceTest` 补到了 6 条，又加了 1 条 Controller 测试。两次命令都是 `BUILD SUCCESS`：

```text
mvn "-Dtest=UserServiceTest" test
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

mvn "-Dtest=UserControllerTest" test
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

PowerShell 里 `-Dtest=...` 必须加引号，否则 Maven 收不到测试类名。

这些测试不启动 8080，也不连接 MySQL。`@PostConstruct` 不会执行，所以不会在启动时删除 id 为 1 的用户。

## 1. Service 测试里的假对象

`UserService` 真正用到数据库，是通过 `UserRepository`。测试里不想连库，就用一个假的 Repository。

| 注解 | 作用 |
|------|------|
| `@ExtendWith(MockitoExtension.class)` | 让 Mockito 创建假对象，并在每条测试前重新准备 |
| `@Mock` | `userRepository` 不是真的 Repository。没写 `when` 的方法，对象默认返回 `null`，数字默认返回 `0` |
| `@InjectMocks` | 把这个假 Repository 放进 `UserService` 的构造方法 |

日志里「构造器被调用」会出现多次：每条 `@Test` 都会新建一个 `UserService`。6 条测试就会看到 6 次。这不是去连数据库。

一条测试分三步：

| 步骤 | 做什么 | 常用方法 |
|------|--------|----------|
| 准备 | 规定假 Repository 被调用时返回什么 | `when(...).thenReturn(...)` |
| 调用 | 执行要测的那个方法 | `userService.createUser(user)` |
| 检查 | 看返回值、异常，以及假对象有没有被调用 | `assertEquals`、`assertThrows`、`verify` |

## 2. 六条 Service 测试各证明什么

| 测试方法 | 假 Repository 怎么回答 | 证明的分支 |
|----------|------------------------|------------|
| `getUserById_shouldReturnUser_whenIdExists` | `findById(1)` 返回名叫 Alice 的用户 | 查到了就原样返回，并且 `findById` 只调用 1 次 |
| `getUserById_shouldThrowBusinessException_whenUserNotFound` | `findById(999)` 返回 `null` | 抛 `BusinessException`，错误码是 `USER_NOT_FOUND` |
| `createUser_shouldThrow_whenAgeUnder18` | 不需要准备返回值 | 年龄 16 抛 `USER_AGE_TOO_YOUNG`，并且 `save` 一次都没被调用 |
| `createUser_shouldSave_whenAgeIsAdult` | `save` 是空方法，默认什么都不做 | 年龄 20 会调用 `save` 1 次。返回的是传进去的那个对象，`id` 仍是 `null`，因为假的 `save` 不会回填主键 |
| `deleteUserById_shouldDelete_whenUserExists` | `findById_` 和 `findById` 都返回这个用户，`deleteById` 返回 `1` | 两次查询都查到，删除影响了 1 行，才会真正调用删除 |
| `setUserById_shouldUpdate_whenUserExists` | `findById` 返回「旧名字、20 岁」的用户，`setById` 返回 `1` | 新姓名、新年龄被抄到原来的对象上，再拿这个对象去 `setById` |

删除测试必须把两次查询都准备好。只准备一次时，另一次默认返回 `null`，方法会提前抛「用户不存在」或「已经删除」，走不到 `deleteById`。

几个断言的差别：

| 方法 | 检查什么 |
|------|----------|
| `assertEquals` | 值相等。两个对象的内容可以相等，但不一定是同一个 |
| `assertSame` | 是不是同一个对象。成年新增测试用它，因为方法返回的就是传进去的那个 `user` |
| `assertThrows` | 调用时必须抛出指定类型的异常，并把它接住，才能再检查错误码 |
| `verify(..., times(1))` | 这个方法正好被调用了 1 次 |
| `verify(..., never())` | 这个方法一次都不能被调用。`any()` 表示不管传入的是哪个对象 |

## 3. MockMvc：伪造一次 HTTP 请求

`UserControllerTest` 测的是 Controller 把 Service 的结果包成 JSON。`UserService` 仍是假的，所以不会进 Repository，更不会进 MySQL。

它和 Service 测试的差别：

| 对比项 | `UserServiceTest` | `UserControllerTest` |
|--------|-------------------|----------------------|
| 直接调用的对象 | `UserService` | 通过 MockMvc 访问 `UserController` |
| 假对象 | `UserRepository` | `UserService` |
| 检查的内容 | Java 对象或异常 | HTTP 状态码和 JSON 字段 |
| 有没有拦截器和切面 | 没有 | 没有。没有启动整个 Spring 容器 |
| 地址里的版本号 | 不涉及 | `addPlaceholderValue("api.version", "v1")`，否则路径仍是 `/api/${api.version}/users`，对不上 `/api/v1/users/1` |

Standalone MockMvc 默认不会用项目里的 Fastjson 转换器。测试里自己放了一个 `FastJsonHttpMessageConverter`，返回的 JSON 才和运行时一样。

这条测试的过程是：

1. 假的 `getUserById(1L)` 返回名叫 Alice 的用户。
2. `mockMvc.perform(get("/api/v1/users/1"))` 伪造一次 GET。
3. `status().isOk()` 要求 HTTP 状态是 200。
4. `jsonPath("$.code")` 要求业务码是 `20000`。
5. `jsonPath("$.data.name")` 要求响应里的姓名是 `Alice`。

`$.code` 里的 `$` 表示整份 JSON，后面的点是往下取字段。`$.data.name` 就是 `data` 里面的 `name`。
