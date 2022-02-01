/*
 * Copyright (c) 11/4/18 10:06 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.InternalTransferDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.bitcoin.InternalTransfer;
import com.gradle.entity.user.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("internalTransferDao")
public class InternalTransferDaoImpl extends GenericDaoImpl<InternalTransfer, Integer> implements InternalTransferDao{

    /**
     *
     * @param user
     * @param userProperty
     * @param projection        Type of the projection like (average or count)
     * @param released          checks if bitcoin released or not
     * @return
     */
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