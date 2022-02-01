/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl;

import com.gradle.dao.iface.user.RoleDao;
import com.gradle.entity.user.Role;
import junit.framework.Assert;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class RoleDaoImplTest extends EntityDaoImplTest {

    @Autowired
    RoleDao roleDao;

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().getResourceAsStream("dao/Role.xml"));
        return dataSet;
    }


    @Test
    public void saveEmployee(){
        roleDao.saveOrUpdate(getRole());
        Assert.assertEquals(roleDao.findAll().size(), 3);
    }

    @Test
    public void findById(){
        Assert.assertNotNull(roleDao.find(1));
        Assert.assertNull(roleDao.find(3));
    }


    public Role getRole(){

        Role role = new Role();
        role.setRole("ROLE_TEST");
        return role;
    }


}
