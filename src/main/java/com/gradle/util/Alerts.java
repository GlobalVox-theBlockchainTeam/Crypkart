/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel that stores and categorize alert information.
 *
 * We will create different list of different type of messages(info, error, success)
 * Finally method setAlertModelAttribute will store them in model attributes
 * and clearAlert method will clear all list so it do not retain them till next request
 *
 * In case when retaining messages required for next request, use setAlertRedirectAttribute
 * In case retaining messages required for multiple request, do not user clearAlerts() method after setting messages
 *      to model or redirectAttributes
 *
 * @author Anand Panchal
 */

public class Alerts {




    @Autowired
    private LocaleHelper localeHelper;

    public static int a = 1;

    public List<String> successes = new ArrayList<String>();
    public List<String> errors = new ArrayList<String>();
    public List<String> warnings = new ArrayList<String>();
    public List<String> notices = new ArrayList<String>();





    public void setSuccess(String msg) {
        successes.add(localeHelper.getApplicationPropertiesText(msg,null,null));
    }
    public void setSuccess(String msg, String[] args) {
        successes.add(localeHelper.getApplicationPropertiesText(msg,args,null));
    }

    public void setWarning(String msg) {

        warnings.add(localeHelper.getApplicationPropertiesText(msg,null,null));
    }
    public void setWarning(String msg, String[] args) {

        warnings.add(localeHelper.getApplicationPropertiesText(msg,null,null));
    }

    public void setNotice(String msg) {
        notices.add(localeHelper.getApplicationPropertiesText(msg,null,null));
    }
    public void setNotice(String msg, String[] args) {
        notices.add(localeHelper.getApplicationPropertiesText(msg,args,null));
    }

    public void setError(String msg) {
        errors.add(localeHelper.getApplicationPropertiesText(msg,null,null));
    }

    public void setError(String msg, String[] args) {
        errors.add(localeHelper.getApplicationPropertiesText(msg,args,null));
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<String> successes) {
        this.successes = successes;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getNotices() {
        return notices;
    }

    public void setNotices(List<String> notices) {
        this.notices = notices;
    }

    public void clearAlert() {
        successes.clear();
        errors.clear();
        warnings.clear();
        notices.clear();
    }


    /**
     *
     * @param model
     */
    public void setAlertModelAttribute(ModelMap model) {
        if (getSuccesses() != null)
            model.addAttribute("alert_success", new ArrayList(getSuccesses()));
        if (getErrors() != null)
            model.addAttribute("alert_errors", new ArrayList(getErrors()));
        if (getWarnings() != null)
            model.addAttribute("alert_warnings", new ArrayList(getWarnings()));
        if (getNotices() != null)
            model.addAttribute("alert_notices", new ArrayList(getNotices()));
    }

    /**
     *
     * @param redirectAttributes
     */
    public void setAlertRedirectAttribute(RedirectAttributes redirectAttributes) {
        if (getSuccesses() != null)
            redirectAttributes.addFlashAttribute("alert_success", new ArrayList(getSuccesses()));
        if (getErrors() != null)
            redirectAttributes.addFlashAttribute("alert_errors", new ArrayList(getErrors()));
        if (getWarnings() != null)
            redirectAttributes.addFlashAttribute("alert_warnings", new ArrayList(getWarnings()));
        if (getNotices() != null)
            redirectAttributes.addFlashAttribute("alert_notices", new ArrayList(getNotices()));
    }


}