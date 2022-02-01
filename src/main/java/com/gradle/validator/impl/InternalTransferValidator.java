/*
 * Copyright (c) 11/4/18 10:54 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.bitcoin.InternalTransfer;
import com.gradle.entity.forms.bitcoin.InternalTransferForm;
import com.gradle.entity.frontend.CMS;
import com.gradle.entity.user.User;
import com.gradle.services.iface.CMSService;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.Common;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.validator.iface.CMSConstraint;
import com.gradle.validator.iface.InternalTransferConstraint;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class InternalTransferValidator implements ConstraintValidator<InternalTransferConstraint, InternalTransferForm> {

    public static final Logger logger = Logger.getLogger(InternalTransferValidator.class);

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private LocaleHelper localeHelper;

    @Override
    public void initialize(InternalTransferConstraint constraint) {
    }

    /**
     * @param form Internal Transfer Form
     * @param cxt  validator context
     * @return
     */
    @Override
    public boolean isValid(InternalTransferForm form, ConstraintValidatorContext cxt) {
        boolean ret = true;
        try {
            Double minAllowedBtc = serviceUtil.getMinimumAllowedBtcTransaction();
            User currentUser = serviceUtil.getCurrentUser();
            if (form.getEmail() != null && !form.getEmail().equalsIgnoreCase("")) {
                User user = serviceUtil.getUserFromUsernameOrEmail(form.getEmail());
                if (user == null) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.email.invalid", null, "Email is not valid")).addPropertyNode("email").addConstraintViolation();
                    ret = false;
                } else if (!user.getUsername().equals(form.getUsername())) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.username.email.combination.invalid", null, "Email and Username you have used are not of the same user. Please check again")).addPropertyNode("username").addConstraintViolation();
                    ret = false;
                }
                if (user != null && user.getEmail().equals(currentUser.getEmail())) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.same.user.error", null, "You can not transfer bitcoins to yourself")).addPropertyNode("email").addConstraintViolation();
                    ret = false;
                }
            }
            if (form.getUsername() != null && !form.getUsername().equalsIgnoreCase("")) {
                User user = serviceUtil.getUserFromUsernameOrEmail(form.getUsername());
                if (user == null) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.username.invalid", null, "Email is not valid")).addPropertyNode("username").addConstraintViolation();
                    ret = false;
                } else if (!user.getEmail().equals(form.getEmail())) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.username.email.combination.invalid", null, "Email and Username you have used are not of the same user. Please check again")).addPropertyNode("email").addConstraintViolation();
                    ret = false;
                }
                if (user != null && user.getUsername().equals(currentUser.getUsername())) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.same.user.error", null, "You can not transfer bitcoins to yourself")).addPropertyNode("username").addConstraintViolation();
                    ret = false;
                }
            }
            if (!Common.isDouble(form.getBtcAmount())) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.btc.amount.invalid", null, "Not a valid btc amount")).addPropertyNode("btcAmount").addConstraintViolation();
                ret = false;
            } else if (!serviceUtil.canDoInternalTransfer(serviceUtil.getCurrentUser(), form)) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.sufficient.balance.error", null, "You dont have sufficient bitcoins to transfer")).addPropertyNode("btcAmount").addConstraintViolation();
                ret = false;
            } else if (minAllowedBtc > Double.parseDouble(form.getBtcAmount())) {
                String[] args = new String[1];
                args[0] = minAllowedBtc.toString();
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.minimum.allowed.btc.transaction", args, "Minimum allowed transaction of btc : " + minAllowedBtc)).addPropertyNode("btcAmount").addConstraintViolation();
                ret = false;
            }


        } catch (Exception e) {
            ret = false;
            logger.error(e.getMessage());
        }

        return ret;
    }
}

