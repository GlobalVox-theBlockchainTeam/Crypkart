/*
 * Copyright (c) 19/3/18 10:55 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;

import com.gradle.validator.impl.FeedBackValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FeedBackValidator.class)
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FeedBackConstraint {
    String message() default "{General.error.msg}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
