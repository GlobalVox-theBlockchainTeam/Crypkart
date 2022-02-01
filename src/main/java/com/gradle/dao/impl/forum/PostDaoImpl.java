/*
 * Copyright (c) 8/3/18 10:37 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.forum;

import com.gradle.dao.iface.forum.PostDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.forum.Post;
import org.springframework.stereotype.Repository;

@Repository("postDao")
public class PostDaoImpl extends GenericDaoImpl<Post, Integer> implements PostDao {

}
