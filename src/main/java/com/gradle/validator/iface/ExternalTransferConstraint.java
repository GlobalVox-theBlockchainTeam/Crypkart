/*
 * Copyright (c) 19/4/18 9:45 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;


import com.gradle.validator.impl.ExternalTransferValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExternalTransferValidator.class)
@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalTransferConstraint {
    String message() default "{Wallet.external.transfer.error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
