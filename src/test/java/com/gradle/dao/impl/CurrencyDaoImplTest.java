/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl;


import com.gradle.dao.iface.bitcoin.CurrencyDao;
import com.gradle.entity.Currency;
import junit.framework.Assert;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class CurrencyDaoImplTest extends EntityDaoImplTest {

    @Autowired
    CurrencyDao currencyDao;





    @Override
    protected IDataSet getDataSet() throws Exception{
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().getResourceAsStream("dao/Currency.xml"));
        return dataSet;
    }

    @Test
    public void saveEmployee(){
        currencyDao.saveOrUpdate(getSampleCurrency());
        Assert.assertEquals(currencyDao.findAll().size(), 3);
    }

    @Test
    public void findById(){
        Assert.assertNotNull(currencyDao.find(1));
        Assert.assertNull(currencyDao.find(3));
    }

    public Currency getSampleCurrency(){
        Currency currency = new Currency();
        currency.setCurrencyCode("AUD");
        currency.setCurrencyName("Australian Dollar");
        return currency;
    }
}
