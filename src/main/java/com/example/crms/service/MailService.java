package com.example.crms.service;

import javax.mail.MessagingException;

public interface MailService {
    boolean sendMail(String to, String subject, String content) throws MessagingException;
}
