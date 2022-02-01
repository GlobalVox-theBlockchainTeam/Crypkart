/*
 * Copyright (c) 24/4/18 5:37 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.PasswordResetDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.PasswordResetToken;
import org.springframework.stereotype.Repository;

@Repository("passwordResetDao")
public class PasswordResetDaoImpl extends GenericDaoImpl<PasswordResetToken, Integer> implements PasswordResetDao {

}
