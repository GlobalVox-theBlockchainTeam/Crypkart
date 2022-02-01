/*
 * Copyright (c) 6/4/18 2:55 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.DirectTransactionDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.bitcoin.DirectTransaction;
import com.gradle.entity.user.User;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("directTransactionDao")
public class DirectTransactionDaoImpl extends GenericDaoImpl<DirectTransaction, Integer> implements DirectTransactionDao {

    @Override
    public Double getByProjection(User user, String userProperty, Projection projection, boolean outgoing) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.add(Restrictions.eq(userProperty, user));
        criteria.setProjection(projection);
        Criterion c1 = Restrictions.eq("outgoing", outgoing);
        criteria.add(c1);
        if (!outgoing) {
            Criterion criterion = Restrictions.ge("totalConfirmation", 1);
            criteria.add(criterion);
        }
        Object result = criteria.uniqueResult();

        return (result != null) ? Double.valueOf(result.toString()).doubleValue() : 0d;
    }

    @Override
    public List<DirectTransaction> getByType(int page, int maxCount, DirectTransaction obj, User user, String userProperty, String property, boolean value) {
        int firstRecord = (page * maxCount) - maxCount;
        Query criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where " + userProperty + "=? and " + property + "=? order by id DESC");
        criteria.setParameter(0, user.getId());
        criteria.setParameter(1, (value) ? 1 : 0);
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }
}
