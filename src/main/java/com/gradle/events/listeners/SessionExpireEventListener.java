/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.listeners;

import com.gradle.util.ActiveSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * Whenever user logs out this event will be called to unset user from ActiveSessionManager
 * @see ActiveSessionManager
 */
@Component
public class SessionExpireEventListener implements ApplicationListener<SessionDestroyedEvent> {

    @Autowired
    private ActiveSessionManager activeSessionManager;

    /**
     *
     * @param event : Logout event
     */
    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext) {
            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
            activeSessionManager.remove(userDetails.getUsername());
        }
    }
}
