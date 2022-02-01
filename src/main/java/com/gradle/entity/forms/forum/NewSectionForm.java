/*
 * Copyright (c) 8/3/18 10:57 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

/**
 * Created by Dawid Stankiewicz on 29.07.2016
 */
package com.gradle.entity.forms.forum;

import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Size;


public class NewSectionForm {
    
    @Size(min = 1, max = 50)
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;
    
    @Size(max = 300)
    @HtmlValidateConstraint(whiteListType = "basic", addAttributes = {"span:style"})
    private String description;
    
    public NewSectionForm() {}
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
