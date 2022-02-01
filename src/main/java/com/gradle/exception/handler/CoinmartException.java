/*
 * Copyright (c) 12/4/18 9:55 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler;

import org.apache.log4j.Logger;

public class CoinmartException extends RuntimeException {

    public static final Logger logger = Logger.getLogger(CoinmartException.class);

    public CoinmartException(String error, String code){
        super(error + code);
    }

    public CoinmartException(String error){
        super(error);
    }
}
