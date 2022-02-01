/*
 * Copyright (c) 11/4/18 10:54 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;


import com.gradle.validator.impl.CMSValidator;
import com.gradle.validator.impl.InternalTransferValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InternalTransferValidator.class)
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface InternalTransferConstraint {
    String message() default "{Wallet.internal.transfer.error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
