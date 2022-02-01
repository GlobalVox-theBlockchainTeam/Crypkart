/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.advertisement.PaymentType;
import com.gradle.services.iface.bitcoin.PaymentTypeService;
import com.gradle.util.LocaleHelper;
import com.gradle.validator.iface.PaymentTypeConstraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class PaymentTypeValidator implements ConstraintValidator<PaymentTypeConstraint, PaymentType> {
    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    private LocaleHelper localeHelper;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(PaymentTypeValidator.class);

    @Override
    public void initialize(PaymentTypeConstraint paymentType) {
    }

    /**
     * @param paymentType Payment Type object
     * @param cxt         validator context
     * @return
     */
    @Override
    public boolean isValid(PaymentType paymentType, ConstraintValidatorContext cxt) {
        String queryString = " from PaymentType where id!=? and paymentTypeName=?";
        Object[] params = new Object[2];
        params[0] = paymentType.getId();
        params[1] = paymentType.getPaymentTypeName();
        boolean ret = true;

        // Check if payment type name do not already exist
        try {
            PaymentType paymentTypeDb = paymentTypeService.first(queryString, params);
            if (paymentTypeDb != null) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.payment.type.name", null, "Payment type already exist")).addPropertyNode("paymentTypeName").addConstraintViolation();
                ret = false;
            }

        } catch (Exception e) {
            logger.error("Class : PaymentTypeValidator : " + e.getMessage());
        } finally {

        }
        return ret;
    }
}
