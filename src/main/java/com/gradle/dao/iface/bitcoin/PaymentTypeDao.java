/*
 * Copyright (c) 8/3/18 10:40 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.advertisement.PaymentType;

import java.util.List;

public interface PaymentTypeDao extends GenericDao<PaymentType, Integer> {
    public List<PaymentType> getPaymentTypes(int records);
}
