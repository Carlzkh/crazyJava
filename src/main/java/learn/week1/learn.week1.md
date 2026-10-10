# 第 1 周：接口、实现类、继承

对照代码在 `src/main/java/learn/week1/`。

## 1. 为什么要写接口，直接写类不行吗

直接写类可以。类和类本来就能互相调用。

接口用在「调用方要能换实现」的时候。调用方只依赖方法约定，不依赖某一个具体的类。

`Week1Main` 里变量类型是 `UserQuery`，所以同一条 `query.query(users)` 可以先放 `AdultUserQuery`，再换成 `NameUserQuery`。

`UserMapper` 也是这个原因。`UserRepository` 只调用 `userMapper.findAll()`。实现不在你的 Java 文件里，是 MyBatis 按 XML 补上的。

一个功能只有一种做法、也不会被人换掉时，直接写类就够了。

### 例子

下面两行调用的是同一个方法名，跑的是两个不同的类：

```java
UserQuery query = new AdultUserQuery();
System.out.println(query.query(users).size());

query = new NameUserQuery("张");
System.out.println(query.query(users).size());
```

如果 `main` 里写成 `AdultUserQuery query = new AdultUserQuery()`，第二行就不能再放进 `NameUserQuery`，因为变量已经绑死在成年查询这一个类上。

## 2. public、protected、private 什么时候用

这三个修饰符决定谁能看见这个类、方法或字段。

| 修饰符 | 谁能用 | 在本周代码里 |
|--------|-----|----------------|
| `public` | 任何类 | `query`，`Week1Main` 要调用它 |
| `protected` | 本类和子类 | `match`，只有父类的循环和两个子类要用 |
| `private` | 只有本类 | `NameUserQuery` 里的 `name`，外面不需要知道关键字 |

`Week1Main` 应该调用 `query()`，不应该自己去调 `match()`。关键字只在 `NameUserQuery` 内部使用，所以字段是 `private`。

接口里的方法即使不写 `public`，也默认是 `public`。

### 例子

`NameUserQuery` 里关键字只有自己用，所以是 `private`：

```java
public class NameUserQuery extends AbstractUserQuery {
    private final String name;

    public NameUserQuery(String name) {
        this.name = name;
    }
}
```

`Week1Main` 可以写 `new NameUserQuery("张")`，因为构造方法是 `public`。  
它不能写 `query.name`，因为字段是 `private`，编译会失败。

父类把判断留给子类，外面不需要调用，所以是 `protected`：

```java
protected abstract boolean match(User user);
```

`Week1Main` 里写 `query.match(user)` 也会编译失败。子类里写 `@Override protected boolean match` 可以。

## 3. @Override 是干什么的

`@Override` 告诉编译器：这个方法是在实现父类或接口里已经有的方法，不是新写一个。

方法名写错时，编译会直接失败，而不是悄悄多出一个没用的方法。

### 例子

父类要求子类实现的方法名是 `match`。子类写成：

```java
@Override
protected boolean match(User user) {
    return user.getName() != null && user.getName().contains(name);
}
```

如果手滑写成 `matc`，又加了 `@Override`，编译器会报错：父类里没有 `matc` 这个方法。  
去掉 `@Override` 再写成 `matc`，编译器会把它当成一个新方法。父类的 `match` 仍然没有实现，错误就没那么直白。

## 4. 父类为什么要用抽象类，不抽象行不行

父类的 `match` 没有方法体。年龄条件在 `AdultUserQuery`，姓名条件在 `NameUserQuery`，父类不知道该怎么判断。

Java 规定：类里有这种没写完的方法，类就必须标成 `abstract`，而且不能 `new AbstractUserQuery()`。

不写成抽象类也可以，但必须给 `match` 编一个方法体。那样父类就能被创建，可是父类并不知道按年龄还是按姓名筛选。这个例子里父类本来就是半成品，用抽象类是为了禁止单独使用它。

### 例子

现在的父类不能单独创建：

```java
public abstract class AbstractUserQuery implements UserQuery {
    @Override
    public List<User> query(List<User> users) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (match(user)) {
                result.add(user);
            }
        }
        return result;
    }

    protected abstract boolean match(User user);
}
```

下面这行编译失败：

```java
UserQuery query = new AbstractUserQuery();
```

如果去掉 `abstract`，又给 `match` 写一个永远返回 `false` 的方法体，这行就能编译。查任何名单结果都是空列表。年龄和姓名两种查询都失去了意义。

## 5. List\<User\> 是不是泛型

是。尖括号里的 `User` 是泛型参数。

```java
public List<User> query(List<User> users)
```

这句定义方法 `query`。返回值是一个列表，元素类型是 `User`。参数 `users` 同样是元素为 `User` 的列表。

`List` 是接口，`ArrayList` 是它的一种实现。父类里 `new ArrayList<>()`，再当作 `List<User>` 返回。

### 例子

三个用户放进列表再按姓名「张」查询：

