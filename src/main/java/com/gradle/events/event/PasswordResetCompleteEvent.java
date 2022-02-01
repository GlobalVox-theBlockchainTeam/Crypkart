/*
 * Copyright (c) 24/4/18 5:41 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.event;

import com.gradle.entity.user.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;


/**
 * This is an event : password reset complete
 */
public class PasswordResetCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
    private String token;

    /**
     * We are registering event after registration complete
     *
     * @param user   : Current user
     * @param locale : Current locale
     * @param appUrl : Application url
     * @param token  : verification token
     */
    public PasswordResetCompleteEvent(
            User user, Locale locale, String appUrl, String token) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.token = token;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
