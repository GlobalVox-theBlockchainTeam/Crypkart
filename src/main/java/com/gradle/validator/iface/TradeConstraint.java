/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;


import com.gradle.validator.impl.TradeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TradeValidator.class)
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TradeConstraint {
    String message() default "{General.error.msg}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
