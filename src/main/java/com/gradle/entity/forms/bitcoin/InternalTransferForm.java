/*
 * Copyright (c) 11/4/18 10:36 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.bitcoin;

import com.gradle.entity.forms.base.BaseForm;
import com.gradle.validator.iface.HtmlValidateConstraint;
import com.gradle.validator.iface.InternalTransferConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@InternalTransferConstraint
public class InternalTransferForm extends BaseForm {

    @NotEmpty
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcAmount;

    @NotEmpty
    @HtmlValidateConstraint(whiteListType = "none")
    private String username;

    @HtmlValidateConstraint(whiteListType = "none")
    @Email
    private String email;

    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBtcAmount() {
        return btcAmount;
    }

    public void setBtcAmount(String btcAmount) {
        this.btcAmount = btcAmount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
