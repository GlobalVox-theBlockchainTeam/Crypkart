/*
 * Copyright (c) 12/3/18 12:22 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.admin.config;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.admin.config.AdminConfigValuesDao;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.services.iface.admin.config.AdminConfigValuesService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("adminConfigValuesService")
@Transactional
public class AdminConfigValuesServiceImpl extends GenericServiceImpl<AdminConfigValues, Integer> implements AdminConfigValuesService {

    @Autowired
    private AdminConfigValuesDao adminConfigValuesDao;



    public AdminConfigValuesServiceImpl() {

    }

    @Autowired
    public AdminConfigValuesServiceImpl(@Qualifier("adminConfigValuesDao") GenericDao<AdminConfigValues, Integer> genericDao) {
        super(genericDao);
        this.adminConfigValuesDao = (AdminConfigValuesDao) genericDao;
    }
}
