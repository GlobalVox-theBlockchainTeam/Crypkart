/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl;

import com.gradle.dao.iface.CMSDao;
import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.frontend.CMS;
import com.gradle.services.iface.CMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("cmsService")
@Transactional
public class CMSServiceImpl extends GenericServiceImpl<CMS, Integer> implements CMSService {

    @Autowired
    private CMSDao cmsDao;



    public CMSServiceImpl() {

    }

    @Autowired
    public CMSServiceImpl(@Qualifier("cmsDao") GenericDao<CMS, Integer> genericDao) {
        super(genericDao);
        this.cmsDao = (CMSDao) genericDao;
    }

    @Override
    public Page<CMS> findByCMS(Pageable pageable) {
        return this.cmsDao.findByCMS(pageable);
    }
}
