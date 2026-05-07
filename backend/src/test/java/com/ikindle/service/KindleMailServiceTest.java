package com.ikindle.service;

import com.ikindle.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KindleMailServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void sendToKindle_shouldSendRealEmailToTestMailbox() throws Exception {
        // 手动配置 JavaMailSender（无需 Spring Boot 上下文）
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setUsername("18659195904@163.com");
        mailSender.setPassword("ZCbanwr5WefbQUeL");
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setProtocol("smtp");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        mailSender.setJavaMailProperties(props);

        KindleMailService kindleMailService = new KindleMailService(mailSender);
        ReflectionTestUtils.setField(kindleMailService, "fromAddress", "18659195904@163.com");
        ReflectionTestUtils.setField(kindleMailService, "localUploadDir", tempDir.toString());

        // 创建临时电子书文件作为附件
        File bookFile = tempDir.resolve("test-book.txt").toFile();
        try (FileWriter writer = new FileWriter(bookFile)) {
            writer.write("This is a test e-book content for Kindle push.\n");
            writer.write("Sender: 18659195904@163.com\n");
            writer.write("Recipient: 691788300@qq.com\n");
        }

        Book book = new Book();
        book.setTitle("测试电子书");
        book.setFileUrl(bookFile.getAbsolutePath());
        book.setFileFormat("txt");

        // 收件人使用用户指定的测试邮箱
        String recipient = "691788300@qq.com";

        assertDoesNotThrow(() -> kindleMailService.sendToKindle(recipient, book));
    }
}
