package com.rubinho.vkproxy.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MailService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;


    @Async
    public void sendActivation(String toEmail, String text) {
        sendEmail(toEmail, "Код для активации", text);
    }


    @Async
    public void sendRestorePassword(String toEmail, String text) {
        sendEmail(toEmail, "Код для сброса пароля", text);
    }

    public void sendEmail(String toEmail, String subject, String text) {
        System.out.println(fromEmail + " " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setSubject(subject);
        message.setTo(toEmail);
        message.setText(text);

        mailSender.send(message);

    }

}
