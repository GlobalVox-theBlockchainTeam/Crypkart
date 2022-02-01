/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.listeners;

import com.gradle.entity.Mail;
import com.gradle.events.event.OnRegistrationCompleteEvent;
import com.gradle.entity.user.User;
import com.gradle.exception.handler.CoinmartException;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.mail.EmailService;
import com.gradle.util.Common;
import com.gradle.util.ServiceUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ServiceUtil serviceUtil;

    public static final Logger logger = Logger.getLogger(RegistrationListener.class);

    /**
     * On registration complete this listener will be called
     * We have registered it in OnRegistrationCompleteEvent
     *
     * @param event : Current Event (in our case Registration complete)
     */
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {

        try {
            User user = event.getUser();
            String token = Common.generateToken();
            //service.createVerificationToken(user, token);
            userService.createVerificationToken(user, token);
            String recipientAddress = user.getEmail();
            String subject = "Registration Confirmation";
            String confirmationUrl
                    = event.getAppUrl() + "/user/registration/email/verification?token=" + token;
            String message = messages.getMessage("message.regSucc", null, event.getLocale());


            Mail mail = new Mail();
            mail.setSubject(message);
            mail.setFrom("jobs@bitwiseonline.com");
            mail.setTo(recipientAddress);
            Map<String, Object> model = new HashMap<>();
            model.put("name", user.getFirstName());
            model.put("verificationToken", token);
            model.put("signature", "Regards<br/>Crypkart");
            model.put("location", "Ahmedabad");

            mail.setModel(model);
            try {
                emailService.sendMailWithInline(mail);
            } catch (Exception e) {
                logger.error(e.getMessage() + e.getStackTrace());
            }

        }catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
       /* SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + "http://127.0.0.1:8880" + confirmationUrl);
        mailSender.send(email);*/
    }
}
