package com.example.safetyapp;

import android.location.Location;
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

    public static CompletableFuture<Boolean> sendEmail(String username, String password, String recipientEmail, String subject, Location location) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
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
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("aayushviswase008@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            String messageText = "I need help. Please contact me as soon as possible.\n" +
                    "Please help me! I am in danger.\n" +
                    "My current location is:\n" +
                    "Latitude: " + location.getLatitude() + "\n" +
                    "Longitude: " + location.getLongitude();
            message.setText(messageText);
            Transport.send(message);

            result.complete(true);

        } catch (MessagingException e) {
            e.printStackTrace();
            result.complete(false);
        }

        return result;
    }
}
