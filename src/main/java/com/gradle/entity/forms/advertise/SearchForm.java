/*
 * Copyright (c) 21/3/18 1:30 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.advertise;

import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

public class SearchForm {

    @HtmlValidateConstraint(whiteListType = "none")
    private String type;

    private Integer currencyId;

    private Integer paymentTypeId;

    @HtmlValidateConstraint(whiteListType = "none")
    private String advertiser;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Integer paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }
}
