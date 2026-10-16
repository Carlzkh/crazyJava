# 第 5 周：拦截器、过滤器和切面

对照代码：

- `com.example.crazyjava.config.RequestLogInterceptor`
- `com.example.crazyjava.config.WebMvcConfig` 里的 `addInterceptors`
- `com.example.crazyjava.aop.LogAspect`

这一周做完的练习：给 `/api/**` 加了一个拦截器，每个接口请求先打一行日志。访问 `GET /api/v1/users` 时，日志顺序是：

```text
拦截器：GET /api/v1/users
==========================================
请求 URL   : http://localhost:8080/api/v1/users
请求方法   : GET
请求 IP    : 0:0:0:0:0:0:0:1
请求参数   : {}
请求 Body  : 1
```

拦截器在前，切面在后。

## 1. 一次请求要经过谁

外部请求要进到 `UserController` 的方法，中间有几道已经能对上的关卡。

| 顺序 | 谁 | 这个项目里 | 这时方法执行了没有 | 能把请求停掉吗 |
|------|----|------------|--------------------|----------------|
| 1 | 过滤器 `Filter` | 还没写 | 还没找到具体方法 | 能。不调用后面的链，请求就停 |
| 2 | 拦截器 `preHandle` | `RequestLogInterceptor` | 还没有 | 能。返回 `false` 就停 |
| 3 | 参数解析和 `@Valid` | `createUser` 上的 `@RequestBody`、`@Valid` | 还没有 | 校验失败会抛异常，方法体不执行 |
| 4 | 切面 `LogAspect` | `@Around` 包着 controller 包的方法 | 正要执行 | 能。不调用 `joinPoint.proceed()` 就不会进方法 |
| 5 | Controller 方法 | 例如分页查询、`createUser` | 正在执行 | 方法自己 `return` 或抛异常 |

姓名是空字符串时：拦截器那一行仍然会出现，因为第 2 步在校验之前。`LogAspect` 的 `========` 不会出现，因为第 3 步校验失败，方法没被调用，包着方法的切面不会开始。

## 2. 过滤器和拦截器、切面对照

| 对比项 | 过滤器 `Filter` | 拦截器 `HandlerInterceptor` | 切面 `LogAspect` |
|--------|-----------------|-----------------------------|------------------|
| 属于哪一层 | Servlet。Spring MVC 还没开始分发 | Spring MVC。已经找到要调用的方法 | 包着某个 Java 方法 |
| 这个项目写了没有 | 没有 | `RequestLogInterceptor` | 有，`@Around` |
| 什么时候运行 | 最早。静态资源、找不到的地址也会经过 | 找到 Controller 方法之后、执行方法之前 | 真正调用 Controller 方法时 |
| 看得到什么 | 原始请求和响应 | 方法、路径。还看不到已经转好的 `User` 对象 | 方法参数和返回值。`joinPoint.proceed()` 前后都能写代码 |
| 校验失败时 | 会经过 | `preHandle` 会执行 | 不会执行。方法没被调用 |
| 怎么停掉请求 | 不把请求交给后面的链 | `preHandle` 返回 `false` | 不调用 `proceed()` |
| 适合做什么 | 所有请求都要做的事，例如统一编码 | 一组接口的门槛，例如以后检查登录 | 记录某个方法的参数、返回值和耗时 |
| 注册方式 | 自己写成 `Filter` 并交给容器，或用 `FilterRegistrationBean` | 在 `WebMvcConfig.addInterceptors` 里登记 | 类上有 `@Aspect` 和 `@Component`，不用登记路径 |

三者不要叠成三份一样的访问日志。现在拦截器只打一行「方法 + 路径」，切面打参数、返回值和耗时，各看一段。

## 3. 拦截器怎么接到项目里

`RequestLogInterceptor` 实现 `HandlerInterceptor`，并加上 `@Component`，启动时容器里才有这一个对象。

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info("拦截器：{} {}", request.getMethod(), request.getRequestURI());
    return true;
}
```

`WebMvcConfig` 已经是 `WebMvcConfigurer`，在这里登记，不要再新建一个配置类：

```java
@Autowired
private RequestLogInterceptor requestLogInterceptor;

@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestLogInterceptor)
            .addPathPatterns("/api/**");
}
```

| 代码 | 作用 |
|------|------|
| `return true` | 放行。后面的校验、切面、Controller 继续走 |
| `return false` | 停在拦截器。后面都不走。这时通常要自己往 `response` 里写错误内容，否则调用方只看到空响应 |
| `addPathPatterns("/api/**")` | 只拦以 `/api/` 开头的地址 |
| 不写 `addPathPatterns` | 默认拦所有进入 Spring MVC 的请求 |

`/api/**` 里的 `**` 表示多段路径，所以 `/api/v1/users` 和 `/api/v1/users/3` 都会被拦。`/hello` 不在这个范围内，访问它不会打出「拦截器：」那一行。

还可以用 `excludePathPatterns` 排除个别地址。例如以后登录接口本身不能要求已经登录：

```java
registry.addInterceptor(requestLogInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns("/api/v1/login");
```

这次没有排除任何路径。

## 4. 拦截器的三个方法

这次只写了 `preHandle`。接口上还有两个方法，不写也有默认空实现。

| 方法 | 什么时候调用 | 返回值 | 这次用了没有 |
|------|----------------|--------|----------------|
| `preHandle` | 方法执行前 | `true` 放行，`false` 停下 | 用了，打方法和路径 |
| `postHandle` | Controller 方法已经返回，响应还没提交完 | 无 | 没有。适合临时改 `Model`，这个项目返回 JSON，很少用 |
| `afterCompletion` | 整个请求结束，无论方法成功还是中途抛异常 | 无 | 没有。适合记耗时、清理本次请求放进去的数据 |

`preHandle` 返回 `false` 时，不会再调用 `postHandle`。`afterCompletion` 只对已经在 `preHandle` 里返回过 `true` 的拦截器调用。

多个拦截器时，`preHandle` 按注册顺序执行，`postHandle` 和 `afterCompletion` 按相反顺序执行。现在只有一个，看不出这个差别。

## 5. 这次日志里容易看错的三行

切面里这几行不是字面意思。

| 日志 | 实际意思 |
|------|----------|
| `请求参数 : {}` | 问号后面的参数。这次是 `GET /api/v1/users`，没有 `?page=1`，所以是空对象。加上 `?name=张` 后，这里才会有内容 |
| `请求 Body : 1` | 这个 GET 没有请求体。`LogAspect.getRequestParams` 把 Controller 方法的第一个普通参数拿去序列化。分页方法第一个参数是 `page`，默认值是 `1` |
| `请求 IP : 0:0:0:0:0:0:0:1` | 本机 IPv6，就是 `localhost`。不是来自外网 |

`getRequestParams` 的循环会跳过 `HttpServletRequest` 和 `HttpServletResponse`，然后把第一个剩下的参数变成 JSON。POST 新增用户时，第一个剩下的参数才是 `User`，那一行才会像请求体。GET 分页时，它打出来的是页码，不是 Body。

## 6. 和登录的关系

这次拦截器只打日志，不检查登录。以后要保护 `/api/**`，就在 `preHandle` 里看请求有没有合法 token：

| 情况 | `preHandle` 怎么做 |
|------|---------------------|
| 有合法 token | 返回 `true`，继续进 Controller |
| 没有或 token 不对 | 往响应里写未登录的 `Result`，返回 `false` |
| 登录接口自己 | 用 `excludePathPatterns` 排除，否则登录请求也会被拦住 |

登录接口和第 8 周的保护还没做。过滤器仍然不需要为了打一行日志而写。
