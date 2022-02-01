/*
 * Copyright (c) 3/4/18 12:38 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.user;

import com.gradle.entity.user.Zones;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import java.util.List;

public class ZonesForm {
    @HtmlValidateConstraint(whiteListType = "none")
    private String countryCode;
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;
    private List<Zones> zones;
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

    public List<Zones> getZones() {
        return zones;
    }

    public void setZones(List<Zones> zones) {
        this.zones = zones;
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
}
