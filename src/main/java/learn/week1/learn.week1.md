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
