/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler.AsyncExceptionHandler;

import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class CustomAsyncExceptionHandler
        implements AsyncUncaughtExceptionHandler {

    public static final Logger logger = Logger.getLogger(CustomAsyncExceptionHandler.class);
    /**
     * In case of exception while executing Async methods this will be called and
     * will log errors
     * @param throwable
     * @param method
     * @param obj
     */
    @Override
    public void handleUncaughtException(
            Throwable throwable, Method method, Object... obj) {
        logger.error("Method name - " + method.getName());
        logger.error("Exception message - " + throwable.getMessage());
        for (Object param : obj) {
            logger.info("Parameter value - " + param);
        }
    }
}
