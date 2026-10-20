# 第 6 周：下载、上传、跨域

对照代码：

- `UserController.exportUsers`：`GET /api/v1/users/export`
- `FileController.upload`：`POST /api/v1/files/upload`
- `WebMvcConfig.addCorsMappings`
- `LogAspect.getRequestParams` 里对 `MultipartFile` 的处理

这一周做完的练习：用 Python 把 `demo.txt` 传到服务器，文件出现在项目下的 `uploads` 目录。再带上 `Origin` 头验证跨域：`http://localhost:3000` 被允许，`http://localhost:3001` 返回 `Invalid CORS request`。

## 1. 下载：服务器把文件交给浏览器

导出接口不返回 `Result`，方法参数里有 `HttpServletResponse`，内容直接写进响应。

```java
response.setContentType("text/csv;charset=UTF-8");
response.setHeader("Content-Disposition", "attachment; filename=users_" + LocalDate.now() + ".csv");
```

| 响应头 | 作用 |
|--------|------|
| `Content-Type: text/csv;charset=UTF-8` | 告诉对方正文是 CSV 文本，编码是 UTF-8 |
| `Content-Disposition: attachment` | 告诉浏览器不要把内容显示成网页，而是下载 |
| `filename=users_日期.csv` | 下载时用的文件名 |

后面的 `PrintWriter` 才是文件内容。第一行是表头，后面每一行是一个用户。姓名里如果有逗号或引号，`escapeCsv` 会把它包进引号，避免这一行被拆成多列。

`throws IOException` 是因为 `response.getWriter()` 是检查异常。写出流用了 `try-with-resources`，方法结束时流会关闭。

## 2. 上传：浏览器把文件交给服务器

上传不能用 `@RequestBody`。`@RequestBody` 接的是一整段 JSON。文件上传的请求类型是 `multipart/form-data`，Spring 用 `MultipartFile` 接其中的一个字段。

```java
@PostMapping("/upload")
public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException
```

`@RequestParam("file")` 里的 `"file"` 是表单字段名，必须和客户端的字段名一致。它不是文件名。

保存时做了三件事：

| 代码 | 作用 |
|------|------|
| `Files.createDirectories(dir)` | `uploads` 目录不存在就创建。已存在不会报错 |
| `Paths.get(original).getFileName()` | 只留文件名最后一段。客户端若传来 `..\其他目录\a.txt`，不会写到 `uploads` 外面 |
| `UUID + "_" + original` | 两次上传同名文件时不会互相覆盖 |
| `file.transferTo(target)` | 把上传内容写到这个路径 |
| `target.toAbsolutePath()` | 返回磁盘上的完整路径，放进 `Result.data` |

空文件在进保存之前就返回 `Result.error(400, "文件不能为空")`。

Spring Boot 2.7 默认单个文件大约不超过 1MB，整个请求大约不超过 10MB。更大的文件还没进 `upload` 就会被拒绝。要放宽时，在配置文件里写 `spring.servlet.multipart.max-file-size` 和 `spring.servlet.multipart.max-request-size`。

## 3. 用 Python 发上传请求

```python
import requests

url = "http://localhost:8080/api/v1/files/upload"

with open("demo.txt", "rb") as f:
    response = requests.post(url, files={"file": f})

print(response.status_code)
print(response.text)
```

`files={"file": f}` 会自动变成 `multipart/form-data`。文件要用 `"rb"` 二进制打开。成功时状态码是 200，`code` 是 `20000`，`data` 是类似下面的路径：

```text
C:\Users\kunhe.zhang\IdeaProjects\github\crazyJava\uploads\679466fb-881b-4236-801d-cb17289a6374_demo.txt
```

## 4. 第一次上传失败：切面把 MultipartFile 交给了 Fastjson

第一次调用时，接口还没执行到 `transferTo`，全局异常处理器就报了运行时异常。堆栈停在 `LogAspect.getRequestParams`。

切面在 `joinPoint.proceed()` 之前，会把 Controller 方法的第一个普通参数交给 `JSON.toJSONString`。上传方法的第一个参数是 `MultipartFile`。这个对象当时还在内存里，没有磁盘路径。Fastjson 去读它的 `resource` 字段，抛出 `FileNotFoundException`。异常发生在进 `upload` 之前，所以文件没有保存。

现在遇到 `MultipartFile` 只记录文件名和大小，不再把整个对象转成 JSON。其他参数序列化失败时，改成 `String.valueOf`，日志不再把接口打挂。

| 日志里的「请求 Body」 | 实际是什么 |
|-----------------------|------------|
| 分页 GET 打出 `1` | 方法第一个参数是 `page`，默认值是 1，不是请求体 |
| 上传打出 `文件名=demo.txt, 大小=...` | `MultipartFile` 的文件名和字节数，不是文件内容 |

## 5. 跨域限制的是浏览器，不是 Python

浏览器把「协议 + 主机 + 端口」都相同才当成同一个来源。页面在 `http://localhost:3000`，接口在 `http://localhost:8080`，端口不同，就是跨域。

`WebMvcConfig` 里现在的配置：

```java
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:3000")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");
```

`allowedOrigins` 写的是页面所在的地址，不是接口自己的 `8080`。`/api/**` 只放开接口。

用 Python 做了三次对照：

| 请求 | 结果 | 原因 |
|------|------|------|
| 不带 `Origin` | 上传成功，`code` 是 `20000` | Spring 不把它当成跨域请求，Controller 正常执行 |
| `Origin: http://localhost:3000` | 上传成功，响应头有 `Access-Control-Allow-Origin: http://localhost:3000` | 这个来源在允许名单里 |
| `Origin: http://localhost:3001` | 响应是 `Invalid CORS request`，文件不会保存 | 带了 `Origin`，但不在允许名单里。Spring 在进 Controller 之前就拒绝 |

Python 的 `requests` 自己不检查跨域。不带 `Origin` 时，两次上传看起来一样成功。差别要看响应头，或者故意写一个未被允许的 `Origin`。

浏览器从页面里用 JavaScript 调用接口时，会自动加上 `Origin`。来源不在名单里，结果和第三行一样。来源在名单里，浏览器才把响应交给页面脚本。服务器已经处理完、但响应头不允许这个来源时，页面脚本仍然读不到结果。这次的配置更严：不在名单里的 `Origin`，请求直接被拒绝，Controller 不会执行。

## 6. 下载、上传、跨域对照

| 对比项 | 下载 `export` | 上传 `upload` | 跨域 |
|--------|----------------|---------------|------|
| 方向 | 服务器写给客户端 | 客户端写给服务器 | 浏览器是否把响应交给另一个来源的页面脚本 |
| 地址 | `GET /api/v1/users/export` | `POST /api/v1/files/upload` | 不单独占一个地址，看的是请求头 `Origin` |
| 参数 | `@RequestParam` 筛选条件 | `@RequestParam("file") MultipartFile` | `Origin: http://localhost:3000` |
| 成功时客户端看到什么 | 浏览器弹出下载，文件名是 `users_日期.csv` | JSON 里的 `data` 是磁盘绝对路径 | 响应头里有 `Access-Control-Allow-Origin` |
| 不配这项会怎样 | 浏览器可能把 CSV 直接显示成文本 | 不能用 `@RequestBody` 接文件，字段名不对则 `file` 为空 | 带了未被允许的 `Origin` 时，本项目返回 `Invalid CORS request` |
