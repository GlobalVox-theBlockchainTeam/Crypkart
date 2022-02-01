/*
 * Copyright (c) 5/4/18 5:11 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.bitcoin;

import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;
import org.hibernate.criterion.Projection;

public interface EscrowService extends GenericService<Escrow,Integer> {
    public Double getByProjection(User user, String userProperty, Projection projection, boolean released);
}
