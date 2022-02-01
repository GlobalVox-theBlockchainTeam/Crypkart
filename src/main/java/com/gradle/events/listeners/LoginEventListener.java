/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.listeners;

import com.gradle.entity.Mail;
import com.gradle.entity.user.User;
import com.gradle.events.event.OnLoginSuccessEvent;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.mail.EmailService;
import com.gradle.util.ActiveSessionManager;
import com.gradle.util.Common;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Whenever user logs in this event will be called to store users in ActiveSessionManager
 *
 * @see ActiveSessionManager
 */
@Component
public class LoginEventListener implements  ApplicationListener<OnLoginSuccessEvent>{

    @Autowired
    private ActiveSessionManager activeSessionManager;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private EmailService emailService;
    public static final Logger logger = Logger.getLogger(RegistrationListener.class);


    /**
     * @param event : login event
     */
    @Override
    public void onApplicationEvent(OnLoginSuccessEvent event) {
        activeSessionManager.add(event.getUser().getUsername());
//        sendLoginSuccessEmail(event);
    }



    private void sendLoginSuccessEmail(OnLoginSuccessEvent event) {

        try {
            User user = event.getUser();
            String token = Common.generateToken();
            //service.createVerificationToken(user, token);
            String recipientAddress = user.getEmail();
            String message = messages.getMessage("message.regSucc", null, event.getLocale());
            event.getLocale().getDisplayCountry();



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
                emailService.sendMailAtLogin(mail);
            } catch (Exception e) {
                logger.error(e.getMessage() + e.getStackTrace());
            }

        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
       /* SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + "http://127.0.0.1:8880" + confirmationUrl);
        mailSender.send(email);*/
    }

}
