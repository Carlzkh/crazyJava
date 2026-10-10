# 第 2 周：异常、泛型、枚举、注解

对照代码主要在 `com.example.crazyjava` 的 `exception`、`enums`、`common`、`controller`、`service`、`entity`。

这一周做完的练习：在 `ResultCodeEnum` 增加 `USER_AGE_TOO_YOUNG(60005, "用户未满18岁")`，并在 `UserService.createUser` 里，年龄小于 18 时抛出它。

## 1. 为什么用 throw，不直接返回一个失败结果

Service 发现业务做不下去时，用 `throw` 把问题交出去，当前方法立刻结束。后面的保存、返回都不会执行。

`UserService.createUser` 里，未满 18 岁不会调用 `userRepository.save(user)`：

```java
public User createUser(User user) {
    if (user.getAge() != null && user.getAge() < 18) {
        throw new BusinessException(ResultCodeEnum.USER_AGE_TOO_YOUNG);
    }
    userRepository.save(user);
    return user;
}
```

Controller 不用自己写 `try-catch`。异常向上冒泡，由 `GlobalExceptionHandler` 统一变成 `Result`。前端看到的是：

- `code`：`60005`
- `msg`：`用户未满18岁`
- `data`：没有，也就是 `null`

如果每个接口都自己拼失败结果，错误格式会慢慢写乱。抛异常、一处处理，格式才统一。

### 例子

用户不存在也是同一条路：

```java
public User getUserById(long id) {
    User user = userRepository.findById(id);
    if (user == null) {
        throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
    }
    return user;
}
```

这里不会返回 `null`。调用方要么拿到一个真实的 `User`，要么收到异常。

## 2. 运行时异常和检查异常

异常的大致关系：

```text
Throwable
├── Error          JVM 严重问题，业务代码不要 catch
└── Exception
    ├── RuntimeException     运行时异常，方法上可以不写 throws
    └── 其他，例如 IOException  检查异常，必须处理或声明抛出
```

**运行时异常**继承 `RuntimeException`。你的业务异常就是这种：

```java
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String msg;
}
```

`createUser` 的方法签名上没有 `throws BusinessException`。调用它的 Controller 也可以不写 `try-catch`。

**检查异常**直接继承 `Exception`，不经过 `RuntimeException`。方法要么 `try-catch`，要么在签名上写 `throws`。导出 CSV 就是这种，因为拿响应输出流会抛 `IOException`：

```java
public void exportUsers(..., HttpServletResponse response) throws IOException {
```

业务规则不满足，用运行时异常。文件、网络这种调用方回避不了的失败，用检查异常。

`Error` 表示虚拟机自己出了大事，例如内存耗尽。不要用它表达「用户未满 18 岁」。

### 例子

未满 18 岁是业务规则，所以抛运行时异常，方法不用写 `throws`：

```java
throw new BusinessException(ResultCodeEnum.USER_AGE_TOO_YOUNG);
```

写文件失败是检查异常，方法必须声明：

```java
public void exportUsers(...) throws IOException
```

## 3. try、catch、finally

`try` 里放可能出错的代码。某种异常出现时，进入对应的 `catch`。`finally` 里的代码，无论是否出异常，都会执行，适合关闭流、释放资源。

捕获时，子类要写在父类前面。如果先写 `catch (Exception e)`，后面的 `catch (BusinessException e)` 永远到不了，因为 `BusinessException` 也是一种 `Exception`。

全局处理器的方法顺序表达的是同一件事：先处理 `BusinessException`，再处理更宽的 `RuntimeException`，最后才是 `Exception`。

```java
@ExceptionHandler(BusinessException.class)
public Result<Void> handleBusinessException(BusinessException e) {
    return Result.error(e.getCode(), e.getMessage());
}

@ExceptionHandler(RuntimeException.class)
public Result<Void> handleRuntimeException(RuntimeException e) {
    return Result.error(ResultCodeEnum.SYSTEM_ERROR);
}
```

`BusinessException` 继承 `RuntimeException`。如果没有第一个方法，未满 18 岁也会被第二个方法接走，前端看到的就变成 `50000 系统内部错误`。

### 例子

读取文件时，关闭可以放在 `finally`。现在更常见的写法是 `try-with-resources`，括号里声明的流会自动关闭。导出 CSV 用的就是这种：

```java
try (PrintWriter writer = response.getWriter()) {
    writer.println("ID,姓名,年龄,性别,创建时间,更新时间");
}
```

出了异常，`writer` 仍会被关闭。

## 4. 项目里有两个 BusinessException

| 类 | 谁在用 | 全局处理器认不认 |
|----|--------|------------------|
| `com.example.crazyjava.exception.BusinessException` | `UserService`、`Gender`、`GlobalExceptionHandler` | 认，按里面的 `code` 返回 |
| `com.example.crazyjava.common.exception.BusinessException` | 目前业务代码没有用它 | 不认。它也是 `RuntimeException`，会落到系统错误 `50000` |

