/*
 * Copyright (c) 2/4/18 10:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util.adminConfig;

import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.util.ServiceUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Table;
import java.util.List;


public class AdminConfigUtil<E> {


    /**
     * Get admin confiration from database
     * @param serviceUtil
     * @param obj
     * @return
     */
    public AdminConfig getAdminConfig(ServiceUtil serviceUtil, E obj){
        return serviceUtil.getAdminConfig(getTableNameFromClass(obj));
    }

    /**
     * Gets Admin Configuration values from database
     * @param serviceUtil
     * @param obj
     * @return
     */
    public List<AdminConfigValues> getAdminConfigValues(ServiceUtil serviceUtil, E obj){
        return serviceUtil.getAdminConfigValues(getTableNameFromClass(obj));
    }

    public String getTableNameFromClass(E obj){
        return obj.getClass().getAnnotationsByType(Table.class)[0].name();
    }
}
