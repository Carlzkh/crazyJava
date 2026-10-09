# 继续学习计划：入门之后，把 Spring Boot 学完整

> 对象：已经手敲过本仓库代码的入门程序员。  
> 你知道自己写下的类是干什么的。这条路径不再从 `/hello` 讲起，也不去读 Spring 源码。  
> 目标：能独立做一套带配置、数据库、事务、登录、缓存和测试的后端接口，并说清每层该写什么。

建议投入：每周 8～10 小时，大约 12 周。全部加在当前项目上，不另起一个 Hello World。

## 已经掌握（本仓库里有对应代码）

这些跳过，只在后面用到时回头看：

- Java：基本类型、流程、数组、字符串、日期、集合、Lambda、Stream（`Chapter3`～`Chapter8`）
- 工程：Spring Boot 2.7、Maven、`application.properties`、多环境文件
- Web：用户模块的增删改查、分页、搜索、CSV 导出
- 分层：Controller → Service → Repository → Mapper
- 数据：MyBatis XML、动态 SQL、PageHelper、HikariCP、`Gender` 枚举和 TypeHandler
- 配套：`@Valid`、全局异常、统一 `Result`、`@Transactional`、日志切面、SpringDoc、一条 Mockito 测试

## 还不系统的部分

Java 还缺：接口与实现、继承和多态、异常体系、泛型、注解怎么读。这些是后面看懂 Spring 的基础，单独用两周补上。

Spring Boot 还缺：容器和配置、Web 上的拦截器与上传、MyBatis 关联查询、事务的常见坑、测试补全、登录、Redis。学完这些，算入门级里「能独立做接口」。

先不学：Spring 源码、自动配置原理、AOP 代理实现、MyBatis 插件机制、分布式和微服务。那些是这条路径之后的事。

## 学习方式

每周一个主题，三步：

1. 对照本项目里已有的类，弄清这个主题在你代码里的位置。
2. 补一个小功能或改一处写法，范围限制在当周主题。
3. 用接口或单测看到结果，能用自己的话讲清「代码放哪一层、为什么」。

## 12 周安排

### 第 1～2 周：Java 补洞（只补 Spring 马上要用的）

| 周 | 内容 | 在本项目里做什么 | 过关 |
|----|------|------------------|------|
| 1 | 接口、实现类、继承、多态 | 看 `UserMapper` 这种「只有方法、没有方法体」的接口；写一个很小的 `UserQuery` 接口和两个实现，在 Service 里用接口类型接收 | 能说出为什么 Mapper 写成接口，实现却不在你的 Java 文件里 |
| 2 | 异常、泛型、枚举、注解 | 顺着 `BusinessException`、`Result<T>`、`Gender` 各看一遍；分清检查异常和运行时异常；认识 `@GetMapping` 是注解，不是方法 | 能自己加一个业务错误码，并在 Service 里抛出、在全局异常里返回 |

### 第 3～4 周：Spring 容器和配置

| 周 | 内容 | 在本项目里做什么 | 过关 |
|----|------|------------------|------|
| 3 | Bean、依赖注入 | 弄清启动时 `@Service` `@Repository` `@RestController` 怎样被创建；把 `UserController`、`UserService`、`UserRepository` 改成构造器注入 | 能画出这三个类谁依赖谁，并说清字段上的 `@Autowired` 和构造器注入有什么差别 |
| 4 | 配置绑定、多环境 | `@Value` 和 `@ConfigurationProperties`；用 `application-dev` / `application-prod` 切换端口或开关，而不是把值写死在代码里 | 改一个配置项，重启后代码里读到的是新值 |

### 第 5～6 周：Web 层补全

| 周 | 内容 | 在本项目里做什么 | 过关 |
|----|------|------------------|------|
| 5 | 参数、拦截器、过滤器 | 整理你已经在用的 `@PathVariable` `@RequestParam` `@RequestBody`；写一个拦截器，给每个请求打一行日志（先不做登录） | 能说出拦截器和你现有的 `LogAspect` 各管一段：一个在进 Controller 之前，一个包着 Controller 方法 |
| 6 | 上传、下载、跨域 | 已有 CSV 导出；补一个上传接口，把文件存到本地目录；加跨域配置 | 用接口工具完成一次上传，浏览器以外的来源也能访问 |

### 第 7～8 周：数据库和事务

| 周 | 内容 | 在本项目里做什么 | 过关 |
|----|------|------------------|------|
| 7 | MyBatis 进阶 | `#{}` 和 `${}` 的差别；给用户加一张简单的地址表，写一对多查询 | 一次查询能带出用户和他的地址，SQL 参数用 `#{}` |
| 8 | 事务 | 在现有 `testTransaction` 上做实验：异常会回滚；同类里自己调用自己时，事务注解不生效。先记住现象和写法，不讲代理源码 | 能把「删除加一条记录」放进同一个事务，失败时两边一起撤销 |

### 第 9 周：测试

把 `UserServiceTest` 从「只测查询」补到新增、更新、删除和「用户不存在」。再用 MockMvc 调一条 Controller，不连接真实 MySQL。

过关：`mvn test` 通过，并且你能指出每个测试在验证哪一个分支。

### 第 10～12 周：串成一个完整模块

| 周 | 内容 | 做什么 | 过关 |
|----|------|--------|------|
| 10 | 登录 | 一个登录接口，成功后返回 token；密码先做简单校验，不引入整套 Spring Security | 错误密码进不了后面的接口 |
| 11 | 保护接口 | 用第 5 周的拦截器检查 token，`/api/v1/users` 必须登录 | 不带 token 被拒绝，带对 token 可以新增用户 |
| 12 | Redis | 按 id 查用户时先查缓存，没有再查数据库，更新和删除时清掉缓存 | 连续查两次，第二次不打到数据库；改完用户后再查是新数据 |

## 12 周之后

仍然在这个项目上扩展，每次一个主题：定时任务、`@Async`、接口文档补全、日志文件（Logback）。  
Spring Security、消息队列、微服务放到这之后，不提前塞进这 12 周。

## 参考资料（配合上面的周次，不代替动手）

- Java 补洞：廖雪峰 Java 教程里「面向对象」「异常」「泛型」几章，https://liaoxuefeng.com/books/java/index.html
- Spring 用法：Spring 官方 Guides，每个 Guide 只做和当周主题同名的那一个，https://spring.io/guides
- 查注解和配置：Spring Boot 2.7 文档，https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/
- MyBatis：官方中文文档「动态 SQL」「结果映射」，https://mybatis.org/mybatis-3/zh_CN/index.html
