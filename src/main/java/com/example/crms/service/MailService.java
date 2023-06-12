package com.example.crms.service;

import javax.mail.MessagingException;

public interface MailService {
    void sendMail(String to, String subject, String content) throws MessagingException;
}
