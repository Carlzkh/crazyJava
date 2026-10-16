# Java 基础语法

对照代码在 `src/main/java/Chapter3` 到 `Chapter8`。面向对象里接口、继承、构造方法注入，见 `learn/week1`、`learn/week3`。异常、泛型见 `learn/week2`。

## 1. 基本类型

8 种基本类型存在变量自己里面，不是对象。`Chapter3/BaseDataType.java` 里的注释就是这张表。

| 类型 | 占几个字节 | 用来存什么 | 写法注意 |
|------|------------|------------|----------|
| `byte` | 1 | 很小的整数，-128 到 127 | 文件、字节流里常见 |
| `short` | 2 | 较小的整数 | 日常很少直接用 |
| `int` | 4 | 最常用的整数 | 整数默认就是 `int`，如 `6` |
| `long` | 8 | 很大的整数、时间戳毫秒 | 字面量必须加 `L`，如 `999999999999999999L`。不加 `L`，数字太大时编译失败 |
| `float` | 4 | 单精度小数 | 字面量加 `f`，如 `65.0f`。小数默认是 `double` |
| `double` | 8 | 双精度小数 | 钱不要用它做精确计算，会有二进制误差 |
| `char` | 2 | 一个字符 | 用单引号 `'A'`。也可以当整数，`'A'` 的值是 65 |
| `boolean` | 没有规定成固定字节 | 只有 `true` / `false` | 不能写成 `1` / `0` |

数字里可以加下划线方便阅读，编译后下划线消失。`Chapter3/UnderscoreTest.java` 练的就是这个，例如 `1_000_000`。

包装类型是对应的类：`Integer`、`Long`、`Double`、`Boolean`、`Character`。集合的尖括号里只能写包装类型，`List<int>` 不行，要写 `List<Integer>`。字段允许「没有值」时用包装类型，`User.age` 是 `Integer`，可以是 `null`；基本类型 `int` 没有 `null`，默认是 `0`。

## 2. 自动转换和强制转换

小范围的数值可以自动变成大范围，不用写括号。`Chapter3/AutoConversion.java` 里 `int a = 6; float f = a;` 能通过。

自动变大的两条路：

| 起点 | 可以自动变成 |
|------|----------------|
| `char` | `int` → `long` → `float` → `double` |
| `byte` | `short` → `int` → `long` → `float` → `double` |

大范围塞回小范围必须强制转换，可能丢掉小数或超出的部分。`Chapter3/NarrowConversion.java` 练的是这个，写法是 `int n = (int) 3.9;`，结果是 `3`。

运算时，较小的类型会先抬到参与运算的较大类型。`Chapter3/AutoPromote.java` 里，`byte`、`short`、`char` 做加法时会先变成 `int`。

`Chapter3/PrimitiveAndString.java` 是数值和字符串互转：`String.valueOf(3)` 得到 `"3"`，`Integer.parseInt("3")` 得到 `3`。字符串不是数字时，`parseInt` 抛 `NumberFormatException`。

## 3. 变量、字面量和运算符

| 概念 | 是什么 | 例子 |
|------|--------|------|
| 字面量 | 源码里直接写出的值 | `6`、`65.0f`、`'A'`、`"Hello"`、`true` |
| 局部变量 | 方法里声明的变量 | `main` 里的 `int a = 6`。用之前必须赋值 |
| 成员变量 | 类里、方法外的字段 | `User.name`。对象字段有默认值，数值是 `0`，对象是 `null`，`boolean` 是 `false` |
| `final` 变量 | 只能赋一次 | `NameUserQuery` 里的 `private final String name` |

常用运算符：`+ - * / %`，比较 `== != > >= < <=`，逻辑 `&& || !`。`&&` 和 `||` 会短路：`user.getAge() != null && user.getAge() < 18` 里，年龄是 `null` 时后面的 `< 18` 不会执行，避免空指针。

字符串的 `+` 是拼接，不是数学加法。`"年龄" + 18` 得到 `"年龄18"`。

## 4. 流程控制

