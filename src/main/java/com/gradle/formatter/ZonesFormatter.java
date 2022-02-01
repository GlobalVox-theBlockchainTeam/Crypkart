/*
 * Copyright (c) 3/4/18 11:32 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.user.Zones;
import com.gradle.services.iface.user.ZonesService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;


/**
 * Formatter class (helps format whole class object to use in forms
 */
public class ZonesFormatter implements Formatter<Zones> {

    private ZonesService zonesService;

    public ZonesFormatter() {
        super();
    }

    public ZonesFormatter(ZonesService zonesService) {
        this.zonesService = zonesService;
    }

    @Override
    public String print(Zones zone, Locale locale) {
        return Integer.toString(zone.getId());
    }

    @Override
    public Zones parse(String id, Locale locale) throws ParseException {
        Zones zone = this.zonesService.find(Integer.parseInt(id));
        //role.setId(Integer.parseInt(id));
        return zone;
    }
}