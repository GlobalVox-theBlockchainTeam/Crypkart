/*
 * Copyright (c) 24/4/18 5:36 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.user.PasswordResetToken;

public interface PasswordResetDao extends GenericDao<PasswordResetToken, Integer> {
}
