/*
 * Copyright (c) 5/4/18 5:11 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.EscrowDao;
import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.EscrowService;
import com.gradle.services.impl.GenericServiceImpl;
import org.hibernate.criterion.Projection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("escrowService")
@Transactional
public class EscrowServiceImpl extends GenericServiceImpl<Escrow, Integer> implements EscrowService {

    @Autowired
    private EscrowDao escrowDao;



    public EscrowServiceImpl() {

    }

    @Autowired
    public EscrowServiceImpl(@Qualifier("escrowDao") GenericDao<Escrow, Integer> genericDao) {
        super(genericDao);
        this.escrowDao = (EscrowDao) genericDao;
    }

    @Override
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released) {
        return this.escrowDao.getByProjection(user, userProperty, projection, released);
    }
}
