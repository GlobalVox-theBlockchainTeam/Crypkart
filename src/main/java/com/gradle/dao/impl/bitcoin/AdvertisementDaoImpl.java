/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.AdvertisementDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.user.User;
import com.gradle.util.CountAdType;
import com.gradle.util.ServiceUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository("advertisementDao")
public class AdvertisementDaoImpl extends GenericDaoImpl<Advertise, Integer> implements AdvertisementDao{

    /**
     * This method gets user records according to the paging parameters for advertisement
     * @param page
     * @param maxCount
     * @param obj
     * @param search
     * @param user
     * @return
     */
    @Override
    public List<Advertise> findPaginatedByUser(int page, int maxCount, Advertise obj, String search, User user) {
        int firstRecord = (page * maxCount) - maxCount;
        Query criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where user_id=? order by id DESC");
        criteria.setParameter(0, user.getId());
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }

    /**
     * This method gets count of advertisement records for specific user
     * @param user
     * @return
     */
    @Override
    public Long countByUser(User user) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.add(Restrictions.eq("user", user));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }


    /**
     * This method gets count of advertisement for specific user which advertisements are enabled
     * @param user
     * @return
     */
    @Override
    public Long countEnabledAdvertisement(User user) {

        Criteria criteria = getCurrentSession().createCriteria(daoType);

        if (user.getParent()!=null){
            Criterion parentCriteria = Restrictions.eq("parent", user);
            Criterion userCriteria = Restrictions.eq("user", user);
            criteria.add(Restrictions.or(parentCriteria, userCriteria));
        }else{
            criteria.add(Restrictions.eq("user", user));
        }
        criteria.add(Restrictions.eq("status", true));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    /**
     * Gets advertise list based on type of the advertisement and with paging constrain
     * @param query
     * @param params
     * @param page
     * @param maxCount
     * @return
     */
    @Override
    public List<Advertise> findPaginatedByType(String query, Object[] params, int page, int maxCount) {
        int firstRecord = (page * maxCount) - maxCount;
        Query criteria = getCurrentSession().createQuery(query);

        int i = 0;
        for (Object param : params) {
            criteria.setParameter(i, param);
            i++;
        }
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }

    /*@Override
    public List<CountAdType> countByPaymentType(String advertismentType) {

        Query query = getCurrentSession().createSQLQuery("select count(`payment_type_id`),`payment_type_id` from advertisement_master where `advertisement_type`=? GROUP By payment_type_id;");
        query.setParameter(0,advertismentType);

        List<CountAdType> list = new ArrayList<>();

        List<Object[]> rows = query.list();
        for(Object[] row : rows){
            CountAdType count = new CountAdType();
            count.setCount(Integer.valueOf(row[0].toString()));
            count.setPayment_type_id(Integer.valueOf(row[1].toString()));
            list.add(count);

        }
        return list;
    }*/

    @Override
    public List<Advertise> countByPaymentType(String groupByProperty, Object advertisementType, String restrictionProperty, Object restrictionPropertyValue){
        Criteria criteria = getCurrentSession().createCriteria(Advertise.class);
        Criterion restriction1 = Restrictions.eq(restrictionProperty, restrictionPropertyValue);
        Criterion restriction2 = Restrictions.eq("advertisementType", advertisementType);
        Criterion criterion = Restrictions.and(restriction1, restriction2);
        criteria.add(criterion);
        return criteria.setProjection(Projections.projectionList().add(Projections.groupProperty(groupByProperty)).add(Projections.rowCount(), "count")).list();
    }
}
