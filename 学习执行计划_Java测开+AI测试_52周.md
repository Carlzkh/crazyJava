# 软件测试工程师进阶执行计划（52 周）

> **目标**：Java 测试开发 + AI 赋能测试，突破职业瓶颈  
> **前提**：你有 Python 基础，了解 Java 基本概念，当前项目为 Spring Boot Web 应用  
> **每周投入建议**：工作日 1~1.5h/天，周末 3~4h/天，共约 10~12h/周

---

## 阶段一：Java 基础硬核补齐（第 1~12 周）

### 目标
- 掌握 Java 核心语法、面向对象、集合框架、异常处理、IO、多线程基础
- 能独立写出 200 行以上的 Java 程序

### 推荐教材

| 类型 | 资源 | 链接 |
|------|------|------|
| 视频（首选） | 韩顺平《零基础30天学会Java》| https://www.bilibili.com/video/BV1fh411y7R8 |
| 视频（备选） | 尚硅谷《7天搞定Java基础》（快速版） | https://www.bilibili.com/video/BV1o841187iP |
| 书籍（首选） | 《Head First Java》（第3版） | 各大电商平台购买，或搜索 PDF 电子版 |
| 书籍（进阶参考） | 《Java 核心技术 卷I》（第12版） | 各大电商平台购买 |
| 在线练习 | LeetCode 简单题 Java 版 | https://leetcode.cn/studyplan/top-interview-150/ |
| 代码随想录 | 算法基础刷题指南 | https://github.com/youngyangyang04/leetcode-master |

---

### 第 1 周：环境搭建 + Java 初体验

| 项目 | 内容 |
|------|------|
| 学习内容 | JDK 安装、IDEA 安装配置、Hello World、变量、数据类型、运算符 |
| 学习方法 | 看韩顺平视频 P1~P30，每个视频暂停后自己敲一遍代码 |
| 每日任务 | 2~3 个视频 + 对应练习 |
| 验证方法 | ✅ 能独立在 IDEA 中创建项目并运行；✅ 能说出 8 种基本数据类型 |
| 练习 | 写一个计算器程序（加减乘除），接收用户输入 |

### 第 2 周：流程控制

| 项目 | 内容 |
|------|------|
| 学习内容 | if/else、switch、for、while、do-while、break/continue、嵌套循环 |
| 学习方法 | 视频 + 手写练习，每个语法点写 3 个小程序 |
| 验证方法 | ✅ 独立写出九九乘法表；✅ 写出冒泡排序；✅ LeetCode 完成 5 道简单题 |
| 练习 | 判断闰年、打印金字塔图案、猜数字游戏 |

### 第 3 周：数组 + 方法（函数）

| 项目 | 内容 |
|------|------|
| 学习内容 | 一维数组、二维数组、Arrays 工具类、方法定义与调用、方法重载、可变参数 |
| 学习方法 | 视频学习 + 每个知识点对应写代码验证 |
| 验证方法 | ✅ 实现数组反转、查找最大值、去重；✅ 封装 3 个工具方法并调用 |
| 练习 | 实现一个简单的学生成绩管理（数组存储+方法封装） |

### 第 4~5 周：面向对象基础

| 项目 | 内容 |
|------|------|
| 学习内容 | 类与对象、构造器、this、封装、继承、多态、super、方法重写、抽象类、接口 |
| 学习方法 | 视频 + 阅读《Head First Java》第 1~9 章 |
| 验证方法 | ✅ 能画出类的 UML 图并用代码实现；✅ 能解释多态的运行时绑定机制 |
| 练习 | 设计一个"动物王国"：Animal 基类 → Dog/Cat 子类 → 实现 Swimmable 接口 |

### 第 6 周：常用类 + 异常处理

| 项目 | 内容 |
|------|------|
| 学习内容 | String/StringBuilder/StringBuffer、日期类（LocalDate）、包装类、异常体系、try-catch-finally、自定义异常 |
| 学习方法 | 视频 + 官方 API 文档查阅练习 |
| 验证方法 | ✅ 能熟练进行字符串操作；✅ 能写 try-catch 处理文件不存在异常 |
| 练习 | 实现一个简单的"身份证号校验器"（字符串处理 + 异常抛出） |

### 第 7~8 周：集合框架（重点！）

