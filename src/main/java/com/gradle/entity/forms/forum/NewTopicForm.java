/*
 * Copyright (c) 8/3/18 10:57 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

/**
 * Created by Dawid Stankiewicz on 28.07.2016
 */
package com.gradle.entity.forms.forum;

import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


public class NewTopicForm {
    
    @Size(min = 3, max = 50)
    @HtmlValidateConstraint(whiteListType = "none")
    private String title;

    
    @Size(min = 5)
    @HtmlValidateConstraint(whiteListType = "none", addAttributes = {"span:style"})
    private String content;
    
    @Min(value = 1)
    private int sectionId;
    
    public NewTopicForm() {}
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
    
}
