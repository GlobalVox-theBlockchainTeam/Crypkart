/*
 * Copyright (c) 25/4/18 2:44 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.listeners;

import com.gradle.entity.user.User;
import com.gradle.events.event.OnRegistrationCompleteEvent;
import com.gradle.events.event.OnSubUserCreateCompleteEvent;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SubUserCreateListener implements
        ApplicationListener<OnSubUserCreateCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * On sub user will be created, this listener will be called
     * We have registered it in
     * @see OnSubUserCreateCompleteEvent
     *
     * @param event : Current Event (in our case Registration complete)
     */
    @Override
    public void onApplicationEvent(OnSubUserCreateCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnSubUserCreateCompleteEvent event) {
        User user = event.getUser();

        String token = Common.generateToken();
        //service.createVerificationToken(user, token);
        userService.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String message = "Your account has been created. your username : " + user.getUsername() + " and password : " + event.getPassword();
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);

        // send email to parent as well
        email.setTo(user.getParent().getEmail());
        email.setText("Child user has been created. your username : " + user.getUsername() + " and password : " + event.getPassword());
        mailSender.send(email);
    }
}