| 项目 | 内容 |
|------|------|
| 学习内容 | List（ArrayList/LinkedList）、Set（HashSet/TreeSet）、Map（HashMap/TreeMap）、Iterator、Collections 工具类、泛型 |
| 学习方法 | 视频 + 对比写代码（同一个需求分别用不同集合实现） |
| 验证方法 | ✅ 能说出 ArrayList vs LinkedList 区别；✅ HashMap 底层原理能画图讲解；✅ LeetCode 完成 10 道集合相关题 |
| 练习 | 用 HashMap 实现一个简单的通讯录（增删改查） |

### 第 9 周：IO 流 + 文件操作

| 项目 | 内容 |
|------|------|
| 学习内容 | File 类、字节流/字符流、缓冲流、序列化、Properties 读写 |
| 学习方法 | 视频 + 实操（必须在自己电脑上实际读写文件验证） |
| 验证方法 | ✅ 能实现文件复制程序；✅ 能读取 .properties 配置文件 |
| 练习 | 写一个"日记本程序"：追加写入文件、按日期读取 |

### 第 10 周：多线程基础

| 项目 | 内容 |
|------|------|
| 学习内容 | Thread 类、Runnable 接口、线程生命周期、synchronized、线程安全集合 |
| 学习方法 | 视频 + 代码调试（用断点观察多线程执行顺序） |
| 验证方法 | ✅ 能写出生产者-消费者模型；✅ 能解释死锁原因 |
| 练习 | 模拟 3 个窗口同时售票（线程安全） |

### 第 11 周：Java 8+ 新特性

| 项目 | 内容 |
|------|------|
| 学习内容 | Lambda 表达式、Stream API、Optional、函数式接口、方法引用 |
| 学习方法 | 视频 + 将之前写的 for 循环代码用 Stream 重写 |
| 验证方法 | ✅ 能用 Stream 完成 filter/map/collect/reduce；✅ 能用 Lambda 简化代码 |
| 练习 | 用 Stream 处理一个学生列表：按成绩排序、筛选及格、计算平均分 |

### 第 12 周：阶段复习 + 综合项目

| 项目 | 内容 |
|------|------|
| 学习内容 | 复习全部知识点，完成一个综合项目 |
| 综合项目 | **学生管理系统（控制台版）**：集合存储、文件持久化、异常处理、多线程模拟并发 |
| 验证方法 | ✅ 项目可正常运行；✅ 代码上传到 GitHub；✅ 能向别人讲解整体设计 |
| 里程碑 | 🎯 完成 LeetCode 30+ 简单题（Java 版） |

---

## 阶段二：Spring Boot 源码级理解（第 13~24 周）

### 目标
- 读懂你们项目的 Controller → Service → DAO 全链路
- 能独立用 Spring Boot 写一个 CRUD 接口

### 推荐教材

| 类型 | 资源 | 链接 |
|------|------|------|
| 视频（首选） | 黑马程序员 SpringBoot3 + Vue3 全套 | https://www.bilibili.com/video/BV14z4y1N7pg |
| 视频（备选） | 尚硅谷 SpringBoot2 零基础入门 | https://www.bilibili.com/video/BV19K4y1L7MT |
| 视频（Spring 基础） | 黑马 Spring 零基础入门到精通 | https://www.bilibili.com/video/BV1rt4y1u7q5 |
| 书籍 | 《Spring Boot 实战》Craig Walls | 各大电商平台 |
| 文档 | Spring Boot 官方文档 | https://docs.spring.io/spring-boot/docs/current/reference/html/ |

---

### 第 13~14 周：Maven + 项目工程结构

| 项目 | 内容 |
|------|------|
| 学习内容 | Maven 核心概念（POM、依赖管理、生命周期）、项目目录结构（src/main/java, resources, test） |
| 学习方法 | 视频 + 对照你们项目的 pom.xml 逐行理解 |
| 验证方法 | ✅ 能看懂项目 pom.xml 的每个依赖作用；✅ 能用 Maven 命令编译打包 |
| 实践 | 用 Maven 创建一个空的 Spring Boot 项目，成功启动 |

### 第 15~16 周：Spring 核心（IoC + AOP）

| 项目 | 内容 |
|------|------|
| 学习内容 | IoC 容器、Bean 生命周期、依赖注入（@Autowired/@Resource）、AOP 概念与实现 |
| 学习方法 | 看黑马 Spring 视频 + 在项目中找到对应注解标记并理解 |
| 验证方法 | ✅ 能画出 Bean 注入关系图；✅ 能写一个切面拦截日志；✅ 能解释 @Component vs @Service vs @Repository |
| 实践 | 在你自己的练习项目中实现一个 AOP 日志切面 |

