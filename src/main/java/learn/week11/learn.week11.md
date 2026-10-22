# 第 11 周：用拦截器检查 token

对照代码：

- `LoginService.isValid`
- `RequestLogInterceptor.preHandle`
- `WebMvcConfig.addInterceptors`，仍然是 `addPathPatterns("/api/**")`

这一周没有改登录接口。用 Python 对 `POST /api/v1/users` 发了两次，请求体都是 `{"name":"拦截测试","age":20}`。

| 请求头 | 返回 |
|--------|------|
| 没有 `Authorization` | `{"code":401,"msg":"未登录"}` |
| `Authorization` 的值是登录返回的 UUID | `{"code":20000,"data":{"age":20,"id":2,"name":"拦截测试"},"msg":"创建用户成功"}` |

第二次用户真的被创建了，`id` 是 `2`。

## 1. token 存在哪，怎样算有效

登录成功时，`LoginService` 把 UUID 放进内存集合 `tokens`。这周加了判断：

```java
public boolean isValid(String token) {
    return token != null && tokens.contains(token);
}
```

`null`、空字符串、随便写的一串，都不在集合里，都是无效。密码正确时那次返回的 `1f02c862-cb71-4344-9882-590465934e94` 这种值，只要进程还在，就是有效的。程序重启后集合清空，旧 token 全部失效，需要重新登录。

## 2. 拦截器在进 Controller 之前检查

`RequestLogInterceptor` 仍然先打一行方法和路径。然后只对用户接口做登录检查：

```java
String prefix = "/api/" + apiVersion + "/users";
if (request.getRequestURI().startsWith(prefix)) {
    String token = request.getHeader("Authorization");
    if (!loginService.isValid(token)) {
        response.setStatus(401);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error(401, "未登录")));
        return false;
    }
}
return true;
```

`api.version` 用 `@Value` 读取，现在是 `v1`，所以前缀是 `/api/v1/users`。不要在代码里再写死一个 `v1`。

请求头的名字是 `Authorization`。值就是登录接口 `data` 里的那串 UUID，不要加 `Bearer `。

| 返回值 | 后面还会发生什么 |
|--------|------------------|
| `true` | 继续做参数解析、`@Valid`、`LogAspect` 和 Controller |
| `false` | 停在拦截器。Controller 和切面都不执行 |

`return false` 时方法没有机会返回 `Result`，所以 401 这段 JSON 要在拦截器里写进 `response`。状态码设成 401，编码设成 UTF-8，否则中文「未登录」可能乱码。

## 3. 哪些地址要登录

拦截器登记的范围仍是 `/api/**`，所以登录、上传、用户接口都会打出「拦截器：」日志。要不要 token，是靠路径前缀再判断的。

| 地址 | 会不会打拦截日志 | 要不要 token |
|------|------------------|--------------|
| `POST /api/v1/login` | 要 | 不要。路径不是 `/api/v1/users` 开头，否则还没登录就无法登录 |
| `POST /api/v1/files/upload` | 要 | 不要。这周没有保护上传 |
| `GET /api/v1/users`、`POST /api/v1/users`、`/api/v1/users/1/addresses` | 要 | 要 |
| `GET /hello` | 不要。不在 `/api/**` 里 | 不要 |

## 4. 和前两周的关系

| 周次 | 做到哪 |
|------|--------|
| 第 5 周 | 拦截器只打日志，并且 `return true` |
| 第 10 周 | 登录成功把 token 放进内存，其他接口还不看它 |
| 第 11 周 | 同一个拦截器在用户接口上调用 `isValid`。无效就 `return false` |

不带 token 时，第 5 周学过的顺序在这里断开：拦截器日志还在，`LogAspect` 的 `========` 不会出现，因为方法没有被调用。
