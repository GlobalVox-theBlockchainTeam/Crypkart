/*
 * Copyright (c) 12/4/18 9:58 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler;

public class CoinmartNumberFormatException extends NumberFormatException {
    public CoinmartNumberFormatException(String error){
        super(error);
    }
}
