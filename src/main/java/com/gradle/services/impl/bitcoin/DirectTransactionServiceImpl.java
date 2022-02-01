/*
 * Copyright (c) 6/4/18 2:56 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.DirectTransactionDao;
import com.gradle.entity.bitcoin.DirectTransaction;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.DirectTransactionService;
import com.gradle.services.impl.GenericServiceImpl;
import org.hibernate.criterion.Projection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("directTransactionService")
@Transactional
public class DirectTransactionServiceImpl extends GenericServiceImpl<DirectTransaction, Integer> implements DirectTransactionService {

    @Autowired
    private DirectTransactionDao directTransactionDao;


    public DirectTransactionServiceImpl() {

    }

    @Autowired
    public DirectTransactionServiceImpl(@Qualifier("directTransactionDao") GenericDao<DirectTransaction, Integer> genericDao) {
        super(genericDao);
        this.directTransactionDao = (DirectTransactionDao) genericDao;
    }

    @Override
    public Double getByProjection(User user, String userProperty, Projection projection, boolean outgoing) {
        return this.directTransactionDao.getByProjection(user, userProperty, projection, outgoing);
    }

    @Override
    public List<DirectTransaction> getByType(int page, int maxCount, DirectTransaction obj, User user, String userProperty, String property, boolean value) {
        return this.directTransactionDao.getByType(page,maxCount,obj,user,userProperty,property,value);
    }
}
