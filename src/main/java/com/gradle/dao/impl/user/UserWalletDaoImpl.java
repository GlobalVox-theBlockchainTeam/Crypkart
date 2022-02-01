/*
 * Copyright (c) 8/3/18 10:42 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.components.jms.MessageSender;
import com.gradle.dao.iface.user.UserWalletDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("userWalletDao")
public class UserWalletDaoImpl extends GenericDaoImpl<UserWallet, Integer> implements UserWalletDao {

    @Autowired
    private MessageSender messageSender;

    /**
     * Gets current wallet address allocated to user
     * @param user
     * @return
     */
    @Override
    public UserWallet getCurrentUserWallet(User user) {
        Query query = getCurrentSession().createQuery("from UserWallet where user_id=? and current_address=1 order by id DESC");
        query.setParameter(0, user.getId());
        List<UserWallet> userWallets = query.list();
        if (userWallets.size() > 0) {
            return userWallets.get(0);
        } else {
            UserWallet userWallet = new UserWallet();
            userWallet.setUid(user.getId());
            userWallet.setWalletAddress("Pending");
            messageSender.sendMessage(userWallet);
            return userWallet;
        }
    }
}
