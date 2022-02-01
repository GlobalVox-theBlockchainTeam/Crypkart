/*
 * Copyright (c) 28/3/18 3:52 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.components.encrypter.PathVariableEncrypt;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.ReportedUser;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.services.iface.user.FeedBackService;
import com.gradle.services.iface.user.ReportedUserService;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.Alerts;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.validator.iface.ReportedUserConstraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class ReportedUserValidator implements ConstraintValidator<ReportedUserConstraint, ReportedUser> {


    @Autowired
    private ReportedUserService reportedUserService;

    @Autowired
    private LocaleHelper localeHelper;

    @Autowired
    private Alerts alerts;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PathVariableEncrypt pathVariableEncrypt;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(ReportedUserValidator.class);

    @Override
    public void initialize(ReportedUserConstraint reportedUser) {
    }

    /**
     * @param reportedUser FeedBack type object
     * @param cxt          validator context
     * @return
     */

    @Override
    public boolean isValid(ReportedUser reportedUser, ConstraintValidatorContext cxt) {
        boolean ret = true;
        try {
            String query = " select count(*) from ReportedUser where user_id=? and reported_user=?";
            Object[] params = new Object[2];
            params[0] = Integer.parseInt(pathVariableEncrypt.decrypt(reportedUser.getReportedById()));
            params[1] = Integer.parseInt(pathVariableEncrypt.decrypt(reportedUser.getReportedUserId()));
            Long count = reportedUserService.countQuery(query, params);
            if (count > 0) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Report.user.duplicate.entry", null, "You already have reported this user before")).addPropertyNode("comment").addConstraintViolation();
                alerts.setError("Report.user.duplicate.entry");
                ret = false;
            }
            if (reportedUser.getReportedById().equalsIgnoreCase(reportedUser.getReportedUserId())){
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Report.user.self", null, "You can not report your self")).addPropertyNode("comment").addConstraintViolation();
                alerts.setError("Report.user.self");
                ret = false;
            }
        } catch (Exception e) {
            logger.error("Class : ReportedUserValidator error  : " + e.getMessage());
            alerts.setError("General.error.msg");
            ret = false;
        } finally {

        }
        return ret;
    }
}