两个类名字相同，但是不同的类。`import` 决定你 `throw` 的是哪一个。Service 里必须是：

```java
import com.example.crazyjava.exception.BusinessException;
```

### 例子

下面这个抛出会被 `handleBusinessException` 接住，返回 `60005`：

```java
throw new BusinessException(ResultCodeEnum.USER_AGE_TOO_YOUNG);
```

如果误 import 了 `common.exception` 里的那个类，它没有接收 `ResultCodeEnum` 的构造方法，现有这行代码也编不过。就算换成那个类能编译的构造方法，处理器也不会按业务码返回。

## 5. throw 和 throws

`throw` 是抛出一个异常对象，写在方法体内。

`throws` 是声明「这个方法可能把某种检查异常交给调用方」，写在方法签名上。

运行时异常可以不写 `throws`。检查异常必须处理，或者写上 `throws`。

### 例子

```java
throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
```

这是 `throw`，立刻中断当前方法。

```java
public void exportUsers(...) throws IOException
```

这是 `throws`，告诉调用方：我这里可能有 `IOException`。

## 6. Result\<T\>：尖括号里是 data 的类型

`Result` 每次都有 `code` 和 `msg`。`data` 的类型不固定，用 `T` 占位。用的时候把 `T` 换成具体类型。

```java
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
}
```

| 返回类型 | data 里是什么 |
|----------|----------------|
| `Result<User>` | 一个 `User`，例如新增成功后的那个人 |
| `Result<Void>` | 没有业务数据，`data` 是 `null`。删除成功、未满 18 岁都是这种 |
| `Result<PageResult<User>>` | 一个分页对象。页码信息在外层，用户列表在它的 `list` 里 |
| `Result<List<User>>` | 直接一个用户列表，搜索接口用的是这种 |
| `Result<Integer>` | 一个整数，例如批量删除返回删除了几条 |

`Void` 是一种类型，表示「这里故意没有值」。它和 `null` 不是同一个词：类型写成 `Result<Void>`，运行时那个 `data` 字段的值才是 `null`。

列表只能放声明过的元素类型。`List<User>` 里不能放 `String`。基本类型不能写进尖括号，要写包装类型：`List<Integer>` 可以，`List<int>` 不行。

### 例子

新增成功：

```java
public Result<User> createUser(@Valid @RequestBody User user) {
    User newUser = userService.createUser(user);
    return Result.success("创建用户成功", newUser);
}
```

`data` 是 `newUser` 这个对象。

删除成功：

```java
public Result<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUserById(id);
    return Result.success("用户删除成功", null);
}
```

`data` 是 `null`。

## 7. PageResult\<T\> 自己也有泛型

分页对象里，页码字段的类型是固定的。真正的数据列表跟着 `T` 走：

```java
public class PageResult<T> {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;
}
```

`PageResult<User>` 的 `list` 就是 `List<User>`。如果以后有订单分页，可以写成 `PageResult<Order>`，字段不用再复制一份。

`Result<PageResult<User>>` 要分成两层看：

- 外层 `Result` 的 `data` 是整个 `PageResult<User>`
- 内层 `list` 才是这一页的用户

### 例子

```java
public Result<PageResult<User>> getUsersByPage(...) {
    PageInfo<User> pageInfo = userService.getUserListByPageAndName(page, size, name);
    PageResult<User> pageResult = new PageResult<>(pageInfo);
    return Result.success(pageResult);
}
```

## 8. 方法上的 \<T\>

`success` 是静态方法。调用它时还没有一个 `Result` 对象，所以类名后面的 `T` 用不上。方法自己再声明一个 `<T>`，看这次参数是什么类型：

```java
public static <T> Result<T> success(T data) {
    Result<T> result = new Result<>();
    result.setData(data);
    return result;
}
```

传入 `User`，返回 `Result<User>`。传入 `PageResult<User>`，返回 `Result<PageResult<User>>`。

类上的 `T` 描述「这个对象的 data」。方法上的 `<T>` 描述「这一次调用的 data」。两边都写 `T`，只是名字相同，各管各的。

### 例子

```java
Result<User> one = Result.success(user);
Result<PageResult<User>> page = Result.success(pageResult);
```

同一个 `success` 方法，两次的 `T` 不同。

## 9. 枚举是一组固定对象

枚举用来表示个数有限、提前定好的值。性别只有男和女，错误码也是事先列好的。程序启动之后就这些对象，不能再 `new` 一个新的出来。

错误码：

