/*
 * Copyright (c) 16/3/18 2:32 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.FeedBackDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.User;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("feedBackDao")
public class FeedBackDaoImpl extends GenericDaoImpl<FeedBack, Integer> implements FeedBackDao {

    /**
     * get feedbacks according to paging parameters
     * @param page
     * @param maxCount
     * @param obj
     * @param search
     * @param user
     * @param userParameter
     * @return
     */
    @Override
    public List<FeedBack> findPaginatedByUser(int page, int maxCount, FeedBack obj, String search, User user, String userParameter) {
        int firstRecord = (page * maxCount) - maxCount;
        Query criteria=null;
        if (userParameter.isEmpty() || userParameter.equalsIgnoreCase("")) {
            criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where user_id=? order by id DESC");
        }else{
            criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where "+userParameter+"=? order by id DESC");
        }
        criteria.setParameter(0, user.getId());
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }

    /**
     * Count of feedbacks for specific user
     * @param user
     * @param userParameter
     * @return
     */
    @Override
    public Long countByUser(User user, String userParameter) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        if (userParameter.isEmpty() || userParameter.equalsIgnoreCase(""))
            criteria.add(Restrictions.eq("user", user));
        else
            criteria.add(Restrictions.eq(userParameter, user));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

}
