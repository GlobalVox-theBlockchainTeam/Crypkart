/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.interceptor;

import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Before processing each request this preHandle will be called
 * We can set extra security here and throw user back to login page in case of
 * unauthorized access
 */
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = Logger.getLogger(RequestProcessingTimeInterceptor.class);

    @Autowired
    LocaleHelper localeHelper;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        // log which url was called and time of called
        logger.info("Request URL::" + request.getRequestURL().toString() + ":: Start Time=" + System.currentTimeMillis());
        request.setAttribute("startTime", startTime);

        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //if returned false, we need to make sure 'response' is sent

        // Get current controller and method name here
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String controllerName = handlerMethod.getBean().getClass().getSimpleName().replace("Controller", "");
        String methodName = handlerMethod.getMethod().getName();
        localeHelper.setControllerName(controllerName);
        localeHelper.setMethodName(methodName);
        localeHelper.setServletPaht(request.getServletPath());
        return true;
    }


    /*@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{
        long startTime = System.currentTimeMillis();
        logger.info("request URL::" + request.getRequestURL().toString() + " Time : " + startTime);
        response.setStatus(1);
    }*/

    /*@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception{
        long startTime = (Long) request.getAttribute("startTime");
        logger.info("Request URL::" + request.getRequestURL().toString() + ":: End Time=" + System.currentTimeMillis());
        logger.info("Request URL::" + request.getRequestURL().toString() + ":: Time Taken=" + (System.currentTimeMillis() - startTime));
    }*/
}
