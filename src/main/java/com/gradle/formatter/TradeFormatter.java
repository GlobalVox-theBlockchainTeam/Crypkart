/*
 * Copyright (c) 19/3/18 11:02 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.formatter;

import com.gradle.entity.advertisement.Trade;
import com.gradle.services.iface.bitcoin.TradeService;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;


/**
 * Formatter class (helps format whole class object to use in forms
 */
public class TradeFormatter implements Formatter<Trade> {

    private TradeService tradeService;

    public TradeFormatter() {
        super();
    }

    public TradeFormatter(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Override
    public String print(Trade trade, Locale locale) {
        return Integer.toString(trade.getId());
    }

    @Override
    public Trade parse(String id, Locale locale) throws ParseException {
        Trade trade = this.tradeService.find(Integer.parseInt(id));
        //role.setId(Integer.parseInt(id));
        return trade;
    }
}