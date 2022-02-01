/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.PaymentTypeDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.advertisement.PaymentType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("paymentTypeDao")
public class PaymentTypeDaoImpl extends GenericDaoImpl<PaymentType, Integer> implements PaymentTypeDao{

    /**
     * This method gets the list of most searched payment types (fist 7 types)
     * @param records
     * @return
     */
    @Override
    public List<PaymentType> getPaymentTypes(int records) {
        Criteria criteria = getCurrentSession().createCriteria(PaymentType.class).setMaxResults(records).addOrder(Order.desc("searchCount"));
        return criteria.list();
    }
}
