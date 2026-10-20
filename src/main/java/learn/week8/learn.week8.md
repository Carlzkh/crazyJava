# 第 8 周：事务

对照代码：

- `UserService.testTransaction(Long id)`，方法上有 `@Transactional`
- `UserController` 的 `POST /api/v1/users/test-tx`
- `UserService.testTransactionBySelf`，里面写的是 `this.testTransaction(id)`
- `POST /api/v1/users/test-tx-self`
- 启动时的 `testTransaction()`，方法上是 `@PostConstruct`，没有事务

这一周用 id 为 1 的用户做了两次实验。删除执行的是 `update user set is_deleted=1`，不是物理删除。

| 调用方式 | 接口返回 | 接着查询这个用户 |
|----------|----------|------------------|
| Controller 直接调用 `testTransaction` | `500`，消息是「模拟异常，事务应回滚」 | `20000`，用户还在，`name` 仍是 `prod` |
| 同一个类里 `this.testTransaction(id)` | 同样是 `500` 和同一句消息 | `60000`，用户不存在。软删除留在了库里 |

接口返回 500 只说明方法失败了，不能说明数据库有没有撤销。要用随后的查询，或者直接看 `is_deleted`。

## 1. 事务包住的是哪一次调用

`testTransaction(Long id)` 先改数据库，方法还没正常结束就抛运行时异常：

```java
@Transactional(rollbackFor = Exception.class)
public void testTransaction(Long id) {
    userRepository.deleteById(id);
    if (true) {
        throw new RuntimeException("模拟异常，事务应回滚");
    }
}
```

`POST /test-tx` 由 Controller 调用 `userService.testTransaction(id)`。这是从类的外面进来的一次调用，事务包住整个方法。抛出异常后，方法里的 `is_deleted=1` 被撤销，用户还能查到。

`rollbackFor = Exception.class` 表示：抛出的只要是 `Exception`，就撤销。不写 `rollbackFor` 时，默认只撤销运行时异常和 `Error`。这次抛的是 `RuntimeException`，两种写法都会撤销。检查异常如果被声明抛出，又不写 `rollbackFor`，默认不会撤销。

异常如果在这个方法内部被 `catch` 住，并且没有继续向外抛，方法看起来是正常结束的，已经做出的修改会保留。

## 2. 同一个类里自己调用自己

`testTransactionBySelf` 自己没有事务注解，它再去调用带注解的方法：

```java
public void testTransactionBySelf(Long id) {
    this.testTransaction(id);
}
```

`POST /test-tx-self` 从外面进来时，包住的是 `testTransactionBySelf`。这个方法上没有事务。它里面的 `this.testTransaction(id)` 走的是这个对象自己的方法，不会再套上一层事务。所以删除真的留下了，接着查询得到 `60000 用户不存在`。

| 对比项 | `POST /test-tx` | `POST /test-tx-self` |
|--------|-----------------|----------------------|
| 外部调用的方法 | `testTransaction` | `testTransactionBySelf` |
| 这个方法上有没有事务 | 有 | 没有 |
| 删除是在哪一步执行的 | 就在这个带事务的方法里 | 类内部又调用了 `testTransaction` |
| 异常之后 `is_deleted` | 仍是 `0` | 变成 `1` |

要让「删除再加上别的修改」一起成功或一起撤销，就把这些修改写进同一个被外部调用、并且标了 `@Transactional` 的方法里。不要在一个没有事务的方法里，用 `this` 去调用另一个带事务的方法，并指望里面的修改会撤销。

## 3. 启动时那个同名方法不会回滚

还有一个无参的 `testTransaction()`，它和上面两个不是同一次实验：

```java
@PostConstruct
public void testTransaction() {
    try {
        userRepository.deleteById(1L);
        if (true) {
            throw new RuntimeException("模拟异常，事务回滚");
        }
    } catch (Exception e) {
        log.error("事务测试失败，已回滚: {}", e.getMessage());
    }
}
```

| 对比项 | 这个启动方法 | `testTransaction(Long id)` |
|--------|----------------|----------------------------|
| 何时执行 | 每次启动，`@PostConstruct` | 只有调用接口时 |
| 有没有事务注解 | 没有 | 有 |
| 谁执行删除 | 直接调用 `userRepository.deleteById` | 在带事务的方法里调用 |
| 异常怎么处理 | 方法内部 `catch` 掉了 | 抛出方法外面 |
| 结果 | 日志写着「已回滚」，删除其实会留下 | 从 Controller 直接调用时，删除会撤销 |

日志里的「已回滚」只是这句 `log.error` 的文字，数据库不会因为这行日志撤销。做事务实验前应去掉这个 `@PostConstruct`，否则每次启动都会把 id 为 1 的用户标成已删除。

## 4. 实验之后恢复数据

`/test-tx-self` 已经把测试用户软删除。在当前连接的库里执行：

```sql
UPDATE user SET is_deleted = 0 WHERE id = 1;
```

`spring.profiles.active=prod` 时，这个库是 `prod`。