```java
List<User> users = new ArrayList<>();
users.add(createUser("张三", 16));
users.add(createUser("李四", 20));
users.add(createUser("张伟", 30));

UserQuery query = new NameUserQuery("张");
List<User> matched = query.query(users);
```

`matched` 的类型是 `List<User>`。里面有两个元素，都是 `User`：张三和张伟。  
这里不能放进一个 `String`，因为列表声明的元素类型是 `User`。

## 补充：这一周还要掌握的概念

下面都是后面写 Spring 时会反复碰到的基本规则。例子仍用 `learn.week1` 里的类。

### 6. 类是模板，对象是 new 出来的

类规定有哪些字段和方法。`new` 一次，内存里就多一个对象。同一个类可以 `new` 出很多个对象，每个对象的字段各自保存。

### 例子

两个查询对象互不影响。一个找「张」，一个找「李」：

```java
NameUserQuery zhang = new NameUserQuery("张");
NameUserQuery li = new NameUserQuery("李");
```

`zhang` 和 `li` 都是 `NameUserQuery` 这一个类造出来的，里面的 `name` 各存各的。

### 7. 构造方法

构造方法的名字和类名相同，没有返回类型。`new` 的时候会执行它，用来给对象做初始赋值。

自己不写构造方法时，编译器会补一个无参构造方法，也就是 `new AdultUserQuery()` 能通过的原因。  
一旦自己写了带参数的构造方法，这个自动补上的无参构造方法就没有了。`NameUserQuery` 因此只能 `new NameUserQuery("张")`，不能 `new NameUserQuery()`。

一个 Java 文件里只能有一个 `public` 类，文件名必须和这个类名相同。`NameUserQuery.java` 里的 public 类就只能叫 `NameUserQuery`。

### 例子

```java
public NameUserQuery(String name) {
    this.name = name;
}
```

这是构造方法，不是普通方法。它没有 `void`，也没有别的返回类型。

### 8. this 表示当前这个对象

构造方法的参数名和字段名都叫 `name` 时，用 `this.name` 指字段，用 `name` 指参数。

### 例子

```java
private final String name;

public NameUserQuery(String name) {
    this.name = name;
}
```

`this.name = name` 是「把传进来的参数，赋给当前对象的字段」。不写 `this`，编译器分不清赋值给谁。

### 9. static 属于类，不属于某个对象

`static` 方法可以直接用类名调用，也可以从 `main` 里调用，不需要先 `new` 出对象。  
`main` 本身必须是 `static`，因为程序启动时还没有任何对象，Java 只能先找类上的 `main`。

`static` 方法里没有 `this`。它不能直接使用非静态的字段和方法。

### 例子

`Week1Main` 里的 `createUser` 是 `private static`。`main` 不需要 `new Week1Main()` 就能调用它：

```java
public static void main(String[] args) {
    users.add(createUser("张三", 16));
}

private static User createUser(String name, int age) {
    User user = new User();
    user.setName(name);
    user.setAge(age);
    return user;
}
```

`NameUserQuery` 的 `match` 不是 `static`。必须先有一个对象，才能调用。

### 10. final 表示定下来之后不能改

字段加 `final`：只能赋值一次。`NameUserQuery` 的 `name` 在构造方法里赋值，之后不能再改。  
方法加 `final`：子类不能重写这个方法。  
类加 `final`：不能再被别的类 `extends`。

### 例子

```java
private final String name;
```

构造方法里写过 `this.name = name` 之后，再写 `this.name = "李"` 会编译失败。

### 11. 不写修饰符，表示只有同一个包能用

除了 `public`、`protected`、`private`，还有一种不写修饰符的访问范围，叫包访问。同一个包里的类能用，别的包不能用。

`learn.week1` 里的类互相调用，是因为它们在同一个包。`User` 在 `com.example.crazyjava.entity`，所以 `Week1Main` 要用它，必须 `import`，而且 `User` 的类本身是 `public`。

### 例子

如果把 `UserQuery` 的 `public` 去掉：

```java
interface UserQuery {
    List<User> query(List<User> users);
}
```

`learn.week1` 包里的类仍然能实现它。`com.example.crazyjava` 包里的类就不能再用这个接口。

### 12. 重载和重写不是一回事

重载：同一个类里，方法名相同，参数不同。调用时按参数决定跑哪一个。你在 `Chapter5/Overload.java` 里写过。  
重写：子类把父类或接口里已有的方法重新写一遍。方法名、参数、返回值都要一致。`@Override` 只用在重写上。

### 例子

重载，都在 `Overload` 这一个类里：

```java
public void test() { }
public void test(String msg) { }
```

重写，子类实现父类的 `match`：

```java
@Override
protected boolean match(User user) {
    return user.getAge() != null && user.getAge() >= 18;
}
```

子类把 `match(User user)` 改成 `match(User user, int age)`，那就变成了重载，父类那个没有方法体的 `match` 仍然没人实现，编译会失败。

### 13. 只能有一个父类，可以实现多个接口

Java 里一个类只能 `extends` 一个类。一个类可以 `implements` 多个接口，接口之间用逗号隔开。

