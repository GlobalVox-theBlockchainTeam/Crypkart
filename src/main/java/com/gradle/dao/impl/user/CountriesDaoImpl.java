/*
 * Copyright (c) 3/4/18 11:08 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.CountriesDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.Countries;
import org.springframework.stereotype.Repository;

@Repository("countriesDao")
public class CountriesDaoImpl extends GenericDaoImpl<Countries, Integer> implements CountriesDao {


}
