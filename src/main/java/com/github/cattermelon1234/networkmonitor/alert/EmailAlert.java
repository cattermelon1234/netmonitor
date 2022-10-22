package com.github.cattermelon1234.networkmonitor.alert;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;
import javax.mail.Transport;

public class EmailAlert implements Alert {
    // email ID of Recipient.
    String recipient = "recipient@gmail.com";

    // email ID of  Sender.
    String sender = "sender@gmail.com";

    // using host as localhost
    String host = "127.0.0.1";

    // creating session object to get properties
    Session session;

    public EmailAlert(String host, String sender, String recipient) {
        this.host = host;
        this.sender = sender;
        this.recipient = recipient;

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        this.session = Session.getDefaultInstance(properties);
    }

    public void alert(String text) {
        try
        {
            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(sender));

            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            // Set Subject: subject of the email
            message.setSubject("Anomalous traffic detected");

            // set body of the email.
            message.setText(text);

            // Send email.
            Transport.send(message);
            System.out.println("Mail successfully sent");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void clearAlert(String text) {
        try
        {
            // MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From Field: adding senders email to from field.
            message.setFrom(new InternetAddress(sender));

            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            // Set Subject: subject of the email
            message.setSubject("Alert cleared");

            // set body of the email.
            message.setText(text);

            // Send email.
            Transport.send(message);
            System.out.println("Mail successfully sent");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
