/*
 * Copyright (c) 8/3/18 10:44 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.user;

import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import com.gradle.services.iface.GenericService;

public interface UserWalletService extends GenericService<UserWallet,Integer> {
    public UserWallet getCurrentUserWallet(User user) ;
}
