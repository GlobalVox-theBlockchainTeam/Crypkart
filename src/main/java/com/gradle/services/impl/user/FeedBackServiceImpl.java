/*
 * Copyright (c) 16/3/18 2:34 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.FeedBackDao;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.User;
import com.gradle.services.iface.user.FeedBackService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("feedBackService")
@Transactional
public class FeedBackServiceImpl extends GenericServiceImpl<FeedBack, Integer> implements FeedBackService {

    @Autowired
    private FeedBackDao feedBackDao;

    public FeedBackServiceImpl() {

    }

    @Autowired
    public FeedBackServiceImpl(
            @Qualifier("feedBackDao")
                    GenericDao<FeedBack, Integer> genericDao) {
        super(genericDao);
        this.feedBackDao = (FeedBackDao) genericDao;
    }

    @Override
    public List<FeedBack> findPaginatedByUser(int page, int maxCount, FeedBack obj, String search, User user, String userParameter) {
        return this.feedBackDao.findPaginatedByUser(page, maxCount, obj, search, user, userParameter);
    }

    @Override
    public Long countByUser(User user, String userParameter) {
        return this.feedBackDao.countByUser(user,userParameter);
    }
}