/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl;

import com.gradle.dao.iface.CMSDao;
import com.gradle.entity.frontend.CMS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository("cmsDao")
public class CMSDaoImpl extends GenericDaoImpl<CMS, Integer> implements CMSDao {
    @Override
    public Page<CMS> findByCMS(Pageable pageable) {
        return null;
    }
}
