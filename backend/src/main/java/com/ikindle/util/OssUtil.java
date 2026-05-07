package com.ikindle.util;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * 文件存储工具
 * 默认走本地磁盘 (K8s 挂 PVC),可通过配置切换到 OSS
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssUtil {

    @Value("${ikindle.oss.enabled:false}")
    private boolean ossEnabled;

    @Value("${ikindle.upload.local-dir:/data/ikindle/uploads}")
    private String localDir;

    @Value("${ikindle.upload.public-base-url:/api/files/public}")
    private String publicBaseUrl;

    @Value("${ikindle.upload.allowed-types:jpg,jpeg,png,gif,pdf,epub,mobi}")
    private String allowedTypes;

    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;

    @PostConstruct
    public void init() throws IOException {
        Path dir = Paths.get(localDir);
        Files.createDirectories(dir);
        log.info("文件存储目录: {} (OSS={})", dir.toAbsolutePath(), ossEnabled);
    }

    /**
     * 上传文件,返回可访问 URL
     * @param category 分类目录:cover/book/avatar
     */
    public String upload(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
                : "";
        Set<String> allowed = Set.of(allowedTypes.toLowerCase().split(","));
        if (!allowed.contains(ext)) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }

        String date = LocalDate.now().toString();
        String filename = UUID.randomUUID() + "." + ext;
        String relativePath = category + "/" + date + "/" + filename;

        try {
            Path target = Paths.get(localDir).resolve(relativePath);
            Files.createDirectories(target.getParent());
            file.transferTo(target.toFile());
            return publicBaseUrl + "/" + relativePath;
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }
    }

    /**
     * 根据 publicBaseUrl 后的相对路径解析本地文件
     */
    public Path resolveLocalPath(String relativePath) {
        return Paths.get(localDir).resolve(relativePath).normalize();
    }

    public String getLocalDir() {
        return localDir;
    }
}
