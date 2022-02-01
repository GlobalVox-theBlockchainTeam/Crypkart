/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.enums.advertisement;

/**
 * Advertisement Type Enum
 * This should be created in database as well
 */
public enum  AdType {
    SELL("Sell"), BUY("Buy");
    private String value;


    AdType(String s) {
        value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
// No changes


}
