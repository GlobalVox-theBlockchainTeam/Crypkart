/*
 * Copyright (c) 12/3/18 10:26 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.admin.config;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.admin.config.AdminConfigDao;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.services.iface.admin.config.AdminConfigService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("adminConfigService")
@Transactional
public class AdminConfigServiceImpl extends GenericServiceImpl<AdminConfig, Integer> implements AdminConfigService {

    @Autowired
    private AdminConfigDao adminConfigDao;



    public AdminConfigServiceImpl() {

    }

    @Autowired
    public AdminConfigServiceImpl(@Qualifier("adminConfigDao") GenericDao<AdminConfig, Integer> genericDao) {
        super(genericDao);
        this.adminConfigDao = (AdminConfigDao) genericDao;
    }
}
