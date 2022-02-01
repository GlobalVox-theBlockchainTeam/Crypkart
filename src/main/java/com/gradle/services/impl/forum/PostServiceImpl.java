/*
 * Copyright (c) 8/3/18 10:48 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.forum;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.forum.PostDao;
import com.gradle.entity.forum.Post;
import com.gradle.services.iface.forum.PostService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("postService")
@Transactional
public class PostServiceImpl extends GenericServiceImpl<Post, Integer> implements PostService{

    @Autowired
    private PostDao postDao;



    public PostServiceImpl() {

    }

    @Autowired
    public PostServiceImpl(@Qualifier("postDao") GenericDao<Post, Integer> genericDao) {
        super(genericDao);
        this.postDao = (PostDao) genericDao;
    }
}