| 语句 | 干什么 | 你项目里的例子 |
|------|--------|----------------|
| `if` / `else` | 条件成立才执行 | `createUser` 里年龄小于 18 就抛异常 |
| `for` | 次数明确的循环 | `Chapter4/exec/NineNineTable.java` 的九九乘法表 |
| 增强 `for` | 把数组或集合里的元素逐个拿出来 | `for (User user : users)` |
| `while` / `do-while` | 条件成立就继续。`do-while` 至少执行一次 | 次数事先不知道时用 |
| `break` | 结束循环 | `Chapter4/BreakAndContinue.java` |
| `continue` | 跳过本轮剩余语句，进入下一轮 | 同上 |
| `switch` | 一个变量对上多个固定值 | 适合枚举、少量整数或字符串 |

`break` 和 `continue` 默认只影响离它最近的那一层循环。要跳出外层，给外层起一个名字，再 `break 名字`。`BreakAndContinue.java` 里外层叫 `outer`，`break outer` 会把两层一起结束。

## 5. 数组

数组长度创建后不能变。`Chapter4/ArrayTest.java`、`ArraysTest.java`、`ArraysDemo.java`、`ForEachTest.java` 都在练它。

```java
int[] nums = new int[3];          // 3 个元素，默认都是 0
String[] names = {"张三", "李四"}; // 长度是 2，下标从 0 开始
names[0]                       // 张三
```

下标从 `0` 到 `长度 - 1`。`names[2]` 会抛 `ArrayIndexOutOfBoundsException`。

`java.util.Arrays` 是工具类，不是把数组变成集合：

| 方法 | 干什么 |
|------|--------|
| `Arrays.toString(a)` | 打印成 `[a, b]`，不要直接打印数组变量 |
| `Arrays.sort(a)` | 排序 |
| `Arrays.fill(a, "*")` | 每个位置填同一个值。九九乘法表那个文件里用来画星号 |
| `Arrays.asList("1", "2")` | 得到一个固定长度的 List，不能再 `add` 新元素 |

数组长度固定。个数会变时用 `ArrayList`，见第 10 节。

## 6. 方法、重载、可变参数

方法是类里的一段可调用代码。签名是：修饰符、返回类型、方法名、参数列表。没有返回值就写 `void`。

`Chapter5/Overload.java` 是重载：同一个类里方法名相同，参数不同。调用时按参数决定跑哪一个。

```java
public void test() { }
public void test(String msg) { }
public void test(String... messages) { }
```

`q.test()` 走无参。`q.test("呵呵")` 走单个 `String`。`q.test("he1", "he2")` 走可变参数。

`String... messages` 表示 0 到多个 `String`，方法里把它当 `String[]` 用。重载和重写不是一回事：重载看参数，在同一个类里；重写是子类改父类已有的方法，方法名和参数都要一样，用 `@Override`。

## 7. 面向对象：类、对象、封装

类是模板，对象是 `new` 出来的一份。`new BaseDataType()` 得到一个对象，可以调用它的 `tst11()`。

| 概念 | 是什么 | 例子 |
|------|--------|------|
| 类 | 规定有哪些字段和方法 | `class User` |
| 对象 | 按类创建出来的一份数据 | `new User()`，再 `setName("张三")` |
| 字段 | 对象自己保存的数据 | `name`、`age` |
| 方法 | 对象能做的事 | `getAge()`、`createUser` |
| 构造方法 | 名字和类名相同，没有返回类型。`new` 时执行 | `NameUserQuery(String name)` |
| `this` | 当前这个对象。参数和字段同名时用 `this.name = name` | `NameUserQuery` 的构造方法 |
| 封装 | 字段设为 `private`，外面用方法读写 | `User` 的字段是 private，`@Data` 生成 getter、setter |

不写构造方法时，编译器补一个无参构造。一旦自己写了带参数的构造方法，无参构造就没有了。`NameUserQuery` 因此不能 `new NameUserQuery()`。

一个 `.java` 文件里只能有一个 `public` 类，文件名必须和这个类名相同。

