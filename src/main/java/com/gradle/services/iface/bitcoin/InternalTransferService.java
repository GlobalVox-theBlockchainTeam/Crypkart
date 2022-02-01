/*
 * Copyright (c) 11/4/18 10:08 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.bitcoin;

import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.bitcoin.InternalTransfer;
import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;
import org.hibernate.criterion.Projection;

import java.util.List;

public interface InternalTransferService extends GenericService<InternalTransfer,Integer> {
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released);
}
