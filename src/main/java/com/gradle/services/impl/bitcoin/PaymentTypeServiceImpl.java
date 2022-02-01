/*
 * Copyright (c) 8/3/18 10:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.PaymentTypeDao;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.services.iface.bitcoin.PaymentTypeService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("paymentTypeService")
@Transactional
public class PaymentTypeServiceImpl extends GenericServiceImpl<PaymentType, Integer> implements PaymentTypeService {


    @Autowired
    private PaymentTypeDao paymentTypeDao;

    public PaymentTypeServiceImpl() {

    }

    @Autowired
    public PaymentTypeServiceImpl(
            @Qualifier("paymentTypeDao")
                    GenericDao<PaymentType, Integer> genericDao) {
        super(genericDao);
        this.paymentTypeDao = (PaymentTypeDao) genericDao;
    }

    @Override
    public List<PaymentType> getPaymentTypes(int records){
        return this.paymentTypeDao.getPaymentTypes(records);
    }
}