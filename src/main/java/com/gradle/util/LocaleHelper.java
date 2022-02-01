/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;

import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.util.Locale;


/**
 *
 * This is our locale helper for application
 * It sets current locale and gets current locale
 * This methods can be called to get related language text from properties files
 */
public class LocaleHelper {

    @Autowired
    @Qualifier("templateMessageSource")
    private MessageSource templateMessageSource;

    private String controllerName = "";
    private String methodName = "";
    private String servletPaht = "";
    private String bitcoinjEnvironment="test";
    private boolean showLastLogin= false;

    @Autowired
    private MessageSource messageSource;

    private HandlerMethod handlerMethod;

    private Locale currentLocale;

    public LocaleHelper(){
        this.bitcoinjEnvironment="test";
    }

    public Locale getCurrentLocale() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return currentLocale;
    }

    // set current locale
    public void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    public NetworkParameters getBitcoinjNetworkParams(){
        return Common.getNetworkParameter(getBitcoinjEnvironment());
    }

    public String getApplicationPropertiesText(String code, String[] args, String defaultMessage) {
        return messageSource.getMessage(code, args, ((defaultMessage == null) ? code : defaultMessage), getCurrentLocale());
    }

    public String getTemplateProperties(String code, String[] args, String defaultMessage) {
        return templateMessageSource.getMessage(code, args, ((defaultMessage == null) ? code : defaultMessage), getCurrentLocale());
    }

    // setting title of the page
    public String getTitle(String[] args, String defaultMessage) {

        String code = controllerName + "." + methodName + ".title";
        return templateMessageSource.getMessage(code, args, ((defaultMessage == null) ? code : defaultMessage), getCurrentLocale());
    }

    // getting page title
    public String getPageTitle(String[] args, String defaultMessage) {

        String code = controllerName + "." + methodName + ".page.title";
        return templateMessageSource.getMessage(code, args, ((defaultMessage == null) ? code : defaultMessage), getCurrentLocale());
    }

    // get current locale
    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getServletPaht() {
        return servletPaht;
    }

    public void setServletPaht(String servletPaht) {
        this.servletPaht = servletPaht;
    }

    public String getBitcoinjEnvironment() {
        return bitcoinjEnvironment;
    }

    public void setBitcoinjEnvironment(String bitcoinjEnvironment) {
        this.bitcoinjEnvironment = bitcoinjEnvironment;
    }

    public boolean isShowLastLogin() {
        return showLastLogin;
    }

    public void setShowLastLogin(boolean showLastLogin) {
        this.showLastLogin = showLastLogin;
    }
}
