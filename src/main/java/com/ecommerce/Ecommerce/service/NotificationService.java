package com.ecommerce.Ecommerce.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class NotificationService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;

    @Value("${twilio.sms.from}")
    private String smsFrom;

    @Autowired
    JavaMailSender javaMailSender;

    public void sendMessage(String to,String message){
        try{
            Twilio.init(accountSid,authToken);
            Message.creator(
                    new PhoneNumber("whatsapp:" + to),
                    new PhoneNumber(whatsappFrom),
                    message
            ).create();
            System.out.println("Whatsapp message is sent " + to);
        }catch (Exception e){
            throw new RuntimeException("Whatsapp message sending failed!!",e);
        }

    }

    public void sendSMS(String to,String message){
        try{
            Twilio.init(accountSid,authToken);
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(smsFrom),
                    message
            ).create();
            System.out.println("SMS message is sent " + to);

        }catch (Exception e){
            throw new RuntimeException("SMS message sending failed..!" , e);
        }
    }

    public void sendEmail(String toEmail,String filePath){
        try{
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setTo(toEmail);
            helper.setSubject("Current stock detail");
            helper.setText("Product details");


            File file = new File(filePath);
            if(!file.exists() || file.length() == 0){
                throw new RuntimeException("CSV file is missing and empty.");
            }
            FileSystemResource resource = new FileSystemResource(file);

            helper.addAttachment(file.getName(),resource);

            javaMailSender.send(message);

            System.out.println("Email sent successfully!"+toEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void sendTextEmail(String toEmail,String msg){
        try{
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setTo(toEmail);
            helper.setSubject("Bill or payments details");
            helper.setText(msg);


//            File file = new File(filePath);
//            if(!file.exists() || file.length() == 0){
//                throw new RuntimeException("CSV file is missing and empty.");
//            }
//            FileSystemResource resource = new FileSystemResource(file);
//
//            helper.addAttachment(file.getName(),resource);

            javaMailSender.send(message);

            System.out.println("Email sent successfully!"+toEmail);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
