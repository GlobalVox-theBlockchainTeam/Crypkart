/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.services.iface.bitcoin.AdvertisementService;
import com.gradle.util.Common;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;
import com.gradle.validator.iface.AdvertiseConstraint;
import org.apache.log4j.Logger;
import org.h2.schema.Constant;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validator for payment type
 */
public class AdvertiseValidator implements ConstraintValidator<AdvertiseConstraint, Advertise> {
    @Autowired
    AdvertisementService advertiseService;

    @Autowired
    private LocaleHelper localeHelper;

    @Autowired
    private ServiceUtil serviceUtil;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(AdvertiseValidator.class);

    @Override
    public void initialize(AdvertiseConstraint cms) {
    }

    /**
     * @param advertise CMS type object
     * @param cxt       validator context
     * @return
     */
    @Override
    public boolean isValid(Advertise advertise, ConstraintValidatorContext cxt) {
        boolean ret = true;

        // Check if payment type name do not already exist
        AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
        List<AdminConfigValues> adminConfigValuesList = adminConfigUtil.getAdminConfigValues(serviceUtil, advertise);
        String minTimeoutValue = Common.getAdminConfigValue(adminConfigValuesList, ConstantProperties.MIN_TIMEOUT_PROPERTY,ConstantProperties.MIN_TIMEOUT.toString());
        try {
            if (!advertise.getBtcRate().isEmpty()){
                if(!Common.isDouble(Common.plainStringPrice(advertise.getBtcRate())) || Common.strToDouble(advertise.getBtcRate()) < 0){
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Advertise.btcrate.invalid", null, "Please enter valid amount")).addPropertyNode("btcRate").addConstraintViolation();
                    ret = false;
                }
            }
            if (!advertise.getMaxLimit().isEmpty()){
                if(!Common.isDouble(Common.plainStringPrice(advertise.getMaxLimit())) || Common.strToDouble(advertise.getMaxLimit()) < 0){
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Advertise.max.invalid", null, "Please enter valid max limit")).addPropertyNode("maxLimit").addConstraintViolation();
                    ret = false;
                }
            }
            if (!advertise.getMinLimit().isEmpty()){
                if(!Common.isDouble(Common.plainStringPrice(advertise.getMinLimit())) || Common.strToDouble(advertise.getMinLimit()) < 0){
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Advertise.min.invalid", null, "Please enter valid min limit")).addPropertyNode("minLimit").addConstraintViolation();
                    ret = false;
                }
            }
            if (advertise.getTimeout()<Integer.parseInt(minTimeoutValue)){
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Advertise.timeout.minimum", null, "Minimum 20 minutes are required")).addPropertyNode("timeout").addConstraintViolation();
                ret = false;
            }
            if (Common.plainStringPrice(advertise.getMaxLimit()) != null && Common.plainStringPrice(advertise.getMinLimit()) != null && !advertise.getMaxLimit().equalsIgnoreCase("")
                    && !advertise.getMinLimit().equalsIgnoreCase("")
                    ) {
                if (Double.parseDouble(advertise.getMaxLimit()) < Double.parseDouble(advertise.getMinLimit())) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Advertise.minlimit.greater.maxlimit", null, "Maximum limit must be greater than Minimum")).addPropertyNode("maxLimit").addConstraintViolation();
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Advertise.maxlimit.less.minlimit", null, "Minimum limit must be less than Maximum")).addPropertyNode("minLimit").addConstraintViolation();
                    ret= false;
                }
            }
        } catch (Exception e) {
            logger.error("Class : AdvertisementValidator error  : " + e.getMessage());
        } finally {

        }
        return ret;
    }
}
