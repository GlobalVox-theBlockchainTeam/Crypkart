/*
 * Copyright (c) 3/4/18 11:20 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.ZonesDao;
import com.gradle.entity.user.Zones;
import com.gradle.services.iface.user.ZonesService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("zonesService")
@Transactional
public class ZonesServiceImpl extends GenericServiceImpl<Zones, Integer> implements ZonesService {

    @Autowired
    private ZonesDao zonesDao;

    public ZonesServiceImpl() {

    }

    @Autowired
    public ZonesServiceImpl(
            @Qualifier("zonesDao")
                    GenericDao<Zones, Integer> genericDao) {
        super(genericDao);
        this.zonesDao = (ZonesDao) genericDao;
    }
}