/*
 * Copyright (c) 11/4/18 10:05 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.bitcoin.InternalTransfer;
import com.gradle.entity.user.User;
import org.hibernate.criterion.Projection;

import java.util.List;

public interface InternalTransferDao extends GenericDao<InternalTransfer, Integer> {
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released);
}
