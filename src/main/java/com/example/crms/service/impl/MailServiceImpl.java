package com.example.crms.service.impl;

import com.example.crms.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    /**
     * 发送邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    public boolean sendMail(String to, String subject, String content) throws MessagingException {
        // 创建邮件消息
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // 设置发件人
        helper.setFrom("321434162@qq.com");
        // 设置收件人
        helper.setTo(to);
        // 设置主题
        helper.setSubject(subject);
        // 设置内容
        helper.setText(content, true);
        try{
            // 发送邮件
            mailSender.send(message);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


}
