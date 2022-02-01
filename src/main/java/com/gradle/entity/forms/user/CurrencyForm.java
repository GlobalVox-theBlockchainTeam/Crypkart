/*
 * Copyright (c) 1/6/18 3:30 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.user;

import com.gradle.entity.Currency;
import com.gradle.entity.user.Zones;
import com.gradle.validator.iface.HtmlValidateConstraint;

import java.util.List;

public class CurrencyForm {
    @HtmlValidateConstraint(whiteListType = "none")
    private String countryCode;
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;
    private List<Currency> currencies;
    @HtmlValidateConstraint(whiteListType = "none")
    private String statusCode;
    @HtmlValidateConstraint(whiteListType = "none")
    private String statusMessage;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }
}
