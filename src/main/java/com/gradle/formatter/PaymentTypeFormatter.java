/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.advertisement.PaymentType;
import com.gradle.services.iface.bitcoin.PaymentTypeService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;


/**
 * Formatter class (helps format whole class object to use in forms
 */
public class PaymentTypeFormatter implements Formatter<PaymentType> {

    private PaymentTypeService paymentTypeService;

    public PaymentTypeFormatter(){
        super();
    }

    public PaymentTypeFormatter(PaymentTypeService paymentTypeService){
        this.paymentTypeService = paymentTypeService;
    }
    @Override
    public String print(PaymentType paymentType, Locale locale) {
        return Integer.toString(paymentType.getId());
    }

    @Override
    public PaymentType parse(String id, Locale locale) throws ParseException {
        PaymentType paymentType = this.paymentTypeService.find(Integer.parseInt(id));
        //role.setId(Integer.parseInt(id));
        return paymentType;
    }
}