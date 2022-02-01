/*
 * Copyright (c) 12/3/18 10:24 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.admin.config;

import com.gradle.dao.iface.admin.config.AdminConfigDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.configurations.AdminConfig;
import org.springframework.stereotype.Repository;

@Repository("adminConfigDao")
public class AdminConfigDaoImpl extends GenericDaoImpl<AdminConfig, Integer> implements AdminConfigDao{

}
