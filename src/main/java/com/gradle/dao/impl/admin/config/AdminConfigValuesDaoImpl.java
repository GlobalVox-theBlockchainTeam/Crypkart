/*
 * Copyright (c) 12/3/18 12:21 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.admin.config;

import com.gradle.dao.iface.admin.config.AdminConfigValuesDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.configurations.AdminConfigValues;
import org.springframework.stereotype.Repository;

@Repository("adminConfigValuesDao")
public class AdminConfigValuesDaoImpl extends GenericDaoImpl<AdminConfigValues, Integer> implements AdminConfigValuesDao{

}
