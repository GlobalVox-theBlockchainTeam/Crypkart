/*
 * Copyright (c) 3/4/18 11:10 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.CountriesDao;
import com.gradle.entity.user.Countries;
import com.gradle.services.iface.user.CountriesService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("countriesService")
@Transactional
public class CountriesServiceImpl extends GenericServiceImpl<Countries, Integer> implements CountriesService {

    @Autowired
    private CountriesDao countriesDao;

    public CountriesServiceImpl() {

    }

    @Autowired
    public CountriesServiceImpl(
            @Qualifier("countriesDao")
                    GenericDao<Countries, Integer> genericDao) {
        super(genericDao);
        this.countriesDao = (CountriesDao) genericDao;
    }
}