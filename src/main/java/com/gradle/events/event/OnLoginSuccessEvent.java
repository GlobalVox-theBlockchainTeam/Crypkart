package com.gradle.events.event;

import com.gradle.entity.user.User;
import com.gradle.util.LocaleHelper;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnLoginSuccessEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    /**
     * We are registering event after registration complete
     *
     * @param user   : Current user
     * @param locale : Current locale
     * @param appUrl : Application url
     */
    public OnLoginSuccessEvent(
            User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
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
}