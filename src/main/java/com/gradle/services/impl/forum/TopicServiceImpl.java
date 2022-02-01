/*
 * Copyright (c) 8/3/18 10:53 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.forum;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.forum.TopicDao;
import com.gradle.entity.forum.Topic;
import com.gradle.services.iface.forum.TopicService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("topicService")
@Transactional
public class TopicServiceImpl extends GenericServiceImpl<Topic, Integer> implements TopicService {

    @Autowired
    private TopicDao topicDao;



    public TopicServiceImpl() {

    }

    @Autowired
    public TopicServiceImpl(@Qualifier("topicDao") GenericDao<Topic, Integer> genericDao) {
        super(genericDao);
        this.topicDao = (TopicDao) genericDao;
    }
}
