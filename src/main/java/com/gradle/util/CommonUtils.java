/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that provides all common static java utility
 * methods.
 */
public final class CommonUtils {

    private static final DateFormat DATE_FORMAT_HH_mm = new SimpleDateFormat("HH:mm");

    public static String getCurrentTimeStamp() {
        return DATE_FORMAT_HH_mm.format(new Date());
    }
}
