/*
 * Copyright (c) 8/3/18 10:44 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.bitcoin;

import com.gradle.entity.advertisement.PaymentType;
import com.gradle.services.iface.GenericService;

import java.util.List;


public interface PaymentTypeService extends GenericService<PaymentType,Integer> {
    public List<PaymentType> getPaymentTypes(int records);
}
