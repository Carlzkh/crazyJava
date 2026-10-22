# 第 10 周：登录

对照代码：

- `application.properties` 里的 `app.login-username`、`app.login-password`
- `AppProperties` 的 `loginUsername`、`loginPassword`
- `LoginService`
- `LoginController`：`POST /api/v1/login`
- `LoginRequest`
- `ResultCodeEnum.LOGIN_FAILED`

这一周没有引入 Spring Security，也没有拦住其他接口。用 Python 发了两次请求：

| 请求 | 返回 |
|------|------|
| 用户名 `admin`，密码 `wrong` | `{"code":50002,"msg":"登录异常"}`，没有 `data` |
| 用户名 `admin`，密码 `123456` | `{"code":20000,"data":"1f02c862-cb71-4344-9882-590465934e94","msg":"操作成功"}` |

第二次 `data` 里的那串 UUID 就是这次登录发出来的 token。

## 1. 登录接口在做什么

`LoginRequest` 只有 `username` 和 `password`。Controller 用 `@RequestBody` 接 JSON，再交给 `LoginService`。

```java
public String login(String username, String password) {
    if (!appProperties.getLoginUsername().equals(username)
            || !appProperties.getLoginPassword().equals(password)) {
        throw new BusinessException(ResultCodeEnum.LOGIN_FAILED);
    }
    String token = UUID.randomUUID().toString();
    tokens.add(token);
    return token;
}
```

用户名或密码有一个不对，就在 `UUID.randomUUID()` 之前抛异常。集合里不会留下一个无效 token。全局异常处理器把 `LOGIN_FAILED` 变成 `code=50002`、`msg=登录异常`。

密码正确时，生成一个新的 UUID，放进 `tokens`，再作为 `Result.data` 返回。每次登录成功都会得到不同的 token。程序停掉之后，这个内存里的集合就没了，之前发出的 token 也全部失效。

`tokens` 用的是 `ConcurrentHashMap.newKeySet()`。多个请求同时登录时，可以一起往集合里放，不会因为普通 `HashSet` 的并发问题把数据写坏。

## 2. 账号密码为什么先放在配置里

`application.properties` 里是：

```properties
app.login-username=admin
app.login-password=123456
```

`AppProperties` 的前缀是 `app`，所以 `app.login-username` 通过 `setLoginUsername` 填进 `loginUsername`。这是第 4 周的对应规则。

这样写只是为了本周把「校验密码、发 token」跑通，不用先改用户表。它不是正式做法。

| 做法 | 这周的代码 | 正式环境 |
|------|------------|----------|
| 账号放哪 | 配置文件里的一个固定账号 | 用户表，一行一个用户 |
| 密码怎么存 | 配置文件里的原文 `123456` | 只存摘要，不存能还原的原文 |
| 登录时怎么比 | 字符串 `equals` | 把用户刚输入的密码再算一次摘要，和库里的结果比较 |
| token 放哪 | `LoginService` 里的内存集合 | Redis，或做成带过期时间的凭证 |
| 其他接口 | 还没检查 token | 第 11 周用拦截器检查 |

摘要是单向的。数据库里只保存算出来的结果，登录时再算一次，看两个结果是否相同。常见算法是 BCrypt。同样的密码，每次算出来的摘要也可以不同。库里的内容被拿走后，不能直接拿去当密码登录。加密通常还能用密钥解回去，密钥一起泄露时，原始密码也就泄露了。密码不要做成这种可还原的密文。

## 3. 这次返回对照

| 对比项 | 密码错误 | 密码正确 |
|--------|----------|----------|
| 有没有执行 `UUID.randomUUID()` | 没有 | 有 |
| `tokens` 里会不会多一项 | 不会 | 会 |
| `code` | `50002` | `20000` |
| `data` | 没有。异常结果不带 token | 一串 UUID |
| 其他接口现在能不能用 | 能。这周还没检查 token | 能。拿到 token 也不等于接口已经被保护 |

第 11 周要在拦截器里看请求有没有带上这个 token。`POST /api/v1/login` 本身必须排除，否则还没登录就无法调用登录接口。
