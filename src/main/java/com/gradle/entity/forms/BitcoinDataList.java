/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms;

import com.gradle.entity.forms.base.BaseForm;

import java.util.List;

/**
 * Bitcoin data list is a list class which we will use to get transactions detail(Bitcoin details so BitcoinDataList)
 */
public class BitcoinDataList extends BaseForm{

    private List<String> value;

    private List<String> sender;

    private List<String> receiver;

    private String transactionId;

    private String type;


    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public List<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(List<String> receiver) {
        this.receiver = receiver;
    }

    public List<String> getSender() {
        return sender;
    }

    public void setSender(List<String> sender) {
        this.sender = sender;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
