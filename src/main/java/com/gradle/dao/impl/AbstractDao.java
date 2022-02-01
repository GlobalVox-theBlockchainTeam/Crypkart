/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public abstract class AbstractDao<PK extends Serializable, T>   {

    private final  Class<T> persistentClass;

    @SuppressWarnings("unchcked")
    public AbstractDao(){
       this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }


    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession(){return sessionFactory.getCurrentSession();}


    public T getByKey(PK key){return (T) getSession().get(persistentClass, key);}

    public void persist(T entity){getSession().persist(entity);}



    protected Criteria createEntityCriteria(){return getSession().createCriteria(persistentClass);}


}