`static` 属于类，不属于某一个对象。`main` 必须是 `static`，因为程序启动时还没有任何对象。`static` 方法里没有 `this`，不能直接用非静态字段。`Week1Main.createUser` 是 `private static`，`main` 里可以直接调用。

访问范围：

| 修饰符 | 谁能用 |
|--------|--------|
| `public` | 任何类 |
| `protected` | 本类和子类 |
| 不写 | 只有同一个包 |
| `private` | 只有本类 |

## 8. 继承、多态、接口、抽象类

这一块的练习主要在 `learn/week1`，不在 Chapter 里。Chapter6 练了 `equals`、`toString` 和单例。

| 概念 | 写法 | 解决什么问题 |
|------|------|----------------|
| 继承 | `class AdultUserQuery extends AbstractUserQuery` | 子类复用父类的循环，只写自己的 `match`。一个类只能 `extends` 一个父类 |
| 实现接口 | `class AbstractUserQuery implements UserQuery` | 调用方只依赖 `query` 方法，可以换成不同实现。一个类可以实现多个接口 |
| 抽象类 | `abstract class`，可以有没写完的方法 | 父类自己不完整，禁止 `new AbstractUserQuery()` |
| 多态 | `UserQuery query = new NameUserQuery("张")` | 变量看左边能调用哪些方法，真正执行的 `match` 看右边的对象 |
| 重写 | 子类方法名、参数与父类一致，加 `@Override` | 父类循环调用 `match` 时，跑的是子类里的判断 |

所有类最终都继承 `Object`。不写 `extends` 时，父类就是 `Object`。`equals`、`toString` 定义在 `Object` 上。

`Chapter6/PrintObject.java` 重写了 `toString`。`record Person(String name)` 是一种简短的数据类，字段、构造方法、`equals` 会按组件生成；你又重写了 `toString`，打印时用你写的那句。

## 9. `==` 和 `equals`

`Chapter6/EqualTest.java` 的结论：

| 比较 | `==` | `equals` |
|------|------|----------|
| 基本类型 | 比数值。`65`、`65.0f`、`'A'` 互相 `==` 都是 true | 基本类型没有 `equals` |
| 引用类型 | 是不是同一个对象 | 默认仍是比是不是同一个对象。`String` 重写后比的是里面的字符 |

`"Hello"` 和另一个 `"Hello"` 可能指向常量池里的同一份，`==` 是 true。`new String("Hello")` 每次都是新对象，两个 `new` 出来的 `==` 是 false，`equals` 仍是 true。

`"疯狂" + "Java"` 在编译期就能拼成常量，可能和 `"疯狂Java"` 是同一个对象。变量拼接 `s2 + s3` 要到运行时才知道结果，是新对象，`==` 为 false，`equals` 为 true。

比较两个用户的姓名是否相同，用 `equals`，不要用 `==`。比较两个枚举常量可以用 `==`，因为每个枚举常量只有一个对象。`equals` 调用前要先确认左边不是 `null`，否则空指针。

## 10. String 和 StringBuilder

`String` 创建后内容不能改。看起来在改的方法，其实是返回一个新字符串。`Chapter7/TestString.java` 练了这些：

| 方法 | 干什么 | 例子 |
|------|--------|------|
| `charAt(i)` | 取下标 `i` 的字符，从 0 开始 | `"fkit.org".charAt(1)` 是 `k` |
| `length()` | 字符个数 | 不是字节数 |
| `equals` | 内容是否相同 | 不要用 `==` 比内容 |
| `compareTo` | 按字典序比较。相同返回 0 | 第一个不同字符的差值；一方是另一方的开头时，返回长度差 |
| `indexOf` | 第一次出现的位置，没有返回 -1 | `indexOf('r')`，或从某个下标再找 |
| `substring` | 截取，含开头、不含结尾 | |
| `contains` | 是否包含这段字符 | `NameUserQuery` 用它判断姓名里有没有关键字 |
| `trim` | 去掉两端空白 | |

