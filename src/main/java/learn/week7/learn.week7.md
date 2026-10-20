# 第 7 周：`#{}`、`${}` 和一对多

对照代码：

- `src/main/resources/mapper/UserMapper.xml` 里的 `userWithAddresses`
- `com.example.crazyjava.entity.Address`
- `User.addresses`
- `GET /api/v1/users/{id}/addresses`

这一周做完的练习：给用户加了地址表。查询 id 为 1 的用户时，一次返回这个人和两条地址。成功时的 `data` 是：

```text
id=1, name=prod, age=18, gender=female
addresses[0] = 上海, 南京路 1 号
addresses[1] = 北京, 长安街 2 号
```

两条地址的 `userId` 都是 `1`。

## 1. `#{}` 和 `${}`

用户输入的值用 `#{}`。它不会把参数拼进 SQL 句子。`findById` 里写的是 `id = #{id}`，真正发给数据库的是 `id = ?`，数值另外送过去。

| 传入的 id | `#{}` 实际执行 | `${}` 实际执行 |
|-----------|----------------|----------------|
| `1` | `WHERE id = ?`，参数是 `1` | `WHERE id = 1` |
| `1 OR 1=1` | `WHERE id = ?`，参数是这整段文字，查不到用户 | `WHERE id = 1 OR 1=1`，条件恒成立，会查出很多行 |

姓名也一样。`LIKE CONCAT('%', #{name}, '%')` 里，用户输入的内容只是要匹配的字符串。写成 `${name}` 时，输入里的引号和 `OR` 会变成 SQL 的一部分。

你现在的 `findByName`、`searchUsers`、`<foreach>` 里的 `#{id}`、以及 `findByIdWithAddresses` 的 `#{id}`，都是传值，不是拼接。

`${}` 只适合不能加引号的位置，例如排序列名。

| 写法 | 变成的 SQL | 结果 |
|------|------------|------|
| `ORDER BY #{column}`，column 是 `name` | `ORDER BY 'name'` | 按一个字符串常量排序，不会按姓名列排 |
| `ORDER BY ${column}`，column 是 `name` | `ORDER BY name` | 按姓名列排序 |

列名必须先在 Java 里限定成 `name`、`age` 这几个固定值，不能把前端传来的字符串直接放进 `${}`。

XML 里比较符号不能直接写 `<`、`>`，要写成 `&lt;`、`&gt;`。这和 `#{}` 不是一回事：`&lt;` 是为了让 XML 文件本身能被解析。

## 2. 一对多：一个用户，多条地址

表是在当前连接的库里建的。`spring.profiles.active=prod` 时，这个库是 `prod`。

```sql
CREATE TABLE address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    city VARCHAR(50) NOT NULL,
    detail VARCHAR(200) NOT NULL
);
```

`Address` 的字段是 `id`、`userId`、`city`、`detail`。`User` 增加了 `List<Address> addresses`，没有加校验注解。新增用户的 JSON 可以不带这个字段。

一次查询用了左连接：

```sql
SELECT u.id AS user_id, u.name, u.age, u.gender, u.create_time, u.update_time,
       a.id AS address_id, a.city, a.detail
FROM user u
LEFT JOIN address a ON a.user_id = u.id
WHERE u.id = #{id} AND u.is_deleted = 0
```

数据库返回的是两行。用户的姓名、年龄在两行里重复，地址不同。

| 行 | user_id | name | address_id | city | detail |
|----|---------|------|------------|------|--------|
| 1 | 1 | prod | 1 | 上海 | 南京路 1 号 |
| 2 | 1 | prod | 2 | 北京 | 长安街 2 号 |

`resultType` 只能按一行填一个对象，没法把这两行收成「一个用户加一个地址列表」。所以这条查询用 `resultMap`，不用 `resultType`。

## 3. `resultMap` 写在哪，每一项是什么

`resultMap` 写在 `UserMapper.xml` 的 `<mapper>` 和 `</mapper>` 之间，和 `<select>`、`<insert>` 同级。放在使用它的 `<select>` 前面。`namespace` 不用改，仍然是 `com.example.crazyjava.mapper.UserMapper`。

```xml
<resultMap id="userWithAddresses" type="com.example.crazyjava.entity.User">
    <id property="id" column="user_id"/>
    <result property="name" column="name"/>
    <collection property="addresses" ofType="com.example.crazyjava.entity.Address">
        <id property="id" column="address_id"/>
        <result property="city" column="city"/>
        <result property="detail" column="detail"/>
    </collection>
</resultMap>

<select id="findByIdWithAddresses" resultMap="userWithAddresses">
    ...
</select>
```

| 写法 | 含义 |
|------|------|
| `id="userWithAddresses"` | 这张映射的名字。`<select resultMap="userWithAddresses">` 必须和它一致 |
| `type="...User"` | 最终装出来的对象是 `User` |
| `property` | Java 字段名，驼峰，例如 `createTime` |
| `column` | SQL 结果里的列名。下划线列要写出来，例如 `create_time` |
| `<id>` | 用哪一列判断是不是同一个对象。用户用 `user_id`，地址用 `address_id` |
| `<collection property="addresses">` | `User` 里的 `List<Address>`。同一用户的多行收进这个列表 |
| `ofType` | 列表里每个元素的类型，这里是 `Address` |

两张表都有 `id`。SQL 里必须写成 `u.id AS user_id` 和 `a.id AS address_id`。如果两列都叫 `id`，地址的 id 会把用户的 id 盖掉。

`LEFT JOIN` 保证用户没有地址时，用户这一行还在，`addresses` 是空列表。改成 `INNER JOIN` 后，没有地址的用户不会出现在结果里。

接口是 `GET /api/v1/users/{id}/addresses`。Controller、Service、Repository 只是把这个 id 传到 `UserMapper.findByIdWithAddresses`。

## 4. 第一次成功响应里没有 `data`

第一次访问返回的是：

```text
{"code":20000,"msg":"操作成功"}
```

没有 `data`。查询结果是 `null`，`Result.success(null)` 把 `data` 设成了 `null`。Fastjson 默认不写出 `null` 字段，所以 JSON 里只剩 `code` 和 `msg`。

查询条件包含 `u.is_deleted = 0`。用户不存在，或者已经被软删除，就一行都没有。地址表里即使有 `user_id = 1`，人也不会被带出来。换成一个未删除用户，并把地址的 `user_id` 改成这个人，才得到上面的两条地址。

`UserService.getUserWithAddresses` 现在是直接把 Mapper 的结果返回。结果为 `null` 时，接口仍然是 `20000`。要和按 id 查询一样，应在这里判断 `null` 并抛 `BusinessException(ResultCodeEnum.USER_NOT_FOUND)`。

## 5. 这周两条 SQL 写法对照

| 场景 | 用哪种 | 原因 |
|------|--------|------|
| `WHERE id = ?`、姓名、年龄、性别 | `#{}` | 值由数据库绑定，用户输入进不了 SQL 结构 |
| `ORDER BY` 的列名 | `${}`，并且列名先做白名单 | 列名不能当成带引号的字符串 |
| 一个用户对应多行地址 | `resultMap` 加 `<collection>` | `resultType` 不能把多行收成一个列表 |
| 用户可以没有地址 | `LEFT JOIN` | 没有地址时仍然返回这个用户 |
