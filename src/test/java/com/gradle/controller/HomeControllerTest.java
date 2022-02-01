/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller;

import com.gradle.entity.Currency;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.services.iface.bitcoin.PaymentTypeService;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.ActiveSessionManager;
import com.gradle.util.Alerts;
import com.gradle.util.ServiceUtil;
import junit.framework.Assert;
import org.hibernate.validator.constraints.ModCheck;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomeControllerTest {

    @Mock
    UserService userService;

    @Mock
    PaymentTypeService paymentTypeService;

    @Mock
    CurrencyService currencyService;

    @Mock
    User user;

    @Mock
    ServiceUtil serviceUtil;

    @Mock
    ActiveSessionManager activeSessionManager;


    @Mock
    MessageSource message;

    @Mock
    Alerts alerts;



    @InjectMocks
    HomeController homeController;

    @Spy
    List<User> users = new ArrayList<User>();

    @Spy
    List<PaymentType> paymentTypes = new ArrayList<PaymentType>();

    @Spy
    List<Currency> currencies = new ArrayList<Currency>();

    @Spy
    ModelMap model;

    @Mock
    BindingResult result;

    @BeforeClass
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        users = getUserList();
        paymentTypes = getPaymentTypeList();
        currencies = getCurrencies();
    }

    @Test
    public void homeTest(){
        when(userService.findAll()).thenReturn(users);
        when(paymentTypeService.getPaymentTypes(5)).thenReturn(getPaymentTypeList());
        when(currencyService.findAll()).thenReturn(currencies);
        User user = new User();user.setUsername("test");
        when(serviceUtil.getCurrentUser()).thenReturn(user);

        Assert.assertEquals(homeController.home(new User(),model,result, null, null), "home/home");
        Assert.assertEquals(model.get("title"), "Coinmart Home");
        verify(userService, atLeastOnce()).findAll();
    }

    @Test
    public void loginTest(){
        Assert.assertEquals(homeController.login("test",null,null, null, model,new User(),null, null), "user/login");
        Assert.assertEquals(homeController.login("error","error",null, null, model,new User(),null, null), "user/login");
        Assert.assertEquals(model.get("loginCssClass"), "active");
        Assert.assertEquals(model.get("captchaTokenError"), "Captcha required");
    }


    /*@Test
    public void listEmployees(){

    }*/

    public List<PaymentType> getPaymentTypeList(){
        PaymentType p = new PaymentType();
        p.setId(1);
        p.setPaymentTypeName("Test");
        List<PaymentType> paymentTypes = new ArrayList<PaymentType>();
        paymentTypes.add(p);
        return paymentTypes;
    }

    public List<Currency> getCurrencies(){
        Currency c = new Currency();
        c.setCurrencyName("Australian Dollar");
        c.setCurrencyCode("AUD");
        currencies.add(c);
        return currencies;
    }

    public List<User> getUserList(){
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
    }
}