很多次拼接不要用 `+` 在循环里堆，每次都会产生新字符串。`Chapter7/StringBuilderTest.java`、`TestSB.java` 用的是 `StringBuilder`：在同一个对象上 `append`，最后 `toString()`。`StringBuilder` 不是线程安全的；多线程共用一份拼接结果时用 `StringBuffer`。普通接口拼一行响应用 `StringBuilder` 就够。

## 11. 时间

项目里新旧两套都出现过。新代码用 `java.time`，不要再新写 `Date` 和 `Calendar`。

| 类 | 包 | 表示什么 | 你代码里的例子 | 要注意 |
|----|----|----------|----------------|--------|
| `Date` | `java.util` | 一个时间点，旧 API | `BaseDataType` 里 Hutool 的 `DateUtil.parseDateTime` | 很多方法已不推荐。月份、格式化容易踩坑 |
| `Calendar` | `java.util` | 可改的日历字段，旧 API | `Chapter7/TestCalendar.java` | `MONTH` 从 0 开始，`Calendar.AUGUST` 才是 8 月。不要写数字 `7` 当 7 月 |
| `LocalDate` | `java.time` | 只有日期，没有钟点 | `TestTime.java` 里 `LocalDate.of(2026, 9, 5)` | 月份从 1 开始，9 就是 9 月 |
| `LocalTime` | `java.time` | 只有钟点 | | 没有日期 |
| `LocalDateTime` | `java.time` | 日期加钟点，没有时区 | `LocalDateTime.now()`，用户的 `createTime` | 业务里的「创建时间」常用它 |
| `Instant` | `java.time` | 时间线上的一个瞬时点，适合存时间戳 | `Instant.now().toEpochMilli()` 得到毫秒 | 日志、跨时区存储用它 |
| `ZonedDateTime` | `java.time` | 带时区的日期时间 | 项目数据源时区是 `Asia/Shanghai` | 要区分「北京时间几点」时用它 |
| `DateTimeFormatter` | `java.time.format` | 按图案格式化和解析 | `ofPattern("yyyy/MM/dd")` | 图案里的 `M` 是月，`m` 是分。中英文月份要加 `Locale` |

`LocalDate` 不能改。`date.plusDays(10)` 返回一个新对象，原来的 `date` 不变。这是 `TestTime.java` 里写明的。

`Calendar` 能改。`TestCalendar.java` 里：

- `add` 会进位。8 月 31 日再加 8 个月，年会变成下一年。
- `roll` 只在当前字段里滚动，年不变。
- `get(Calendar.MONTH)` 的 0 是 1 月。`LocalDate.getMonthValue()` 的 1 才是 1 月。

和数据库、接口互转时，图案要和字符串一致。`"2026-09-05"` 用 `LocalDate.parse` 的默认格式即可。`"2026/09/05"` 必须自己写 `DateTimeFormatter.ofPattern("yyyy/MM/dd")`。

`User` 的 `createTime`、`updateTime` 类型是 `LocalDateTime`。插入 SQL 里用的是数据库的 `now()`，不是 Java 里先算好再传进去。

## 12. 集合

集合长度可变，用泛型声明元素类型。练习在 `Chapter8/Collection` 和 `Chapter8/Map`。

先选接口，再选实现类。变量写成接口类型：`List<String> list = new ArrayList<>();`。

| 接口 | 特点 | 常用实现 | 什么时候用 |
|------|------|----------|------------|
| `List` | 有顺序，可重复，有下标 | `ArrayList` 按位置取最快；`LinkedList` 两头插入更快，按下标取要遍历 | 用户列表、分页结果。查询多就用 `ArrayList` |
| `Set` | 不重复 | `HashSet` 无序；`LinkedHashSet` 保留插入顺序；`TreeSet` 按排序规则排好 | 只要唯一值，例如一组不重复的 id |
| `Queue` | 排队，通常一头进、另一头出 | `LinkedList`、`ArrayDeque` | 按先后处理任务 |
| `Deque` | 两头都能进能出 | `ArrayDeque` | 当栈（后进先出）或当队列 |
| `Map` | 键值对。键不重复，后放入的同键会覆盖旧值 | 见下表 | 用一个键快速找到值，例如按名字找到数量 |

