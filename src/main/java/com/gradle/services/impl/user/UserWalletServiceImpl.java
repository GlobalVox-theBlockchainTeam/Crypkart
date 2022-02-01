/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.UserWalletDao;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import com.gradle.services.iface.user.UserWalletService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("userWalletService")
@Transactional
public class UserWalletServiceImpl extends GenericServiceImpl<UserWallet, Integer> implements UserWalletService {

    @Autowired
    private UserWalletDao userDao;
    public UserWalletServiceImpl() {

    }
    @Autowired
    public UserWalletServiceImpl(
            @Qualifier("userWalletDao")
                    GenericDao<UserWallet, Integer> genericDao) {
        super(genericDao);
        this.userDao = (UserWalletDao) genericDao;
    }

    @Override
    public UserWallet getCurrentUserWallet(User user) {
        return this.userDao.getCurrentUserWallet(user);
    }
}