/*
 * Copyright (c) 10/4/18 10:57 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.iface;


import com.gradle.validator.impl.HtmlValidator;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HtmlValidator.class)
@Target( { ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HtmlValidateConstraint {
    String message() default "{org.hibernate.validator.constraints.SafeHtml.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String whiteListType() default "";
    String[] allowedTags() default "";
    String[] disallowedTags() default "";
    String[] addAttributes() default "";
    boolean nullable() default true;
}
