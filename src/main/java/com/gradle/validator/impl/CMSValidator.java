/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.frontend.CMS;
import com.gradle.services.iface.CMSService;
import com.gradle.util.LocaleHelper;
import com.gradle.validator.iface.CMSConstraint;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class CMSValidator implements ConstraintValidator<CMSConstraint, CMS> {
    @Autowired
    CMSService cmsService;

    @Autowired
    private LocaleHelper localeHelper;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(CMSValidator.class);

    @Override
    public void initialize(CMSConstraint cms) {
    }

    /**
     * @param cms           CMS type object
     * @param cxt         validator context
     * @return
     */
    @Override
    public boolean isValid(CMS cms, ConstraintValidatorContext cxt) {
        String queryString = " from CMS where id!=? and pageId=?";
        Object[] params = new Object[2];
        params[0] = cms.getId();
        params[1] = cms.getId();
        boolean ret = true;

        // Check if payment type name do not already exist
        try {
            CMS cmsDb = cmsService.first(queryString, params);
            if (cmsDb != null) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.cms.pagid", null, "CMS Page ID already exist. choose different ID")).addPropertyNode("cmsId").addConstraintViolation();
                ret = false;
            }

        } catch (Exception e) {
            logger.error("Class : CMSValidator error  : " + e.getMessage());
        } finally {

        }
        Jsoup.isValid(cms.getContent(), Whitelist.basic());
        return ret;
        //return true;
    }
}
