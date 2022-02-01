/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.Currency;
import com.gradle.services.iface.bitcoin.CurrencyService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formatter class (helps format whole class object to use in forms
 */
public class CurrencyFormatter implements Formatter<Currency> {

    private CurrencyService currencyService;

    public CurrencyFormatter(){
        super();
    }

    public CurrencyFormatter(CurrencyService currencyService){
        this.currencyService = currencyService;
    }
    @Override
    public String print(Currency currency, Locale locale) {
        return Integer.toString(currency.getId());
    }

    @Override
    public Currency parse(String id, Locale locale) throws ParseException {
        Currency currency = this.currencyService.find(Integer.parseInt(id));
        return currency;
    }
}