### 第 17~18 周：Spring Boot Web 开发

| 项目 | 内容 |
|------|------|
| 学习内容 | @RestController、@RequestMapping、请求参数绑定（@RequestParam/@RequestBody/@PathVariable）、响应封装、统一异常处理 |
| 学习方法 | 视频 + 用 Postman 调试自己写的接口 |
| 验证方法 | ✅ 能写出 GET/POST/PUT/DELETE 四种接口；✅ 能用全局异常处理器统一返回格式 |
| 实践 | 写一套"用户管理"RESTful API（不连数据库，用 List 存储） |

### 第 19~20 周：数据库 + MyBatis/MyBatis-Plus

| 项目 | 内容 |
|------|------|
| 学习内容 | MySQL 基础（DDL/DML/DQL）、MyBatis 配置与映射、MyBatis-Plus 快速 CRUD、分页查询 |
| 学习方法 | 视频 + 对照项目中的 Mapper.xml / Mapper 接口逐行理解 |
| 验证方法 | ✅ 能看懂项目中的 SQL 映射；✅ 能独立写 CRUD + 联表查询 |
| 实践 | 将"用户管理"API 连接真实数据库，完成完整 CRUD |

### 第 21~22 周：项目实战 — 读懂你们的项目

| 项目 | 内容 |
|------|------|
| 学习内容 | 跟着项目代码，从 Controller → Service → Mapper 走完一个完整业务流程 |
| 学习方法 | **选取项目中 3 个接口**，从入口到 SQL 完整 debug 跟踪 |
| 验证方法 | ✅ 能画出 3 个接口的时序图；✅ 能说出每个注解的作用；✅ 能找到业务逻辑中的潜在 bug 点 |
| 产出 | 整理一份"接口分析笔记"，记录每个接口的入参、校验、业务逻辑、返回值 |

### 第 23~24 周：进阶 — 项目中的常见模式

| 项目 | 内容 |
|------|------|
| 学习内容 | 拦截器/过滤器、Spring Security 基础（认证/授权）、定时任务、消息队列概念 |
| 学习方法 | 视频 + 查看项目中是否使用了这些组件 |
| 验证方法 | ✅ 能解释登录认证流程；✅ 能添加一个自定义拦截器 |
| 里程碑 | 🎯 能独立看懂项目 80% 的代码逻辑，知道每个类的职责 |

---

## 阶段三：Java 测试开发实战（第 25~38 周）

### 目标
- 掌握 JUnit5 + Mockito + REST Assured
- 能为项目写单元测试和接口自动化测试
- 能搭建一个测试框架

### 推荐教材

| 类型 | 资源 | 链接 |
|------|------|------|
| 视频 | REST Assured 接口测试实战 | https://www.bilibili.com/video/BV1wx41197xR |
| 视频 | JUnit5 官方教程 | https://junit.org/junit5/docs/current/user-guide/ |
| 视频 | Mockito 官方文档 | https://site.mockito.org/ |
| 书籍 | 《Java 测试驱动开发》| 各大电商平台 |
| GitHub | REST Assured 官方示例 | https://github.com/rest-assured/rest-assured |
| GitHub | Spring Boot 测试最佳实践 | https://github.com/springdoc/springdoc-openapi |

---

### 第 25~26 周：JUnit 5 单元测试

| 项目 | 内容 |
|------|------|
| 学习内容 | @Test、@BeforeEach/@AfterEach、断言（assertEquals/assertTrue/assertThrows）、@ParameterizedTest、@Nested、@DisplayName |
| 学习方法 | 官方文档 + 直接为你的练习项目 Service 层写测试 |
| 验证方法 | ✅ 为 5 个方法写完整单测；✅ 参数化测试覆盖边界值；✅ 测试覆盖率 > 80% |
| 实践 | 在你的 Spring Boot 练习项目中添加 test 目录，为 Service 层写测试 |

### 第 27~28 周：Mockito 模拟测试

| 项目 | 内容 |
|------|------|
| 学习内容 | @Mock、@InjectMocks、when().thenReturn()、verify()、ArgumentCaptor、doThrow() |
| 学习方法 | Mockito 官方文档 + 为 Service 层 mock 掉 DAO 依赖 |
| 验证方法 | ✅ 能 mock 数据库依赖做隔离测试；✅ 能验证方法调用次数和参数 |
| 实践 | 为"用户管理"Service 的增删改查写 Mock 测试（不依赖真实数据库） |

