/*
 * Copyright (c) 5/3/18 2:02 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.advertisement.Advertise;
import com.gradle.services.iface.bitcoin.AdvertisementService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * Formatter class (helps format whole class object to use in forms)
 */
public class AdvertiseFormatter implements Formatter<Advertise> {

    private AdvertisementService advertisementService;

    public AdvertiseFormatter() {
        super();
    }

    public AdvertiseFormatter(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @Override
    public String print(Advertise advertise, Locale locale) {
        return Integer.toString(advertise.getId());
    }

    @Override
    public Advertise parse(String id, Locale locale) throws ParseException {
        Advertise advertise = this.advertisementService.find(Integer.parseInt(id));
        return advertise;
    }

}
