/*
 * Copyright (c) 10/4/18 10:58 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.validator.iface.HtmlValidateConstraint;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.SafeHtml;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class HtmlValidator implements ConstraintValidator<HtmlValidateConstraint, String> {



    public static final Logger logger = Logger.getLogger(HtmlValidator.class);

    private boolean nullable;
    private Whitelist whitelist;
    @Override
    public void initialize(HtmlValidateConstraint str) {
        switch (str.whiteListType()){
            case "basic":
            case "Basic":
                whitelist = Whitelist.basic();
                break;
            case "relaxed":
            case "Relaxed":
                whitelist = Whitelist.relaxed();
                break;
            case "basicWithImage":
            case "BasicWithImage":
                whitelist = Whitelist.basicWithImages();
                break;
            case "simpleText":
            case "SimpleText":
                whitelist = Whitelist.relaxed();
                break;
            default:
                whitelist = new Whitelist();
                break;
        }

        if (str.addAttributes().length>0){
            for (String attr : str.addAttributes()){
                String[] attributeArray = attr.split(":");
                if (attributeArray.length>0) {
                    String attribute = attributeArray[0];
                    if (!attribute.isEmpty() && attribute!=null && !attribute.equalsIgnoreCase("")) {
                        String[] tags = attributeArray[1].split(",");
                        if (tags.length>0)
                            whitelist.addAttributes(attribute, tags);
                    }
                }
            }
        }
        if (str.allowedTags().length > 0){
            for (String tag : str.allowedTags()){
                if (!tag.isEmpty() && tag!=null && !tag.equalsIgnoreCase(""))
                    whitelist.addTags(tag);
            }
        }
        if (str.disallowedTags().length > 0){
            for(String tag : str.disallowedTags()){
                if (!tag.isEmpty() && tag!=null && !tag.equalsIgnoreCase(""))
                    whitelist.removeTags(tag);
            }
        }

        nullable = (str.nullable()) ? true : false;
    }

    /**
     * @param html           html to be validated
     * @param cxt         validator context
     * @return
     */
    @Override
    public boolean isValid(String html, ConstraintValidatorContext cxt) {
            if (html!=null && !html.equalsIgnoreCase("") && !html.isEmpty())
                return Jsoup.isValid(html, whitelist);
            else if (nullable)
                return true;
            else
                return false;
    }
}