### 第 29~30 周：Spring Boot Test 集成测试

| 项目 | 内容 |
|------|------|
| 学习内容 | @SpringBootTest、@WebMvcTest、MockMvc、@DataJpaTest、测试配置隔离、H2 内存数据库测试 |
| 学习方法 | Spring 官方测试文档 + 实操 |
| 验证方法 | ✅ 能用 MockMvc 测试 Controller 层；✅ 能用 H2 数据库做集成测试 |
| 实践 | 写完整的分层测试：Controller 层（MockMvc）→ Service 层（Mockito）→ Repository 层（H2） |

### 第 31~33 周：REST Assured 接口自动化

| 项目 | 内容 |
|------|------|
| 学习内容 | REST Assured 基础语法（given/when/then）、请求构造、响应断言、JSON 解析、认证处理、数据驱动 |
| 学习方法 | 视频 + 官方 GitHub 示例 + 对你们项目真实接口进行测试 |
| 验证方法 | ✅ 能对 10 个真实接口编写自动化测试；✅ 能处理 Token 认证；✅ 测试报告生成（Allure） |
| 实践 | **核心产出**：用 REST Assured 对你们 eCollect 项目的 5 个核心 API 写自动化用例 |

### 第 34~35 周：测试框架搭建

| 项目 | 内容 |
|------|------|
| 学习内容 | 测试框架分层设计、配置管理（多环境）、数据驱动（Excel/YAML）、测试报告（Allure）、CI 集成 |
| 学习方法 | 参考开源项目结构 + 自己动手搭建 |
| 验证方法 | ✅ 框架支持多环境切换；✅ 用例数据与代码分离；✅ 能生成 Allure 报告 |
| 产出 | 一个可复用的 Java 接口自动化测试框架（上传 GitHub） |

### 第 36~38 周：应用到真实项目

| 项目 | 内容 |
|------|------|
| 学习内容 | 将框架应用到你们 eCollect 项目，覆盖核心业务场景 |
| 验证方法 | ✅ 覆盖 20+ 个接口；✅ 集成到 CI/CD 流水线；✅ 每次提测自动跑一轮 |
| 里程碑 | 🎯 具备"Java 测试开发工程师"的核心技能，可以在组内做技术分享 |

---

## 阶段四：Python + AI 赋能测试（第 39~52 周）

### 目标
- 用 Python 构建 AI 辅助测试工具
- 掌握 LLM + Agent 在测试领域的应用
- 能做"AI 智能测试"方向的技术分享或工具产出

### 推荐教材

| 类型 | 资源 | 链接 |
|------|------|------|
| 视频 | Python 自动化测试（pytest） | https://www.bilibili.com/video/BV1YjXMYUEci |
| 视频 | Playwright 自动化测试 | https://www.bilibili.com/video/BV1VSh9zqEAw |
| 视频 | LangChain 快速入门到实战 | https://www.bilibili.com/video/BV1BgfBYoEpQ |
| 视频 | AI Agent 从 0 到 1 开发 | https://www.bilibili.com/video/BV11mWXzeEJh |
| 文章 | LangChain 构建测试智能体 | https://cloud.tencent.com/developer/article/2629605 |
| 文章 | LangChain 自动化测试用例生成 | https://www.cnblogs.com/hogwarts/p/18436123 |
| GitHub | LangChain 官方仓库 | https://github.com/langchain-ai/langchain |
| 文档 | LangChain 官方文档 | https://python.langchain.com/docs/get_started/introduction |

---

### 第 39~40 周：Python 测试基础强化

| 项目 | 内容 |
|------|------|
| 学习内容 | pytest 框架（fixture、参数化、标记、插件）、requests 库、数据驱动 |
| 学习方法 | B 站 pytest 教程 + 官方文档 + 对你们项目接口写 Python 版测试 |
| 验证方法 | ✅ 用 pytest 写 10 个接口测试用例；✅ 生成 HTML 测试报告 |
| 实践 | 同一组接口，分别用 Java（REST Assured）和 Python（pytest+requests）实现，对比 |

### 第 41~42 周：Playwright UI 自动化

| 项目 | 内容 |
|------|------|
| 学习内容 | Playwright 安装、元素定位、页面操作、等待机制、截图、录制、POM 模式 |
| 学习方法 | B 站视频 + 官方文档 + 对你们 eCollect Web 页面写 UI 测试 |
| 验证方法 | ✅ 完成 5 个页面的 UI 自动化测试；✅ 实现 POM 分层；✅ 截图对比 |
| 实践 | 用 Playwright 实现 eCollect 系统的登录 → 数据采集 → 导出全流程 |

