# 第 4 周：配置文件和多环境

对照文件：

- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`
- `com.example.crazyjava.config.EnvNameLogger`
- `com.example.crazyjava.config.AppProperties`

这一周做完的练习：用配置表示当前环境名和每页条数，不写死在 Java 里。激活 `prod` 后，启动日志是 `当前环境名：prod,每页条数：50`。

## 1. 为什么把值放到配置文件

端口、数据库地址、接口版本、每页条数，换环境就要改。写死在 Java 里，每次都要改代码再编译。放进 `application.properties`，改文件、重启，Spring 启动时读进来。

已经在用的几项：

| 配置 | 基础文件里的值 | 谁在用 |
|------|----------------|--------|
| `server.port` | `8080` | 内嵌服务器的端口 |
| `spring.datasource.url` | 库名 `test` | 连接哪一个 MySQL |
| `api.version` | `v1` | 接口地址前缀 |
| `spring.profiles.active` | 现在是 `prod` | 再额外读哪一份环境文件 |
| `app.env-name` | `base` | `AppProperties.envName` |
| `app.page-size` | `10` | `AppProperties.pageSize` |

`UserController` 的地址不是写死的 `v1`：

```java
@RequestMapping("/api/${api.version}/users")
```

`${api.version}` 在启动时换成配置里的 `v1`，实际路径是 `/api/v1/users`。改成 `v2` 并重启后，路径变成 `/api/v2/users`，Java 不用改。

## 2. 基础文件和环境文件怎么叠在一起

`application.properties` 每次启动都会读。`spring.profiles.active=prod` 表示再读 `application-prod.properties`。同名配置用环境文件里的值盖掉基础文件；环境文件里没写的项，仍用基础文件。

这次练习里实际发生过两步：

| 步骤 | `spring.profiles.active` | prod 文件里有没有 `app.env-name` | 日志 |
|------|--------------------------|-----------------------------------|------|
| 第一次 | `prod` | 没有 | `当前环境名：base`。用了基础文件 |
| 在 prod 文件加上 `app.env-name=prod` 并重启 | `prod` | 有，值是 `prod` | `当前环境名：prod` |

`application-dev.properties` 里虽然写了 `app.env-name=dev`，激活的是 `prod` 时，那份文件不会被读取。

`application-prod.properties` 里另外盖掉的项：库名是 `prod`，连接池更大，SQL 日志关掉，业务日志是 `INFO`。基础文件里的 `api.version=v1` 在 prod 文件里没有同名项，所以仍然是 `v1`。

| 文件 | 什么时候读 | 这次启动读了没有 |
|------|------------|------------------|
| `application.properties` | 每次都读 | 读了 |
| `application-dev.properties` | `spring.profiles.active=dev` 时读 | 没读 |
| `application-prod.properties` | `spring.profiles.active=prod` 时读 | 读了 |

切换环境有两种办法：

- 改基础文件里的 `spring.profiles.active`，然后重启。
- 启动参数 `--spring.profiles.active=dev`。命令行上的值盖过文件里的值，不用为了临时切换去改文件。

## 3. `@Value`：只取一个配置

`EnvNameLogger` 最初是这样读的：

```java
public EnvNameLogger(@Value("${app.env-name}") String envName) {
    log.info("当前环境名：{}", envName);
}
```

`@Value` 里的名字必须和配置文件一致，包含 `app.` 这段前缀。类要是 Bean（这里是 `@Component`），Spring 创建它时才会把值填进构造方法。

| 写法 | 配置里有这项 | 配置里没有这项 |
|------|----------------|----------------|
| `@Value("${app.env-name}")` | 注入文件里的值。有环境文件同名项时，用盖住之后的值 | 启动失败 |
| `@Value("${app.env-name:base}")` | 仍用文件里的值，冒号后面的默认值不用 | 用默认值 `base`，启动继续 |

适合只在某一个类里用到一次的单个值，例如一个开关、一个超时毫秒数。同一组配置有好几个字段时，不要写一排 `@Value`。

## 4. `@ConfigurationProperties`：一组配置装进一个对象

后来改成 `AppProperties` 装 `app` 下面的全部项，`EnvNameLogger` 只接收这一个对象：

```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String envName;
    private Integer pageSize;
    // 必须有 getter 和 setter
}
```

```java
public EnvNameLogger(AppProperties appProperties) {
    log.info("当前环境名：{},每页条数：{}",
            appProperties.getEnvName(), appProperties.getPageSize());
}
```

`prefix = "app"` 表示只绑 `app.` 开头的配置。重启后日志是 `当前环境名：prod,每页条数：50`：两个值都来自 prod 文件。

配置文件里常用短横线，Java 字段必须是驼峰。字段名里不能写短横线，`private String env-name` 会编译失败，所以代码里只能写成 `envName`。

短横线不是必须的。下面几种写法都会调用同一个 `setEnvName`，启动日志都是 `prod`：

| 配置文件里这样写 | 对应的 Java 字段 | 说明 |
|------------------|------------------|------|
| `app.env-name=prod` | `envName` | 配置文件里的习惯写法 |
| `app.envName=prod` | `envName` | 和字段名一致，也可以 |
| `app.env_name=prod` | `envName` | 下划线同样能对上 |

Spring 做成这样，是因为同一个配置会从不同地方来。环境变量通常只能写成 `APP_ENV_NAME`，properties 和命令行又常用短横线。Java 字段固定是驼峰，Spring 负责把这些写法对到同一个 setter 上。

`page-size` 也是同一规则，并且会把字符串 `50` 转成 `Integer`：

| 配置文件 | Java 字段 | 怎么填进去 |
|----------|-----------|------------|
| `app.env-name` | `envName` | 调用 `setEnvName` |
| `app.page-size` | `pageSize` | 调用 `setPageSize`，`50` 转成整数 |

没有 setter 时，Spring 填不进去，字段一直是 `null`。`@Data` 可以生成 setter，这个类手写了 getter 和 setter，效果一样。

少了某一项时，那个字段是 `null`，不会因为缺一项就启动失败。`@Value` 缺了项默认会启动失败。这是两者最要紧的差别。

## 5. `@Value` 和 `@ConfigurationProperties` 对照

| 对比项 | `@Value` | `@ConfigurationProperties` |
|--------|----------|----------------------------|
| 一次读几个 | 一个 | 同一前缀下的一组 |
| 写在哪 | 构造方法参数、字段或方法参数上 | 一个类上，`prefix` 写前缀 |
| 配置名字 | 写全名，`${app.env-name}` | 前缀只写一次 `app`，字段写 `envName` |
| 类型 | 按你声明的参数类型转，如 `String`、`int` | 按字段类型转，如 `Integer pageSize` |
| 文件里没有这项 | 启动失败，除非写成 `${app.env-name:base}` | 对应字段是 `null`，程序仍能启动 |
| 要不要 setter | 注入到构造方法参数时不要 setter | 要 setter，Spring 靠它赋值 |
| 这个项目里 | `EnvNameLogger` 曾经用它读环境名 | 现在用 `AppProperties` 同时读环境名和每页条数 |
| 什么时候用 | 只在一个地方用到的单个值 | 一组会一起用的配置，例如一批 `app.` 开头的项 |

## 6. 同一个配置，谁的优先级高

越靠前越优先。前面有值，后面的同名项就不再生效。

| 顺序 | 来源 | 例子 |
|------|------|------|
| 1 | 启动命令行 | `--server.port=9090` 或 `--spring.profiles.active=dev` |
| 2 | 当前环境文件 | `application-prod.properties` 里的 `app.page-size=50` |
| 3 | 基础文件 | `application.properties` 里的 `app.page-size=10` |
| 4 | `@Value` 冒号后面的默认值 | 只有前三处都没有这项时才用 |

所以 prod 启动时每页条数是 `50`，不是基础文件里的 `10`。若命令行再写 `--app.page-size=20`，日志里会变成 `20`，prod 文件里的 `50` 也会被盖住。

## 7. 和代码的对应

| 你想改的东西 | 改哪里 | 要不要改 Java |
|--------------|--------|----------------|
| 端口 | `server.port` | 不用。这是 Spring Boot 自己认识的配置 |
| 数据库地址 | `spring.datasource.url` | 不用 |
| 接口版本 | `api.version` | 不用。`@RequestMapping` 里的 `${api.version}` 会换掉 |
| 环境名、每页条数 | `app.env-name`、`app.page-size` | 不用。`AppProperties` 的 setter 会接到 |
| 当前用哪份环境文件 | `spring.profiles.active` 或命令行 | 不用 |

Spring Boot 已经规定好的配置（`server.port`、`spring.datasource.*`）不用自己写 `AppProperties`。只有你自己起的名字，才需要 `@Value` 或 `@ConfigurationProperties`。
