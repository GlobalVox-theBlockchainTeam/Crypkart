/*
 * Copyright (c) 11/4/18 10:09 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.InternalTransferDao;
import com.gradle.entity.bitcoin.InternalTransfer;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.InternalTransferService;
import com.gradle.services.impl.GenericServiceImpl;
import org.hibernate.criterion.Projection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("internalTransferService")
@Transactional
public class InternalTransferServiceImpl extends GenericServiceImpl<InternalTransfer, Integer> implements InternalTransferService {

    @Autowired
    private InternalTransferDao internalTransferDao;



    public InternalTransferServiceImpl() {

    }

    @Autowired
    public InternalTransferServiceImpl(@Qualifier("internalTransferDao") GenericDao<InternalTransfer, Integer> genericDao) {
        super(genericDao);
        this.internalTransferDao = (InternalTransferDao) genericDao;
    }

    @Override
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released) {
        return this.internalTransferDao.getByProjection(user, userProperty, projection, released);
    }
}
