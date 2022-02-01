/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.user.User;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * This class contains all basic required method for persisting to db using hibernate
 *
 * @param <E> Generic class : will be casted to dao class passed as an arguments in methods
 * @param <K> Generic Integer key : will be used as an Integer key
 */


@SuppressWarnings("unchecked")
@Repository

/*@Transactional*/
public abstract class GenericDaoImpl<E, K extends Serializable> implements GenericDao<E, K> {

    @Autowired
    private SessionFactory sessionFactory;

    @PersistenceContext
    EntityManager entityManager;

    protected Class<? extends E> daoType;


    /**
     * By defining this class as abstract, we prevent Spring from creating
     * instance of this class If not defined as abstract,
     * getClass().getGenericSuperClass() would return Object. There would be
     * exception because Object class does not hava constructor with parameters.
     */
    public GenericDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        daoType = (Class) pt.getActualTypeArguments()[0];


    }

    // Getting current hibernate session to persist
    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * getting JPA entity manager combined with hibernate session
     * by doing this we will be able to use hibernate sesssion factory
     * features with JAP entity manger's feature
     * <p>
     * JAP : annotations like prePersist, perUpdate, preDelete etc...
     * Hibernate Session Factory : easy methods to persist to db like
     * save, saveOrUpdate, delete etc...
     *
     * @return
     */
    protected Session getCurrentSession() {
        //return sessionFactory.getCurrentSession();
        return this.entityManager.unwrap(Session.class);

    }

    /**
     * Save entity by casting
     *
     * @param entity
     */
    @Override
    public void save(E entity) {
        getCurrentSession().save(entity);
    }

    /*@Override
    public void saveOrUpdate(E entity) {
        currentSession().saveOrUpdate(entity);
    }

    @Override
    public void update(E entity) {
        currentSession().saveOrUpdate(entity);
    }*/


    /**
     * if id present in object, it will be updated else saved
     *
     * @param entity
     */
    @Override
    public void saveOrUpdate(E entity) {
        getCurrentSession().saveOrUpdate(entity);

    }

    /**
     * Update entity
     *
     * @param entity
     */
    @Override
    public void update(E entity) {
        getCurrentSession().update(entity);
        //getCurrentSession().update(entity);

    }

    /**
     * Delete entity
     *
     * @param entity
     */
    @Override
    public void delete(E entity) {
        //this.entityManager.remove(entity);
        /*entity = this.entityManager.contains(entity) ? entity : this.entityManager.merge(entity);
        //this.entityManager.remove(entity);
        Session currentSession = sessionFactory.getCurrentSession();
        this.entityManager.setFlushMode(FlushModeType.COMMIT);
        this.entityManager.unwrap(currentSession.getClass()).delete(entity);
        this.entityManager.getFlushMode();*/


        //currentSession.delete(entity);

        //getCurrentSession().clear();
        sessionFactory.getCurrentSession().delete(entity);

        //getCurrentSession().delete(entity);
        //this.entityManager.flush();
    }

    /**
     * find by key(primary)
     */
    @Override
    public E find(K key) {

        return (E) getCurrentSession().get(daoType, key);
    }

    /**
     * Find all records for specific entity
     *
     * @return
     */
    @Override
    public List<E> findAll() {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        //criteria.addOrder(Order.desc("id"));
        return criteria.list();
        //return getCurrentSession().createCriteria(daoType).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    /**
     * Raw query
     *
     * @param queryString
     * @return
     */
    @Override
    public List<E> query(String queryString) {
        //Query query = getCurrentSession().createQuery(queryString);
        return getCurrentSession().createQuery(queryString).list();
        //return this.entityManager.createQuery(queryString).getResultList();
        //return query.list();
    }

    /**
     * Raw query with parameters (can combine query and queryWithParameters methods but
     * for ease of use we have separate methods :)
     *
     * @param queryString
     * @param params
     * @return
     */
    @Override
    public List<E> queryWithParameter(String queryString, Object[] params) {
        Query query = getCurrentSession().createQuery(queryString);
        int i = 0;
        for (Object param : params) {
            query.setParameter(i, param);
            i++;
        }

        return query.list();
    }

    /**
     * This method can be used if you want to have a query object in any other class
     *
     * @param query
     * @return
     */
    @Override
    public Query getQuery(String query) {
        return this.entityManager.unwrap(Session.class).createQuery(query);
    }

    /**
     * @param query
     * @return
     */
    @Override
    public Query getNamedQuery(String query) {
        return this.entityManager.unwrap(Session.class).getNamedQuery(query);
    }


    /**
     * If you have raw query object you can use this method to just execute it
     *
     * @param query
     * @return
     */
    @Override
    public List<E> executeQuery(Query query) {
        return query.list();
    }


    /**
     * Raw hql (hibernate query language)
     *
     * @param query
     * @return
     */
    @Override
    public List<E> hql(Query query) {
        return query.list();
    }


    /**
     * get first object of list
     *
     * @param queryString
     * @param params
     * @return
     */
    @Override
    public E first(String queryString, Object[] params) {
        Query query = getCurrentSession().createQuery(queryString);
        int i = 0;
        for (Object param : params) {
            query.setParameter(i, param);
            i++;
        }

        List<E> list = query.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    /**
     * Get criteria using hiberante session factory
     *
     * @param obj
     * @return
     */
    @Override
    public Criteria getCriteria(E obj) {
        Session session = getCurrentSession();

        Criteria crit = session.createCriteria(obj.getClass());
        return crit;
    }

    /**
     *
     * @param criteria
     * @return
     */
    @Override
    public List<E> getByCriteria(Criteria criteria) {
        return criteria.list();
    }

    /**
     * Returns result as Object List
     * @param query
     * @return
     */
    @Override
    public List<Object> queryAsObjectList(String query) {
        List<Object> list = getCurrentSession().createSQLQuery(query).list();

        return list;
    }


    /**
     * Get config values for a specific table like "users"
     * @param tableName
     * @return
     */
    @Override
    public List<AdminConfigValues> getConfigValues(String tableName) {
        List<AdminConfig> adminConfigs = getConfig(tableName);
        if (adminConfigs.size() > 0) {
            Query query = getCurrentSession().createQuery("from AdminConfigValues where admin_config_id=?");
            query.setParameter(0, adminConfigs.get(0).getId());
            List<AdminConfigValues> adminconfigValues = query.list();
            return adminconfigValues;
        }
        return new ArrayList<AdminConfigValues>();
    }

    /**
     * Get config for specific tables like "user"
     * @param tableName
     * @return
     */
    @Override
    public List<AdminConfig> getConfig(String tableName) {
        //String tableName = obj.getClass().getAnnotationsByType(Table.class)[0].name();
        Query query = getCurrentSession().createQuery("from AdminConfig where data_table=?");
        query.setParameter(0, tableName);
        List<AdminConfig> adminConfig = query.list();
        return adminConfig;
    }


    /**
     * Get list of objects using pagination constrain
     * @param page
     * @param maxCount
     * @param obj
     * @param search
     * @return
     */
    @Override
    public List<E> findPaginated(int page, int maxCount, E obj, String search) {
        int firstRecord = (page * maxCount) - maxCount;


        Query criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString());
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }

    /**
     * Total count of records of table
     * @param obj
     * @return
     */
    @Override
    public Long count(E obj) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    /**
     * Total records in table for specific user
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
     * Total records for a specific user in table but for different properties
     * eg. in most of the tables there is a filed of user_id but in few tables there are more fields like buyer and seller
     * @param user
     * @param userProperty
     * @return
     */
    @Override
    public Long countByUserByProperty(User user, String userProperty) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.add(Restrictions.eq(userProperty, user));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }


    /**
     * Gets records for tables for specific user but with pagination constrain
     * @param page
     * @param maxCount
     * @param obj
     * @param search
     * @param user
     * @return
     */
    @Override
    public List<E> findPaginatedByUser(int page, int maxCount, E obj, String search, User user) {
        int firstRecord = (page * maxCount) - maxCount;
        Query criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where user_id=?");
        criteria.setParameter(0, user.getId());
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }


    /**
     *
     * @param page
     * @param maxCount
     * @param obj
     * @param user
     * @param userProperty
     * @return
     */
    @Override
    public List<E> findPaginatedByUser(int page, int maxCount, E obj, User user, String userProperty) {

        int firstRecord = (page * maxCount) - maxCount;
        Query criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where " + userProperty + "=?");
        criteria.setParameter(0, user.getId());
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }


    @Override
    public List<E> findPaginatedByUserWithOrder(int page, int maxCount, E obj, User user, String userProperty, String orderProperty, String orderDirection) {
        int firstRecord = (page * maxCount) - maxCount;
        orderProperty = (orderProperty==null) ? "id" : orderProperty;
        orderDirection = (orderDirection!=null) ? "DESC" : orderDirection;
        Query criteria = getCurrentSession().createQuery(" from " + obj.getClass().getName().toString() + " where " + userProperty + "=? order by " + orderProperty + " " + orderDirection);
        criteria.setParameter(0, user.getId());
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(maxCount);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setReadOnly(true);
        return criteria.list();
    }

    /**
     * Count records using hql (hibernate query language)
     * @param queryString
     * @param params
     * @return
     */
    @Override
    public Long countQuery(String queryString, Object[] params) {

        //Query query = getCurrentSession().createQuery(queryString);
        Query query = sessionFactory.getCurrentSession().createQuery(queryString);
        int i = 0;
        for (Object param : params) {
            query.setParameter(i, param);
            i++;
        }
        return (Long) query.uniqueResult();
    }


    /**
     * Gets average of records for specific column using criteria of hibernate
     * @param obj
     * @param avgPropertyName
     * @return
     */
    @Override
    public Long avg(E obj, String avgPropertyName) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.setProjection(Projections.avg(avgPropertyName));
        return (Long) criteria.uniqueResult();
    }


    /**
     * for specific user - Gets average of records for specific column using criteria of hibernate
     * @param user
     * @param avgPropertyName
     * @param userProperty
     * @return
     */
    @Override
    public Double avgByUser(User user, String avgPropertyName, String userProperty) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.add(Restrictions.eq(userProperty, user));
        criteria.setProjection(Projections.avg(avgPropertyName));
        return (criteria.uniqueResult() != null) ? (Double) criteria.uniqueResult() : 0d;
    }

    /**
     * Gets projection of records for specific column using criteria of hibernate
     * @param user
     * @param userProperty
     * @param projection
     * @return
     */
    @Override
    public Double getByProjection(User user, String userProperty, Projection projection) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        criteria.add(Restrictions.eq(userProperty, user));
        criteria.setProjection(projection);
        Object result = criteria.uniqueResult();

        return (result != null) ? Double.valueOf(result.toString()).doubleValue() : 0d;
    }


    /**
     * Gets general configurations
     * @return
     */
    @Override
    public List<AdminConfigValues> getGeneralConfig() {
        List<AdminConfigValues> adminConfigValuesList = new ArrayList<>();


        Query query = getCurrentSession().createQuery(" from AdminConfig where data_table=?");
        query.setParameter(0, "General");
        List<AdminConfig> adminConfigList = query.list();
        if (adminConfigList.size() > 0) {
            query = getCurrentSession().createQuery(" from AdminConfigValues where admin_config_id=?");
            query.setParameter(0, adminConfigList.get(0).getId());
            adminConfigValuesList = query.list();
        }

        return adminConfigValuesList;
    }

    /**
     * Get config for specific table
     * @param obj
     * @return
     */
    @Override
    public List<AdminConfigValues> getEntityConfig(E obj) {
        List<AdminConfigValues> adminConfigValuesList = new ArrayList<>();
        Query query = getCurrentSession().createQuery(" from AdminConfig where data_table=?");
        query.setParameter(0, obj.getClass().getName().toString());
        List<AdminConfig> adminConfigs = query.list();
        if (adminConfigs.size() > 0) {
            query = getCurrentSession().createQuery(" from AdminConfigValues where admin_config_id=?");
            query.setParameter(0, adminConfigs.get(0).getId());
            adminConfigValuesList = query.list();
        }
        return adminConfigValuesList;
    }


    /**
     * Execute simple query with parameters
     * @param query
     * @param params
     * @return
     */
    @Override
    public boolean executeQuery(String query, Object[] params) {
        Query hql = getCurrentSession().createQuery(query);
        int i = 0;
        for (Object param : params) {
            hql.setParameter(i, param);
            i++;
        }
        hql.executeUpdate();
        return true;
    }


    /**
     * refresh Hibernate entity
     * @param obj
     */
    @Override
    public void refresh(E obj) {
        getCurrentSession().refresh(obj);
    }

    /**
     * Gets last record from table
     * @param orderProperty
     * @return
     */
    @Override
    public E last(String orderProperty) {
        Query query = getCurrentSession().createQuery("from " + daoType.getName() + " order by "+orderProperty+" DESC");
        query.setMaxResults(1);
        return (E)query.uniqueResult();
    }

    @Override
    public List<E> getListByProjection(String property){
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        return criteria.setProjection(Projections.projectionList().add(Projections.groupProperty(property)).add(Projections.rowCount(), "count")).list();
    }

}