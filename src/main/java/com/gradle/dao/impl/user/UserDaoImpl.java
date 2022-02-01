/*
 * Copyright (c) 8/3/18 10:42 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.UserDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.User;
import com.gradle.entity.user.VerificationToken;
import com.gradle.services.iface.user.VerificationService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository("userDao")
public class UserDaoImpl extends GenericDaoImpl<User, Integer> implements UserDao {

    @Autowired
    private VerificationService verificationService;

    /**
     * Create verification token (for more details check VerificationDao)
     * @param user
     * @param token
     */
    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(verificationToken.calculateExpiryDate(30));
        verificationService.saveOrUpdate(verificationToken);
    }

    @Override
    public List<User> getChildUsers(User user) {
        String query = "from User where parent_id=?";
        Object[] params = new Object[1];
        params[0] = user.getId();
        return queryWithParameter(query, params);
    }
}
