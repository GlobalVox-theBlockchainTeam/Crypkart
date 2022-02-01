/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.user.Role;
import com.gradle.services.iface.user.RoleService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;


/**
 * Formatter class (helps format whole class object to use in forms
 */
public class RoleFormatter implements Formatter<Role> {

    private RoleService roleService;

    public RoleFormatter(){
        super();
    }

    public RoleFormatter(RoleService roleService){
        this.roleService = roleService;
    }
    @Override
    public String print(Role role, Locale locale) {
        return Integer.toString(role.getId());
    }

    @Override
    public Role parse(String id, Locale locale) throws ParseException {
        Role role = this.roleService.find(Integer.parseInt(id));
        //role.setId(Integer.parseInt(id));
        return role;
    }
}