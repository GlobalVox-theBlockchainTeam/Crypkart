/*
 * Copyright (c) 16/3/18 2:31 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.User;

import java.util.List;

public interface FeedBackDao extends GenericDao<FeedBack, Integer> {
    public List<FeedBack> findPaginatedByUser(int page, int maxCount, FeedBack obj, String search, User user, String userParameter);
    public Long countByUser(User user, String userParameter);
}
