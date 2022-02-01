/*
 * Copyright (c) 12/3/18 11:59 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.configurations.AdminConfig;
import com.gradle.services.iface.admin.config.AdminConfigService;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.validator.iface.AdminConfigConstraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator for payment type
 */
public class AdminConfigValidator implements ConstraintValidator<AdminConfigConstraint, AdminConfig> {
    @Autowired
    private AdminConfigService adminConfigService;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private LocaleHelper localeHelper;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(AdminConfigValidator.class);

    @Override
    public void initialize(AdminConfigConstraint adminConfig) {
    }

    /**
     * @param adminConfig CMS type object
     * @param cxt         validator context
     * @return
     */
    @Override
    public boolean isValid(AdminConfig adminConfig, ConstraintValidatorContext cxt) {
        boolean ret = true;

        // Check if payment type name do not already exist
        try {
            String queryString = " from AdminConfig where id!=? and (config_name=? or (data_table=? and data_table!=?) )";
            Object[] params = new Object[4];
            params[1] = adminConfig.getConfigName();
            params[0] = adminConfig.getId();
            params[2] = adminConfig.getType();
            params[3] = "General";
            List<Object> list = serviceUtil.getAllTables();

            Pattern p = Pattern.compile("[^a-z0-9_]", Pattern.CASE_INSENSITIVE);




            AdminConfig adminConfigDb = adminConfigService.first(queryString, params);
            if (adminConfigDb != null) {
                if (adminConfig.getType().equalsIgnoreCase(adminConfigDb.getType()) && !adminConfig.getType().equalsIgnoreCase("General")) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.admin.config.type", null, "Only one config per data_table Allowed")).addPropertyNode("type").addConstraintViolation();
                }
                if (adminConfig.getConfigName().equalsIgnoreCase(adminConfigDb.getConfigName())) {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.admin.config.pagid", null, "Config Name already exist. choose different Name")).addPropertyNode("configName").addConstraintViolation();
                }
                ret = false;
            }
            if (p.matcher(adminConfig.getConfigName()).find()){
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.admin.config.name.invalid", null, "Config can have only Alphabets, numbers and _")).addPropertyNode("configName").addConstraintViolation();
                ret = false;
            }

            if (!list.contains(adminConfig.getType()) && !adminConfig.getType().equalsIgnoreCase("General")) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Admin.config.invalid.type", null, "Please choose valid type")).addPropertyNode("type").addConstraintViolation();
                ret = false;
            }


        } catch (Exception e) {
            logger.error("Class : AdvertisementValidator error  : " + e.getMessage());
            ret = false;
        } finally {

        }
        return ret;
    }
}
