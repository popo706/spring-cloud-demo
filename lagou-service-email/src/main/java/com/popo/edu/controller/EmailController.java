package com.popo.edu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final JavaMailSender mailSender;

    /**
     * 发送邮件者
     */
    @Value("${spring.mail.username}")
    private String username;

    @GetMapping("/{email}/{code}")
    public boolean sendEmail(@PathVariable("email") String email, @PathVariable("code") String code) {
        try {
            log.info("发送邮件,email:{},code:{}",email,code);
            MimeMessage message = mailSender.createMimeMessage();
            // 如果发送带有html格式的邮件，设置utf-8或GBK编码，否则邮件可能会有乱码
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(username); // 发送者
            helper.setTo(new String[]{email});// 接受者
            helper.setSubject("注册验证码");// 主题
            helper.setSentDate(new Date());// 发送时间
            helper.setText("验证码为：" + code, true);// 发送内容，设置true可以发送带有html格式的邮件

            //开始发送邮件
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
