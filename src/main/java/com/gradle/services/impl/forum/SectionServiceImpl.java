/*
 * Copyright (c) 8/3/18 10:53 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.forum;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.forum.SectionDao;
import com.gradle.entity.forum.Section;
import com.gradle.services.iface.forum.SectionService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("sectionService")
@Transactional
public class SectionServiceImpl extends GenericServiceImpl<Section, Integer> implements SectionService{

    @Autowired
    private SectionDao sectionDao;



    public SectionServiceImpl() {

    }

    @Autowired
    public SectionServiceImpl(@Qualifier("sectionDao") GenericDao<Section, Integer> genericDao) {
        super(genericDao);
        this.sectionDao = (SectionDao) genericDao;
    }
}
