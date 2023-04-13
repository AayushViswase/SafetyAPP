package com.example.safetyapp;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

public class SendEmailTask {

    public static CompletableFuture<Boolean> sendEmail(String username, String password, String recipientEmail, String subject, String messageBody) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        System.out.println("sendmailtask called");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress("aayushviswase008@gmail.com"));
            mm.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            mm.setSubject(subject);
            mm.setText("I need help. Please contact me as soon as possible.");
                    Transport.send(mm);

            result.complete(true);
        } catch (MessagingException e) {
            e.printStackTrace();
            result.complete(false);
        }

        return result;
    }
}
