/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl;

import com.gradle.dao.iface.user.UserDao;
import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import junit.framework.Assert;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class UserDaoImplTest extends EntityDaoImplTest{
    @Autowired
    private UserDao userDao;

    @Mock
    UserService userService;

    @Autowired
    DataSource dataSource;

    @BeforeMethod
    public void setUp() throws Exception {
        IDatabaseConnection dbConn = new DatabaseDataSourceConnection(
                dataSource);
        DatabaseOperation.CLEAN_INSERT.execute(dbConn, getDataSet());
        MockitoAnnotations.initMocks(this);
    }


/*
    @BeforeClass
    public void setUp(){
       // MockitoAnnotations.initMocks(this);
        //userList = getUsersList();
    }*/

    @Override
    protected IDataSet getDataSet() throws Exception{
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().getResourceAsStream("dao/User.xml"));
        return dataSet;
    }

    @Test
    public void saveUser(){

        String queryString = " from User where id!=? and username=?";
        Object[] params = new Object[2];
        params[0] = getUser().getId();
        params[1] = getUser().getUsername();

        when(userService.first(any(),any())).thenReturn(getUser());
        Assert.assertNotSame(userDao.findAll().size(), 3);
        userDao.saveOrUpdate(getUser());
        Assert.assertEquals(userDao.findAll().size(), 3);
    }

    @Test
    public void findById(){
       /* when(userService.find(1)).thenReturn(getUser());
        when(userService.find(3)).thenReturn(null);*/
        Assert.assertNotNull(userDao.find(1));
        Assert.assertNull(userDao.find(3));
    }

    public User getUser(){
        User user = new User();
        user.setFirstName("testuser");
        user.setLastName("Anantestuser");
        user.setAddress("testuser");
        user.setPassword("testuser");
        user.setConfirmPassword("testuser");
        user.setPhone("987798789");
        user.setEmail("abnand@gmail.com");
        user.setUsername("testtest");
        return user;
    }

    public List<User> getUsersList(){
        User user = new User();
        user.setId(2);
        user.setFirstName("Anand");
        user.setLastName("Anand");
        user.setAddress("test");
        user.setPassword("test");
        user.setConfirmPassword("test");
        user.setPhone("test");
        user.setEmail("abnand@gmail.com");
        user.setUsername("testtest");

        User user1 = new User();
        user.setId(1);
        user.setFirstName("Anand1");
        user.setLastName("Anand1");
        user.setAddress("test1");
        user.setPassword("test1");
        user.setConfirmPassword("test1");
        user.setPhone("test1");
        user.setEmail("abnand1@gmail.com");
        user.setUsername("testtest1");
        List<User> userList = new ArrayList<User>();
        userList.add(user);
        userList.add(user1);
        return userList;
        //userList.add(user);
        //userList.add(user1);

    }
}
