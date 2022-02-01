/*
 * Copyright (c) 1/6/18 4:04 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.user;

import com.gradle.entity.Currency;
import com.gradle.entity.user.Countries;
import com.gradle.validator.iface.HtmlValidateConstraint;

import java.util.List;

public class CountryForm {
    @HtmlValidateConstraint(whiteListType = "none")
    private String countryCode;
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;
    private List<Countries> countries;
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

    public List<Countries> getCountries() {
        return countries;
    }

    public void setCountries(List<Countries> countries) {
        this.countries = countries;
    }
}
