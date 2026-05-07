package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.util.OssUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * 文件上传 / 静态访问
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final OssUtil ossUtil;

    /**
     * 通用上传 (登录用户)
     */
    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                                   @RequestParam(defaultValue = "common") String category) {
        String url = ossUtil.upload(file, category);
        return ApiResponse.success(Map.of("url", url, "filename", file.getOriginalFilename()));
    }

    /**
     * 管理端专用上传(图书文件)
     */
    @PostMapping("/upload/book")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, String>> uploadBook(@RequestParam("file") MultipartFile file) {
        String url = ossUtil.upload(file, "book");
        return ApiResponse.success(Map.of("url", url, "filename", file.getOriginalFilename(),
                "size", String.valueOf(file.getSize())));
    }

    /**
     * 静态文件访问 (本地存储模式)
     */
    @GetMapping("/public/**")
    public ResponseEntity<Resource> serve(jakarta.servlet.http.HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String prefix = contextPath + "/files/public/";
        if (!requestUri.startsWith(prefix)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        String relative = requestUri.substring(prefix.length());
        Path path = ossUtil.resolveLocalPath(relative);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
        }
        FileSystemResource resource = new FileSystemResource(path);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(resource);
    }
}
