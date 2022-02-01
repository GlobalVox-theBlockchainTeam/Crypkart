/*
 * Copyright (c) 7/3/18 11:44 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler.chat;

import com.gradle.util.Alerts;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "No such Order")
public class ChatFileNotFoundException extends IOException {
    @Autowired
    private Alerts alerts;

    public static final Logger logger = Logger.getLogger(ChatFileNotFoundException.class);

    private RedirectAttributes redirectAttributes;

    public ChatFileNotFoundException(RedirectAttributes redirectAttributes) {
        this.redirectAttributes = redirectAttributes;
    }



}
