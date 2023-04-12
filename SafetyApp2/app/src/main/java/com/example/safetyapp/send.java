package com.example.safetyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class send extends AsyncTask<Void,Void,Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private final String email;
    private final String subject;


    public send( String email, String subject) {
        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrr]");
        this.email = email;
        this.subject = subject;



    }



    @Override
    protected Void doInBackground(Void... voids) {
        final String username = "aayushviswase008@gmail.com";
        String password = "Safety@123";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);

                    }
                });

        try {
            Message mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress("aayushviswase008@gmail.com"));
            mm.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            mm.setSubject(subject);
            mm.setText("I need help. Please contact me as soon as possible.");
                    Transport.send(mm);


        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}