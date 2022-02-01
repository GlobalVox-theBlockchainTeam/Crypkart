/*
 * Copyright (c) 5/4/18 5:09 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.user.User;
import org.hibernate.criterion.Projection;

import java.util.List;

public interface EscrowDao extends GenericDao<Escrow, Integer> {
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released);
}
