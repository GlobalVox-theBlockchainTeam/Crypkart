/*
 * Copyright (c) 15/3/18 11:26 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler.controller.advice;

import org.eclipse.core.internal.resources.ResourceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;

@EnableWebMvc
@ControllerAdvice
public class ResourceNotFoundException {
    @ExceptionHandler(value = ResourceException.class)
    public String handleError1(HttpServletRequest request, ResourceNotFoundException e) {
        //redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "denied";

    }


}
