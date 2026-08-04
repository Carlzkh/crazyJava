package com.example.crazyjava.controller;


import com.example.crazyjava.Gender;
import com.example.crazyjava.common.PageResult;
import com.example.crazyjava.common.Result;
import com.example.crazyjava.entity.User;
import com.example.crazyjava.service.UserService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;


    @GetMapping
    public Result<PageResult<User>> getUsersByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        log.info("===== 访问了 /users 接口，当前时间：" + new java.util.Date() + " =====");
        log.info("Controller 层：分页查询，page={}, size={},name={}", page, size, name);
        PageInfo<User> pageInfo = userService.getUserListByPageAndName(page, size, name);
        PageResult<User> pageResult = new PageResult<>(pageInfo);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable long id) {
        log.info("Controller 层：根据 ID 查询用户: {}", id);
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @GetMapping("/search")
    public Result<List<User>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate){
        log.info("Controller 层：综合搜索，name={}, minAge={}, maxAge={}, gender={}, startDate={}, endDate={}", name, minAge, maxAge, gender,startDate,endDate);
        if(minAge != null && maxAge!=null && minAge>maxAge){
            return Result.error(400,"最小年龄不能大于最大年龄");
        }
        // gender 使用枚举 Gender，非法值由全局异常处理器统一返回 400
        List<User> users = userService.searchUsers(name,minAge,maxAge,gender,startDate,endDate);
        return Result.success(users);
    }

    @PutMapping("/{id}")
    public Result<User> setUserById(@PathVariable Long id, @Valid @RequestBody User user) {
        log.info("Controller 层：根据 ID 查询用户: {}", id);

        User newUser = userService.setUserById(id, user);
        return Result.success(newUser);
    }

    @DeleteMapping("/batch")
    public Result<Integer> deleteUserBatch(@RequestBody List<Long> ids) {
        log.info("Controller 层：删除用户 ID: {}", ids);
        int deletedCount = userService.deleteUsersByIds(ids);
        return Result.success("成功删除 " + deletedCount + " 个用户", deletedCount);
    }

    // ========== 新增：删除用户 ==========
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("Controller 层：删除用户 ID: {}", id);
        userService.deleteUserById(id);
        return Result.success("用户删除成功", null);
    }




    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user) {
        log.info("新增user的入参 {}", user);
        User newUser = userService.createUser(user);
        log.info("新增成功{}", newUser);
        return Result.success("创建用户成功", newUser);


    }

    @GetMapping("/export")
    public void exportUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) throws IOException {

        log.info("Controller 层：导出用户数据，参数：name={}, minAge={}, maxAge={}, gender={}, startDate={}, endDate={}",
                name, minAge, maxAge, gender, startDate, endDate);

        // 1. 查询数据（复用 search 逻辑）
        List<User> users = userService.searchUsers(name, minAge, maxAge, gender, startDate, endDate);

        // 2. 设置响应头，告诉浏览器这是一个 CSV 文件
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment; filename=users_" + LocalDate.now() + ".csv");

        // 3. 写入 CSV 内容
        try (PrintWriter writer = response.getWriter()) {
            // 写入 CSV 表头
            writer.println("ID,姓名,年龄,性别,创建时间,更新时间");

            // 写入数据行
            for (User user : users) {
                String line = String.format("%d,%s,%d,%s,%s,%s",
                        user.getId(),
                        escapeCsv(user.getName()),
                        user.getAge(),
                        user.getGender() != null ? user.getGender().name() : "",
                        user.getCreateTime() != null ? user.getCreateTime().toString() : "",
                        user.getUpdateTime() != null ? user.getUpdateTime().toString() : ""
                );
                writer.println(line);
            }
        }

        log.info("Controller 层：导出完成，共 {} 条数据", users.size());
    }
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

}
