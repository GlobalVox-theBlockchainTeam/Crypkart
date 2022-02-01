/*
 * Copyright (c) 6/4/18 11:31 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.ReleasedDao;
import com.gradle.entity.bitcoin.Released;
import com.gradle.services.iface.bitcoin.ReleasedService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("releasedService")
@Transactional
public class ReleasedServiceImpl extends GenericServiceImpl<Released, Integer> implements ReleasedService {

    @Autowired
    private ReleasedDao releasedDao;



    public ReleasedServiceImpl() {

    }

    @Autowired
    public ReleasedServiceImpl(@Qualifier("releasedDao") GenericDao<Released, Integer> genericDao) {
        super(genericDao);
        this.releasedDao = (ReleasedDao) genericDao;
    }
}
