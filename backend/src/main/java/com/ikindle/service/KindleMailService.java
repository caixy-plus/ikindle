package com.ikindle.service;

import com.ikindle.entity.Book;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class KindleMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${ikindle.upload.local-dir:/data/ikindle/uploads}")
    private String localUploadDir;

    public void sendToKindle(String kindleEmail, Book book) throws MessagingException {
        if (kindleEmail == null || kindleEmail.isBlank()) {
            throw new IllegalArgumentException("Kindle邮箱未设置");
        }
        if (book.getFileUrl() == null || book.getFileUrl().isBlank()) {
            throw new IllegalArgumentException("电子书文件不存在: " + book.getTitle());
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(kindleEmail);
        helper.setSubject("convert");
        helper.setText("");

        String fileName = book.getTitle() + "." + (book.getFileFormat() != null ? book.getFileFormat() : "mobi");
        InputStreamSource attachment = resolveAttachment(book);
        helper.addAttachment(fileName, attachment);

        mailSender.send(message);
        log.info("Kindle推送成功: book={} -> {}", book.getTitle(), kindleEmail);
    }

    private InputStreamSource resolveAttachment(Book book) {
        String fileUrl = book.getFileUrl();
        try {
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                return new UrlResource(new URL(fileUrl));
            }
            File file = new File(fileUrl);
            if (file.exists()) {
                return new FileSystemResource(file);
            }
            File relativeFile = new File(localUploadDir, fileUrl);
            if (relativeFile.exists()) {
                return new FileSystemResource(relativeFile);
            }
            throw new IllegalArgumentException("无法找到电子书文件: " + fileUrl);
        } catch (IOException e) {
            throw new IllegalArgumentException("读取电子书文件失败: " + fileUrl, e);
        }
    }
}
