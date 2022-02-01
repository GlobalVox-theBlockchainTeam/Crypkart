/*
 * Copyright (c) 28/3/18 3:24 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.ReportedUserDao;
import com.gradle.entity.user.ReportedUser;
import com.gradle.services.iface.user.ReportedUserService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("reportedUserService")
@Transactional
public class ReportedUserServiceImpl extends GenericServiceImpl<ReportedUser, Integer> implements ReportedUserService {

    @Autowired
    private ReportedUserDao reportedUserDao;
    public ReportedUserServiceImpl() {

    }
    @Autowired
    public ReportedUserServiceImpl(
            @Qualifier("reportedUserDao")
                    GenericDao<ReportedUser, Integer> genericDao) {
        super(genericDao);
        this.reportedUserDao = (ReportedUserDao) genericDao;
    }
}