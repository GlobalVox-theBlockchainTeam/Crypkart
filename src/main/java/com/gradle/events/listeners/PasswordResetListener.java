/*
 * Copyright (c) 24/4/18 5:42 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.listeners;

import com.gradle.entity.user.User;
import com.gradle.events.event.OnRegistrationCompleteEvent;
import com.gradle.events.event.PasswordResetCompleteEvent;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Password reset event
 */
@Component
public class PasswordResetListener implements
        ApplicationListener<PasswordResetCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * On password reset event this listener will be called
     * We have registered it in
     * @see PasswordResetCompleteEvent
     *
     * @param event : Current Event (in our case Password Reset)
     */
    @Override
    public void onApplicationEvent(PasswordResetCompleteEvent event) {
        this.resetPassword(event);
    }

    private void resetPassword(PasswordResetCompleteEvent event) {
        User user = event.getUser();
        String token = event.getToken();
        //service.createVerificationToken(user, token);
        //userService.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "Password reset";
        String confirmationUrl = event.getAppUrl() + "/user/password/reset/verification/" + token;
        String message = messages.getMessage("message.regSucc", null, event.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + "http://127.0.0.1:8880" + confirmationUrl);
        mailSender.send(email);
    }
}
