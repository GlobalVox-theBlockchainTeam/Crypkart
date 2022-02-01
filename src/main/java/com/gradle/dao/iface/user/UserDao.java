/*
 * Copyright (c) 8/3/18 10:39 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.user.User;

import java.util.List;

public interface UserDao extends GenericDao<User, Integer> {
    public void createVerificationToken(User user, String token) ;
    public List<User> getChildUsers(User user);

}
