package com.example.crazyjava.controller;

import com.example.crazyjava.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/${api.version}/files")
public class FileController {

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        Path dir = Paths.get("uploads");
        Files.createDirectories(dir);
        String original = file.getOriginalFilename() == null ? "unknown" : Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path target = dir.resolve(UUID.randomUUID() + "_" + original);
        file.transferTo(target);
        return Result.success(target.toAbsolutePath().toString());
    }
}