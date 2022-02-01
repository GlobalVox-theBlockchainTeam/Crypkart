/*
 * Copyright (c) 8/3/18 10:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.AdvertisementDao;
import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.AdvertisementService;
import com.gradle.services.impl.GenericServiceImpl;
import com.gradle.util.CountAdType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("advertisementService")
@Transactional
public class AdvertisementServiceImpl extends GenericServiceImpl<Advertise, Integer> implements AdvertisementService {


    @Autowired
    private AdvertisementDao advertisementDao;

    public AdvertisementServiceImpl() {

    }

    @Autowired
    public AdvertisementServiceImpl(
            @Qualifier("advertisementDao")
                    GenericDao<Advertise, Integer> genericDao) {
        super(genericDao);
        this.advertisementDao = (AdvertisementDao) genericDao;
    }

    @Override
    public List<Advertise> findPaginatedByUser(int page, int maxCount, Advertise obj, String search, User user) {
        return this.advertisementDao.findPaginatedByUser(page, maxCount, obj, search, user);
    }

    @Override
    public Long countByUser(User user) {
        return this.advertisementDao.countByUser(user);
    }

    @Override
    public Long countEnabledAdvertisement(User user) {
        return this.advertisementDao.countEnabledAdvertisement(user);
    }

    @Override
    public List<Advertise> findPaginatedByType(String query, Object[] params, int page, int maxCount) {
        return this.advertisementDao.findPaginatedByType(query, params, page, maxCount);
    }

    @Override
    public List<Advertise> countByPaymentType(String groupByProperty, Object advertisementType, String restrictionProperty, Object restrictionPropertyValue) {
        return this.advertisementDao.countByPaymentType(groupByProperty, advertisementType, restrictionProperty, restrictionPropertyValue);
    }
}