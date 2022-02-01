/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.VerificationDao;
import com.gradle.entity.user.VerificationToken;
import com.gradle.services.iface.user.VerificationService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service("verificationService")
@Transactional
public class VerificationServiceImpl extends GenericServiceImpl<VerificationToken, Integer> implements VerificationService {
    @Autowired
    private VerificationDao verificationDao;


    public VerificationServiceImpl() {

    }

    @Autowired
    public VerificationServiceImpl(
            @Qualifier("verificationDao")
                    GenericDao<VerificationToken, Integer> genericDao) {

        super(genericDao);
        this.verificationDao = (VerificationDao) genericDao;
    }

}
