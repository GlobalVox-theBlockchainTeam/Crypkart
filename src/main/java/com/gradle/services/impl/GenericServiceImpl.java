/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.TransactionScoped;
import java.beans.Transient;
import java.util.List;


/**
 * All methods are calling GenericDaoImpl methods so for detailed comments
 *
 * @param <E>
 * @param <K>
 * @see com.gradle.dao.impl.GenericDaoImpl
 */

@Service
@Transactional
public abstract class GenericServiceImpl<E, K>
        implements GenericService<E, K> {

    private GenericDao<E, K> genericDao;

    public GenericServiceImpl(GenericDao<E, K> genericDao) {
        this.genericDao = genericDao;
    }

    public GenericServiceImpl() {
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(E entity) {
        genericDao.saveOrUpdate(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> findAll() {
        return genericDao.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public E find(K id) {
        return genericDao.find(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(E entity) {
        genericDao.save(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(E entity) {
        genericDao.update(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(E entity) {
        genericDao.delete(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> query(String query) {
        return genericDao.query(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> hql(Query query) {
        return genericDao.hql(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Query getQuery(String query) {
        return genericDao.getQuery(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Query getNamedQuery(String query) {
        return genericDao.getNamedQuery(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> executeQuery(Query query) {
        return genericDao.executeQuery(query);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> queryWithParameter(String queryString, Object[] params) {
        return genericDao.queryWithParameter(queryString, params);
    }

    @Override
    public E first(String queryString, Object[] params) {
        return genericDao.first(queryString, params);
    }


    @Override
    public Criteria getCriteria(E obj) {
        return genericDao.getCriteria(obj);
    }

    @Override
    public List<E> getByCriteria(Criteria criteria) {
        return genericDao.getByCriteria(criteria);
    }

    @Override
    public List<Object> queryAsObjectList(String query) {
        return genericDao.queryAsObjectList(query);
    }


    @Override
    public List<AdminConfigValues> getConfigValues(String tableName) {
        return genericDao.getConfigValues(tableName);
    }

    @Override
    public List<AdminConfig> getConfig(String tableName) {
        return genericDao.getConfig(tableName);
    }

    @Override
    public List<E> findPaginated(int page, int maxCount, E obj, String search) {
        return genericDao.findPaginated(page, maxCount, obj, search);
    }

    @Override
    public Long count(E obj) {
        return genericDao.count(obj);
    }

    /*@Transactional
        @Override
        public Page<E> findAll(Pageable pageRequest){
            return genericDao.findAll(pageRequest);
        }*/
    @Override
    public Long countByUser(User user) {
        return this.genericDao.countByUser(user);
    }


    @Override
    public List<E> findPaginatedByUser(int page, int maxCount, E obj, String search, User user) {
        return this.genericDao.findPaginatedByUser(page, maxCount, obj, search, user);
    }

    @Override
    public Long countQuery(String query, Object[] params) {
        return this.genericDao.countQuery(query, params);
    }

    @Override
    public List<AdminConfigValues> getGeneralConfig() {
        return this.genericDao.getGeneralConfig();
    }

    @Override
    public List<AdminConfigValues> getEntityConfig(E obj) {
        return this.genericDao.getEntityConfig(obj);
    }


    @Override
    public Long avg(E obj, String avgPropertyName) {
        return this.genericDao.avg(obj, avgPropertyName);
    }

    @Override
    public Double avgByUser(User user, String avgPropertyName, String userProperty) {
        return this.genericDao.avgByUser(user, avgPropertyName, userProperty);
    }

    @Override
    public Double getByProjection(User user, String userProperty, Projection projection) {
        return this.genericDao.getByProjection(user, userProperty, projection);
    }

    @Override
    public List<E> findPaginatedByUser(int page, int maxCount, E obj, User user, String userProperty) {
        return this.genericDao.findPaginatedByUser(page, maxCount, obj, user, userProperty);
    }

    @Override
    public List<E> findPaginatedByUserWithOrder(int page, int maxCount, E obj, User user, String userProperty, String orderProperty, String orderDirection) {
        return this.genericDao.findPaginatedByUserWithOrder(page, maxCount, obj, user, userProperty, orderProperty, orderDirection);
    }

    @Override
    public Long countByUserByProperty(User user, String userProperty) {
        return this.genericDao.countByUserByProperty(user, userProperty);
    }

    @Override
    public boolean executeQuery(String query, Object[] params) {
        return this.genericDao.executeQuery(query, params);
    }


    @Override
    public void refresh(E obj) {
        this.genericDao.refresh(obj);
    }


    @Override
    public E last(String orderProperty) {
        return this.genericDao.last(orderProperty);
    }

    @Override
    public List<E> getListByProjection(String property) {
        return this.genericDao.getListByProjection(property);
    }
}