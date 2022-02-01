/*
 * Copyright (c) 6/4/18 2:55 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.bitcoin;

import com.gradle.entity.bitcoin.DirectTransaction;
import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;
import org.hibernate.criterion.Projection;

import java.util.List;

public interface DirectTransactionService extends GenericService<DirectTransaction,Integer> {
    public Double getByProjection(User user, String userProperty, Projection projection, boolean outgoing);
    public List<DirectTransaction> getByType(int page, int maxCount, DirectTransaction obj, User user, String userProperty, String property, boolean value);
}
