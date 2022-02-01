/*
 * Copyright (c) 24/4/18 5:38 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.PasswordResetDao;
import com.gradle.entity.user.PasswordResetToken;
import com.gradle.services.iface.user.PasswordResetService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("passwordResetService")
@Transactional
public class PasswordResetServiceImpl extends GenericServiceImpl<PasswordResetToken, Integer> implements PasswordResetService {
    @Autowired
    private PasswordResetDao PasswordResetDao;


    public PasswordResetServiceImpl() {

    }

    @Autowired
    public PasswordResetServiceImpl(
            @Qualifier("passwordResetDao")
                    GenericDao<PasswordResetToken, Integer> genericDao) {

        super(genericDao);
        this.PasswordResetDao = (PasswordResetDao) genericDao;
    }

}