你现在的关系是：

- `AbstractUserQuery` 实现接口 `UserQuery`
- `NameUserQuery` 继承 `AbstractUserQuery`
- 所以 `NameUserQuery` 也是一种 `UserQuery`

子类不用再写一遍 `implements UserQuery`。

### 例子

一个类同时实现两个接口的写法：

```java
public class NameUserQuery extends AbstractUserQuery implements UserQuery, AnotherInterface {
}
```

这里 `implements UserQuery` 可以不写，因为父类已经实现了。`extends` 后面仍然只能有一个类，不能写成 `extends AbstractUserQuery, 另一个类`。

### 14. 子类构造时会先调用父类构造

子类对象创建时，会先跑父类的构造方法，再跑子类的构造方法。  
父类有无参构造方法时，子类可以不写 `super()`，编译器会自动补上。  
父类只有带参数的构造方法时，子类构造方法的第一行必须写 `super(参数)`。

`AbstractUserQuery` 没有自己的构造方法，所以它有一个自动生成的无参构造。`NameUserQuery` 因此不用写 `super()`。

### 例子

如果父类改成必须接收一个标题：

```java
public abstract class AbstractUserQuery implements UserQuery {
    private final String title;

    public AbstractUserQuery(String title) {
        this.title = title;
    }
}
```

子类就要显式调用父类构造方法，而且 `super(...)` 必须是第一行：

```java
public NameUserQuery(String name) {
    super("按姓名查");
    this.name = name;
}
```

### 15. 多态：变量看左边，真正执行看右边

变量声明成父类或接口，实际放进去的是子类对象。  
能调用哪些方法，看左边声明的类型。  
重写的方法最终跑哪一段代码，看右边真正的对象。

### 例子

```java
UserQuery query = new NameUserQuery("张");
query.query(users);
```

左边是 `UserQuery`，所以这里只能调用 `query()`，不能调用 `NameUserQuery` 才有的东西。  
`query()` 的循环写在父类 `AbstractUserQuery` 里。循环调用 `match()` 时，跑的是右边这个对象的 `NameUserQuery.match`。

换成 `new AdultUserQuery()` 之后，循环还是同一段，`match()` 就改跑成年判断。

### 16. 接口里能写什么

接口里的方法，不写修饰符时默认是 `public abstract`，所以 `UserQuery` 里的 `query` 没有方法体。  
接口里的字段默认是 `public static final`，也就是常量。  
接口没有构造方法，不能 `new UserQuery()`。

Java 8 之后，接口还可以写 `default` 方法和 `static` 方法，它们可以有方法体。日常先记住：接口主要用来约定方法。公共的循环这种具体代码，放在抽象类里更合适。你现在就是这么分的。

### 例子

接口里可以放常量：

```java
public interface UserQuery {
    int ADULT_AGE = 18;

    List<User> query(List<User> users);
}
```

`ADULT_AGE` 不用写 `public static final`，它已经是常量。实现类里用 `UserQuery.ADULT_AGE` 读取。

### 17. 抽象类和接口怎么选

有一份大家共用的具体代码，又有一块必须由子类去补的逻辑，用抽象类。`AbstractUserQuery` 的循环就是共用代码，`match` 就是子类去补的部分。

只规定「要有哪些方法」，谁来做、怎么做都不管，用接口。`UserQuery` 只要求有 `query`。`UserMapper` 也是只规定方法，SQL 交给 MyBatis。

两者可以一起用，你这个包就是这样：接口约定能力，抽象类放下半成品实现，普通子类补上最后的条件。

### 例子

只保留接口、不要抽象类，也是对的。`AdultUserQuery` 和 `NameUserQuery` 各自把整段 `query` 写一遍即可。  
抽父类是因为两段循环重复了。重复的放父类，不同的留在子类。

### 18. 每个类最终都继承 Object

一个类不写 `extends` 时，它的父类是 `java.lang.Object`。`equals`、`toString` 都定义在 `Object` 上。

`User` 使用了 Lombok 的 `@Data`，会生成 `toString`。所以把用户列表直接打印出来，能看到 `name` 和 `age`，而不是一串看不懂的地址。

### 例子

```java
System.out.println(query.query(users));
```

打印结果类似：

```text
[User(id=null, name=张三, age=16, ...), User(id=null, name=张伟, age=30, ...)]
```

这是 `User` 的 `toString` 被 `List` 打印时调用了。

### 19. instanceof 用来判断对象的真实类型

左边是接口或父类时，如果确实需要用子类才有的方法，先判断右边到底是哪个类，再强制转换。能用接口完成的事，就不要转换。

### 例子

```java
UserQuery query = new NameUserQuery("张");

if (query instanceof NameUserQuery) {
    NameUserQuery nameQuery = (NameUserQuery) query;
}
```

`query` 上看不到 `NameUserQuery` 的字段。转换之后，变量类型变成子类，才能使用子类特有的内容。  
`query.query(users)` 不需要转换，因为 `query` 已经在接口 `UserQuery` 上了。
