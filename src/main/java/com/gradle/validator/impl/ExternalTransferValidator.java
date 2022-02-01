/*
 * Copyright (c) 19/4/18 9:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.forms.bitcoin.ExternalTransferForm;
import com.gradle.entity.forms.bitcoin.InternalTransferForm;
import com.gradle.entity.user.User;
import com.gradle.util.Common;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.validator.iface.ExternalTransferConstraint;
import com.gradle.validator.iface.InternalTransferConstraint;
import org.apache.log4j.Logger;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class ExternalTransferValidator implements ConstraintValidator<ExternalTransferConstraint, ExternalTransferForm> {

    public static final Logger logger = Logger.getLogger(ExternalTransferValidator.class);

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private LocaleHelper localeHelper;

    @Override
    public void initialize(ExternalTransferConstraint constraint) {
    }

    /**
     * @param form Internal Transfer Form
     * @param cxt  validator context
     * @return
     */
    @Override
    public boolean isValid(ExternalTransferForm form, ConstraintValidatorContext cxt) {
        boolean ret = true;
        try {
            Double minAllowedBtc = serviceUtil.getMinimumAllowedBtcTransaction();
            User currentUser = serviceUtil.getCurrentUser();
            if (!Common.isDouble(form.getBtcAmount())) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.btc.amount.invalid", null, "Not a valid btc amount")).addPropertyNode("btcAmount").addConstraintViolation();
                ret = false;
            } else if (!serviceUtil.canDoExternalTransfer(currentUser, form)) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.sufficient.balance.error", null, "You dont have sufficient bitcoins to transfer")).addPropertyNode("btcAmount").addConstraintViolation();
                ret = false;
            } else if (minAllowedBtc > Double.parseDouble(form.getBtcAmount())) {
                String[] args = new String[1];
                args[0] = minAllowedBtc.toString();
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.minimum.allowed.btc.transaction", args, "Minimum allowed transaction of btc : " + minAllowedBtc)).addPropertyNode("btcAmount").addConstraintViolation();
                ret = false;
            }


            if (!Common.isValidBitcoinAddress(form.getWalletAddress(), localeHelper.getBitcoinjNetworkParams())) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Wallet.invalid.address", null, "Please enter valid wallet address ")).addPropertyNode("walletAddress").addConstraintViolation();
                ret = false;
            }
        } catch (Exception e) {
            ret = false;
            logger.error(e.getMessage());
        }

        return ret;
    }
}

