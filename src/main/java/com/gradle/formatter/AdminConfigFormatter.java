/*
 * Copyright (c) 12/3/18 12:24 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.user.Role;
import com.gradle.services.iface.admin.config.AdminConfigService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;


/**
 * Formatter class (helps format whole class object to use in forms)
 */
public class AdminConfigFormatter implements Formatter<AdminConfig> {

    private AdminConfigService adminConfigService;

    public AdminConfigFormatter() {
        super();
    }

    public AdminConfigFormatter(AdminConfigService adminConfigService) {
        this.adminConfigService = adminConfigService;
    }

    @Override
    public String print(AdminConfig adminConfig, Locale locale) {
        return Integer.toString(adminConfig.getId());
    }

    @Override
    public AdminConfig parse(String id, Locale locale) throws ParseException {
        AdminConfig adminConfig = this.adminConfigService.find(Integer.parseInt(id));
        //role.setId(Integer.parseInt(id));
        return adminConfig;
    }
}