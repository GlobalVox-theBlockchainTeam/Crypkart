/*
 * Copyright (c) 16/3/18 2:33 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.user;

import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;

import java.util.List;

public interface FeedBackService extends GenericService<FeedBack,Integer> {
    public List<FeedBack> findPaginatedByUser(int page, int maxCount, FeedBack obj, String search, User user, String userParameter);
    public Long countByUser(User user, String userParameter);
}
