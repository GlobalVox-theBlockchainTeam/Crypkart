/*
 * Copyright (c) 5/4/18 5:10 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.EscrowDao;
import com.gradle.dao.iface.bitcoin.PaymentTypeDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.user.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("escrowDao")
public class EscrowDaoImpl extends GenericDaoImpl<Escrow, Integer> implements EscrowDao{
    @Override
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.add(Restrictions.eq(userProperty, user));
        criteria.setProjection(projection);
        criteria.add(Restrictions.eq("released", released));
        Object result = criteria.uniqueResult();

        return (result!=null) ? Double.valueOf(result.toString()).doubleValue() : 0d;
    }
}
