/*
 * Copyright (c) 19/4/18 9:44 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.bitcoin;

import com.gradle.entity.forms.base.BaseForm;
import com.gradle.validator.iface.ExternalTransferConstraint;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Objects;

@ExternalTransferConstraint
public class ExternalTransferForm extends BaseForm {

    @NotEmpty
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcAmount;

    @NotEmpty
    @HtmlValidateConstraint(whiteListType = "none")
    private String walletAddress;

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

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalTransferForm that = (ExternalTransferForm) o;
        return Objects.equals(btcAmount, that.btcAmount) &&
                Objects.equals(walletAddress, that.walletAddress) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {

        return Objects.hash(btcAmount, walletAddress, email);
    }
}
