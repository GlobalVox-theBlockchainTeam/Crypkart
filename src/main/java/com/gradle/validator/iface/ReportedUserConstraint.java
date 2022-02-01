/*
 * Copyright (c) 28/3/18 3:53 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;

import com.gradle.validator.impl.ReportedUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReportedUserValidator.class)
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportedUserConstraint {
    String message() default "{General.error.msg}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
