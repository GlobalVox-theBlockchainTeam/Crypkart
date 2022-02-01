/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms;

import com.gradle.entity.forms.base.BaseForm;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

/**
 * This is our send coin form
 * @property bitcoin : coin value
 * @property walletAddress
 */
public class SendCoinForm extends BaseForm{

    @NotEmpty
    @HtmlValidateConstraint(whiteListType = "none")
    private String bitcoin;

    @NotEmpty
    @HtmlValidateConstraint(whiteListType = "none")
    private String walletAddress;

    public String getBitcoin() {
        return bitcoin;
    }

    public void setBitcoin(String bitcoin) {
        this.bitcoin = bitcoin;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
