/*
 * Copyright (c) 8/3/18 10:57 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

/**
 * Created by Dawid Stankiewicz on 30.07.2016
 */
package com.gradle.entity.forms.forum;

import com.gradle.validator.iface.HtmlValidateConstraint;

import javax.validation.constraints.Size;


public class NewPostForm {

    @Size(min = 3)
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