`CollectionListTest.java` 里 `ArrayList` 可以 `add` 重复元素，`get(0)` 取第一个，`set(1, "set")` 改下标 1，`add(1, "add")` 是在下标 1 插入并把后面的元素往后挪。

Map 的实现：

| 类 | 顺序 | 能不能有 null 键 | 是不是线程安全 | 你文件里的结论 |
|----|------|------------------|----------------|----------------|
| `HashMap` | 不保证顺序 | 允许一个 null 键 | 否 | 最常用。`put("Apple", 5)` 后再 `put("Apple", 10)`，值变成 10 |
| `LinkedHashMap` | 按插入顺序 | 允许 | 否 | 既要按键查，又要按放入顺序遍历 |
| `TreeMap` | 按键排序 | 不允许 null 键 | 否 | 键需要从小到大 |
| `Hashtable` | 不保证 | 不允许 null | 是，但整表加锁，旧类 | 新代码不要用。要并发用下面这个 |
| `ConcurrentHashMap` | 不保证 | 不允许 null | 是，分段并发，比 Hashtable 适合多线程 | 多线程同时读写同一份 Map |

`Chapter8/CollectionsTest.java`、`CollectionsDemo.java` 用的是 `java.util.Collections` 工具类，给已有集合排序、查找、包装成不可修改。它和接口 `Collection` 不是同一个东西。

`Chapter8/ImmutableCollectionsTest.java` 是不可变集合：`List.of(...)`、`Map.of(...)` 创建之后不能 `add` 或 `put`，改了会抛异常。适合当固定配置，不适合当还要往里加用户的列表。

## 13. Lambda、方法引用、Stream

`Chapter8/LambdaTest.java`：集合的 `forEach` 可以传一段「对每个元素做什么」。

```java
numbers.forEach(num -> System.out.println(num));
numbers.forEach(System.out::println);
```

`num -> System.out.println(num)` 是 Lambda：箭头左边是参数，右边是要执行的代码。`System.out::println` 是方法引用，表示「把元素交给这个已有方法」，和上面那行是同一件事。

`Chapter8/StreamTest.java` 把一个集合变成流水线，原来的 `names` 不会被改掉。中间操作是懒的，遇到 `toList()`、`count()` 这种结束操作才真正跑。

| 操作 | 干什么 | 例子 |
|------|--------|------|
| `filter` | 留下符合条件的 | 名字长度大于 3 |
| `map` | 每个元素变成另一种 | `String::toUpperCase` |
| `distinct` | 去重 | 两个 `Tom` 留一个 |
| `sorted` | 排序 | 默认按自然顺序 |
| `limit` | 只要前 n 个 | `limit(3)` |
| `count` | 数有多少个 | 以 `A` 开头的个数 |
| `toList()` | 结束并收集成 List | Java 16 起可以直接这样写 |
| `collect` | 结束并收成别的结构 | `Chapter8/CollectorsTest.java`，例如收成 `Map` |

`Chapter8/StreamUltimateDemo.java` 是把这些操作串在一起的综合例子。业务代码里一段简单循环用 `for` 更直白；筛选、转换、统计链比较长时，用 Stream 更短。

## 14. 和后面 Spring 代码的对应关系

| 基础语法 | 在用户模块里出现在哪 |
|----------|----------------------|
| 类、对象、`private` 字段 | `User` |
| 包装类型 `Integer`、`Long` | `User.age`、`User.id`，可以是 `null` |
| `LocalDateTime` | `User.createTime`、`User.updateTime` |
| 枚举 | `Gender`、`ResultCodeEnum` |
| `List<User>` | 查询结果 |
| `equals` / 内容比较 | 姓名包含用的是 `contains`，不是 `==` |
| 接口 | `UserMapper`、`UserQuery` |
| 异常 | `BusinessException extends RuntimeException` |
| Lambda | `LogAspect` 之外，Stream 练习在 Chapter8；业务代码里更多是普通方法 |
