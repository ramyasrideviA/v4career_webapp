package com.learn.learningarea.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML content
            
            mailSender.send(message);
        } catch (Exception e) {
         //  System.err.println("Failed to send email: " + e.getMessage());
          e.printStackTrace();  
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachmentData, String attachmentFilename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            
            org.springframework.core.io.ByteArrayResource byteArrayResource = new org.springframework.core.io.ByteArrayResource(attachmentData);
            helper.addAttachment(attachmentFilename, byteArrayResource);
            
            mailSender.send(message);
        } catch (Exception e) {
          //  System.err.println("Failed to send email with attachment: " + e.getMessage());
             e.printStackTrace();
        }
    }

    public void sendOtpEmail(String to, String otp, String name) {
        String subject = "Your OTP for Learning Area";
        String body = "Hello " + name + ",\n\n" +
                      "Your One Time Password (OTP) for registration/reset is: " + otp + "\n\n" +
                      "This OTP is valid for 5 minutes.\n\n" +
                      "Regards,\nLearning Area Team";
        
        sendEmail(to, subject, body);
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
