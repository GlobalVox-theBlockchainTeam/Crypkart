/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.iface.user.ZonesService;
import com.gradle.util.Common;
import com.gradle.util.LocaleHelper;
import com.gradle.validator.iface.UsernameConstraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator sor User model
 */
public class UsernameValidator implements ConstraintValidator<UsernameConstraint, User> {

    @Autowired
    private UserService userService;


    @Autowired
    private ZonesService zonesService;

    @Autowired
    private LocaleHelper localeHelper;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(UsernameValidator.class);

    @Override
    public void initialize(UsernameConstraint userName) {
    }

    /**
     *
     * @param user  : current user object
     * @param cxt   : Validation context
     * @return
     */
    @Override
    public boolean isValid(User user, ConstraintValidatorContext cxt) {
        String queryString = " from User where id!=? and username=?";
        Object[] params = new Object[2];
        params[0] = (user.getId()==null) ? 0 : user.getId();
        params[1] = user.getUsername();
        boolean ret = true;
        try {



            User userDb = userService.first(queryString, params);

            // check if new user and password is empty
            if ((user.getId()!=null && user.getId()==0) && (user.getPassword().isEmpty() || user.getPassword().equals(null))){
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Password.user.required", null, "Password is required field")).addPropertyNode("password").addConstraintViolation();
                ret = false;
            }
            // Check for duplicate user name
            if (userDb != null) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.user.username", null, "Username already exist")).addPropertyNode("username").addConstraintViolation();
                ret = false;
            }
            //check password and confirm password match
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Confirm.user.password.not.match", null, "Password and Confirm Password should match")).addPropertyNode("confirmPassword").addConstraintViolation();
                ret = false;
            }

            if (user.getPhone() != null && !user.getPhone().equals("")){
                if (!Common.isDouble(user.getPhone())){
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("user.phone.invalid", null, "Please enter valid phone number")).addPropertyNode("phone").addConstraintViolation();
                    ret =false;
                }
            }

            if (user.getCountryCode() != null && !user.getCountryCode().equals("")){
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("user.country.code.invalid", null, "Please enter valid country code")).addPropertyNode("countryCode").addConstraintViolation();
                if (!Common.isDouble(user.getCountryCode())){
                    ret =false;
                }
            }

            queryString = " from User where id!=? and email=?";
            params[1] = user.getEmail();
            userDb = userService.first(queryString, params);
            if (userDb != null) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Duplicate.user.email", null, "Email id already exist")).addPropertyNode("email").addConstraintViolation();
                ret = false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            ret=false;
        } finally {

        }
        return ret;
    }

}