```java
public enum ResultCodeEnum {
    USER_NOT_FOUND(60000, "用户不存在"),
    USER_AGE_TOO_YOUNG(60005, "用户未满18岁"),
    SYSTEM_ERROR(50000, "系统内部错误，请稍后重试");

    private final Integer code;
    private final String msg;
}
```

`USER_AGE_TOO_YOUNG(60005, "用户未满18岁")` 会调用构造方法，把 `60005` 放进 `code`，把文字放进 `msg`。Service 里写 `ResultCodeEnum.USER_AGE_TOO_YOUNG`，用的就是这一个现成对象。

性别：

```java
public enum Gender {
    MALE(1, "男"),
    FEMALE(0, "女");

    private final int code;
    private final String desc;

    Gender(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

`FEMALE(0, "女")` 执行完，`code` 是 `0`，`desc` 是 `"女"`。

枚举的构造方法不能写成 `public`。即使源码里不写 `private`，它也是私有的。所以别的类不能 `new Gender(2, "未知")`。要增加一种性别或一个错误码，只能在枚举里再加一行常量。

### 例子

合法用法：

```java
Gender gender = Gender.MALE;
int code = gender.getCode();       // 1
String desc = gender.getDesc();    // 男
```

不合法，编译失败：

```java
Gender gender = new Gender(2, "未知");
```

## 10. 枚举上常用的方法

这些方法是枚举自带的，不用自己写。

| 方法 | 作用 | 例子 |
|------|------|------|
| `values()` | 返回全部常量，方便循环 | `Gender.values()` 得到 `MALE` 和 `FEMALE` |
| `name()` | 常量的名字 | `Gender.MALE.name()` 是 `"MALE"` |
| `valueOf(名字)` | 按名字找常量，对不上就抛异常 | `Gender.valueOf("MALE")` |
| `ordinal()` | 常量的声明顺序，从 0 开始 | 不要拿它当业务编号 |

业务编号要自己定义字段，例如 `Gender.code`、`ResultCodeEnum.code`。不要用 `ordinal()`。以后在中间插入一个常量，顺序号会变，已经存进数据库的值就错了。

同一枚举的两个常量可以用 `==` 比较，因为每个常量只有一个对象。`gender == Gender.MALE` 和 `gender.equals(Gender.MALE)` 都可以。

### 例子

`Gender.fromCode` 就是循环全部常量，按自己的 `code` 字段查找。找不到就抛业务异常：

```java
public static Gender fromCode(int code) {
    for (Gender g : values()) {
        if (g.code == code) {
            return g;
        }
    }
    throw new BusinessException(ResultCodeEnum.GENDER_INVALID);
}
```

`fromCode(1)` 得到 `Gender.MALE`。`fromCode(9)` 抛出 `GENDER_INVALID`。

`fromValue` 用的是 `name()`，并且忽略大小写。`"female"` 能找到 `FEMALE`。对不上时抛的是 `IllegalArgumentException`，这是 JDK 自带的运行时异常，不是 `BusinessException`。

## 11. 注解是标记，不是方法

注解以 `@` 开头，贴在类、方法、字段或参数上。你的业务代码不会去调用它。编译器或 Spring 读到标记后，按标记做事。

这一周接触到的注解分三类。

**编译器看的。** `@Override` 检查这个方法是不是真的重写了父类或接口的方法。方法名写错时，编译失败。程序运行阶段不再用它。

**Spring 看的。** 用它决定请求怎么进来、异常怎么被接住、哪个类交给容器创建。

**Lombok 看的。** 编译时生成代码。`User` 上的 `@Data` 会生成 `getName`、`setName` 这些方法。源文件里看不到，但 `user.getAge()` 能调用。

注解可以带参数。括号里是名字和值。只有一个参数且名字是 `value` 时，可以省略名字，例如 `@GetMapping("/export")`。

### 例子

新增用户这个方法上的三个注解：

```java
@PostMapping
public Result<User> createUser(@Valid @RequestBody User user)
```

- `@PostMapping` 贴在方法上。浏览器用 POST 访问这个方法。它后面没有再写路径，地址就用类上的 `@RequestMapping("/api/${api.version}/users")`，实际是 `/api/v1/users`。
- `@RequestBody` 贴在参数上。把请求体里的 JSON 变成 `User`。它不负责地址栏上的参数。
- `@Valid` 贴在参数上。JSON 转成对象之后、进入方法体之前，按 `User` 字段上的规则检查。

姓名字段上的规则：

```java
@NotNull(message = "{user.name.notnull}")
@Size(min = 1, max = 50, message = "姓名长度必须在1-50之间")
private String name;
```

`null` 过不了 `@NotNull`，提示「姓名不能为空」。空字符串 `""` 不是 `null`，`@NotNull` 会通过，再由 `@Size(min = 1)` 拦住，提示「姓名长度必须在1-50之间」。两种失败都进不了 `createUser` 方法体。

## 12. 参数从哪里来

前端传来的数据有三种常见位置，对应三个注解。

| 注解 | 贴在哪 | 数据在哪 | 项目里的例子 |
|------|--------|----------|----------------|
| `@RequestBody` | 参数 | 请求体 JSON | `createUser` 的 `User` |
| `@PathVariable` | 参数 | 地址路径里的一段 | `DELETE /api/v1/users/3` 里的 `3` |
| `@RequestParam` | 参数 | 地址后面的 `?name=张三` | 分页的 `page`、`size`、`name` |

`@RequestBody` 不是「凡是前端传的都归它」。查询字符串用 `@RequestParam`，路径里的 id 用 `@PathVariable`。

### 例子

删除用户，id 在路径里：

```java
@DeleteMapping("/{id}")
public Result<Void> deleteUser(@PathVariable Long id)
```

`DELETE /api/v1/users/3` 时，`id` 是 `3`。

导出用的是查询参数，而且可以不传：

```java
public void exportUsers(@RequestParam(required = false) String name, ...)
```

`required = false` 表示这个参数可以没有。没有时 `name` 是 `null`。

## 13. 这一周用到的注解

| 注解 | 贴在哪 | 谁来读 | 作用 |
|------|--------|--------|------|
| `@Override` | 方法 | 编译器 | 确认这是重写，不是新方法 |
| `@RestController` | 类 | Spring | 这个类的方法返回值直接变成 HTTP 响应 |
| `@RequestMapping` | 类或方法 | Spring | 地址前缀或完整地址 |
| `@GetMapping` `@PostMapping` `@PutMapping` `@DeleteMapping` | 方法 | Spring | 请求方式，分别是 GET、POST、PUT、DELETE |
| `@RequestBody` | 参数 | Spring | JSON 请求体转成对象 |
| `@PathVariable` | 参数 | Spring | 路径里的变量 |
| `@RequestParam` | 参数 | Spring | `?` 后面的查询参数 |
| `@Valid` | 参数 | Spring 和校验框架 | 进入方法前做字段校验 |
| `@NotNull` `@Size` `@Min` `@Max` | 字段 | 校验框架 | 具体的校验规则 |
| `@ExceptionHandler` | 方法 | Spring | 这个方法负责处理某种异常 |
| `@RestControllerAdvice` | 类 | Spring | 全局异常处理类 |
| `@ResponseStatus` | 方法 | Spring | 处理完异常后 HTTP 状态码是多少 |
| `@Service` | 类 | Spring | 业务类，启动时创建并放进容器 |
| `@Repository` | 类 | Spring | 数据访问类 |
| `@Autowired` | 字段或构造方法 | Spring | 把容器里的对象塞进来 |
| `@Transactional` | 方法 | Spring | 这个方法用事务包起来 |
| `@Data` `@Getter` `@Slf4j` | 类 | Lombok | 生成 getter、setter、日志对象 |
| `@Mapper` | 接口 | MyBatis | 这个接口的实现按 XML 生成 |

现在先会认、会贴。自己定义一个新注解，要写给反射去读，那是后面的内容。

### 例子

`@GetMapping("/export")` 同时规定了两件事：请求方式是 GET，路径在类的前缀后面再加 `/export`。完整地址是 `/api/v1/users/export`。

`@PostMapping` 没有写路径，就停在类的前缀 `/api/v1/users`。

## 14. 校验失败和业务异常不是同一种失败

校验发生在进 Controller 方法之前。`name` 为空时，`createUser` 里的第一行日志都不会打印。抛出的是 `MethodArgumentNotValidException`，由 `handleValidationException` 返回 `40000` 和字段上的提示。

业务异常发生在方法已经开始执行之后。年龄传 `16` 能通过 `@Min(0)`，于是进入 `createUser`，再由 Service 抛 `USER_AGE_TOO_YOUNG`。这是规则不满足，不是 JSON 格式不合法。

### 例子

年龄字段现在的注解是：

```java
@NotNull(message = "{user.age.notnull}")
@Min(value = 0, message = "{user.age.min}")
@Max(value = 150, message = "{user.age.max}")
private Integer age;
```

| 传入的年龄 | 停在哪 | 返回 |
|------------|--------|------|
| 不传，或 JSON 里是 `null` | 进方法之前，`@NotNull` 失败 | `40000`，年龄不能为空 |
| `-1` | 进方法之前，`@Min(0)` 失败 | `40000`，年龄不能小于 0 |
| `16` | Service 里抛业务异常 | `60005`，用户未满18岁 |
| `20` | 通过校验，也通过业务判断 | 继续保存 |

未满 18 岁没有写成 `@Min(18)`。它是后来加在 Service 里的业务规则，用来练习「抛业务异常」。两种做法都能拦住 16 岁，失败类型不一样。
