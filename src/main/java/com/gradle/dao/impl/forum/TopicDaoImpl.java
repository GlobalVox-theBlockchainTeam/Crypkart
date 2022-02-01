/*
 * Copyright (c) 8/3/18 10:52 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.forum;

import com.gradle.dao.iface.forum.TopicDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.forum.Topic;
import org.springframework.stereotype.Repository;

@Repository("topicDao")
public class TopicDaoImpl extends GenericDaoImpl<Topic, Integer> implements TopicDao {

}
