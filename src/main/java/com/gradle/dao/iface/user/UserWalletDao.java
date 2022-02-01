/*
 * Copyright (c) 8/3/18 10:39 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;

public interface UserWalletDao extends GenericDao<UserWallet, Integer> {
    public UserWallet getCurrentUserWallet(User user);
}
