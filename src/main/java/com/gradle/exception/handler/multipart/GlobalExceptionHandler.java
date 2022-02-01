/*
 * Copyright (c) 6/3/18 4:14 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler.multipart;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;

/**
 * Exception handler for Max Size upload error
 * if user tries to exceed allowed limit in uploading file
 */
@EnableWebMvc
@ControllerAdvice
public class GlobalExceptionHandler {
    //StandardServletMultipartResolver
    @ExceptionHandler(value = MultipartException.class)
    public String handleError1(HttpServletRequest request, MultipartException e) {

        //redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "redirect:/uploadStatus";

    }

    //CommonsMultipartResolver
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public String handleError2(HttpServletRequest request, MaxUploadSizeExceededException e) {

        //redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "redirect:/trade/maxsizeexceeded";

    }

}