### 第 43~44 周：Python + LLM 基础

| 项目 | 内容 |
|------|------|
| 学习内容 | OpenAI API / 国产大模型 API 调用、Prompt Engineering 基础、温度/Token 概念 |
| 学习方法 | 官方文档 + 动手调用 API + 测试不同 Prompt 效果 |
| 验证方法 | ✅ 能调用 LLM API 生成测试用例描述；✅ 能设计好的 Prompt 模板 |
| 实践 | 写一个脚本：输入需求描述 → LLM 输出测试用例（文本格式） |

### 第 45~47 周：LangChain 实战

| 项目 | 内容 |
|------|------|
| 学习内容 | LangChain 核心概念（Chain、Agent、Tool、Memory）、RAG（检索增强生成）、向量数据库 |
| 学习方法 | B 站 LangChain 教程 + 官方文档 + 实操 |
| 验证方法 | ✅ 能构建一个 RAG 系统（读取你们的需求文档生成测试用例）；✅ 能创建一个 Agent 自动执行多步操作 |
| 实践 | **核心产出**：构建"AI 测试用例生成器" — 输入需求 PDF → 自动生成结构化测试用例 |

### 第 48~50 周：AI 测试智能体（Agent）

| 项目 | 内容 |
|------|------|
| 学习内容 | AI Agent 架构设计、Tool 定义、多 Agent 协作、LangGraph 工作流 |
| 学习方法 | 视频 + 官方示例 + 基于你的 test-studio 项目扩展 |
| 验证方法 | ✅ Agent 能自动分析需求 → 生成用例 → 验证覆盖率；✅ 能与实际测试流程结合 |
| 实践 | 在 test-studio 项目中实现完整的 AI 测试 workflow |

### 第 51~52 周：总结 + 作品集 + 职业行动

| 项目 | 内容 |
|------|------|
| 整理作品 | ① Java 接口自动化框架（GitHub）② AI 测试用例生成工具（GitHub）③ 技术博客 5+ 篇 |
| 技术分享 | 在团队内做一次"AI 赋能测试"主题分享 |
| 简历更新 | 更新简历，突出"Java 测试开发 + AI 测试"双技能线 |
| 职业行动 | 投递"测试开发工程师"岗位，或在现有岗位争取晋升/转岗 |
| 里程碑 | 🎯 具备"高级测试开发工程师"能力画像 |

---

## 日常习惯建议

| 习惯 | 说明 |
|------|------|
| 🕐 固定时间 | 每天早起 1h 或晚上 9:00~10:30 固定学习 |
| 📝 输出倒逼输入 | 每周写一篇学习笔记（可以是 CSDN/掘金/语雀） |
| 💻 代码优先 | 视频看完必须自己敲，不敲等于没看 |
| 🔁 间隔复习 | 每周六花 30 分钟回顾本周知识点 |
| 🎯 项目驱动 | 所有学习内容尽量围绕你的真实项目应用 |
| 👥 找同伴 | 加入技术社群（如 LeetCode 打卡群、测试开发交流群） |

---

## 关键验收节点

| 时间 | 里程碑 | 可量化标准 |
|------|--------|-----------|
| 第 12 周 | Java 基础通关 | LeetCode 30 题 + 完整项目 |
| 第 24 周 | 能读懂项目代码 | 画出 3 个核心接口时序图 |
| 第 38 周 | 测试开发能力成型 | 自建框架 + 20 个自动化用例在项目中跑通 |
| 第 52 周 | AI 测试方向产出 | AI 测试工具 + 技术分享 + 简历更新 |

---

## 注意事项

1. **不要贪多**：每周严格按计划推进，宁可一个知识点扎实也不要走马观花
2. **允许延期**：如果某周内容没消化，允许顺延 1 周，但不超过 2 周
3. **关联项目**：尽量把学到的东西用到你的 eCollect 项目中，学以致用
4. **记录问题**：遇到不懂的记录下来，可以在社区提问或找我讨论
5. **定期复盘**：每月底做一次月度复盘，调整下月计划

---

> 💡 **最终目标画像**：一个既能用 Java 深度参与项目开发测试、又能用 Python + AI 构建智能测试工具的**全栈测试开发工程师**。
