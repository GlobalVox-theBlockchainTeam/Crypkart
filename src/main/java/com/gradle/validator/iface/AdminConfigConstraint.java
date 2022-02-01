/*
 * Copyright (c) 12/3/18 11:59 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;


import com.gradle.validator.impl.AdminConfigValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AdminConfigValidator.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminConfigConstraint {
    String message() default "{General.error.msg}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
