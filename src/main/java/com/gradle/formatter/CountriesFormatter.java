/*
 * Copyright (c) 3/4/18 11:30 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.user.Countries;
import com.gradle.services.iface.user.CountriesService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;


/**
 * Formatter class (helps format whole class object to use in forms)
 */
public class CountriesFormatter implements Formatter<Countries> {

    private CountriesService countriesService;

    public CountriesFormatter() {
        super();
    }

    public CountriesFormatter(CountriesService countriesService) {
        this.countriesService = countriesService;
    }

    @Override
    public String print(Countries country, Locale locale) {
        return Integer.toString(country.getId());
    }

    @Override
    public Countries parse(String id, Locale locale) throws ParseException {
        Countries country = this.countriesService.find(Integer.parseInt(id));
        //role.setId(Integer.parseInt(id));
        return country;
    }
}