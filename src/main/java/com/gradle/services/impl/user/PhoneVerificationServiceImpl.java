/*
 * Copyright (c) 1/5/18 10:13 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.PhoneVerificationDao;
import com.gradle.entity.user.PhoneVerification;
import com.gradle.services.iface.user.PhoneVerificationService;
import com.gradle.services.iface.user.VerificationService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("phoneVerificationService")
@Transactional
public class PhoneVerificationServiceImpl extends GenericServiceImpl<PhoneVerification, Integer> implements PhoneVerificationService {
    @Autowired
    private PhoneVerificationDao phoneVerificationDao;


    public PhoneVerificationServiceImpl() {

    }

    @Autowired
    public PhoneVerificationServiceImpl(
            @Qualifier("phoneVerificationDao")
                    GenericDao<PhoneVerification, Integer> genericDao) {

        super(genericDao);
        this.phoneVerificationDao = (PhoneVerificationDao) genericDao;
    }

}
