/*
 * Copyright (c) 25/4/18 2:43 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.event;

import com.gradle.entity.user.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;


/**
 * This is an event : for sub user creation
 */
public class OnSubUserCreateCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
    private String password;

    /**
     * We are registering event after sub user creation complete
     *
     * @param user   : Current user
     * @param locale : Current locale
     * @param appUrl : Application url
     * @param password : generated password for the user
     */
    public OnSubUserCreateCompleteEvent(
            User user, Locale locale, String appUrl, String password) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
