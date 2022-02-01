/*
 * Copyright (c) 9/3/18 5:38 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.events.listeners;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Registering db triggers for SessionFactory for hibernate
 */
@Component
    public class CRUDEventRegistration {
        @Autowired
        private SessionFactory sessionFactory;
        @Autowired
        private CRUDEventListener listener;
        @PostConstruct
        public void registerListeners() {
            EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
            registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(listener);
            registry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(listener);
            registry.getEventListenerGroup(EventType.POST_COMMIT_DELETE).appendListener(listener);
            registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(listener);
            registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(listener);

        }
    }


