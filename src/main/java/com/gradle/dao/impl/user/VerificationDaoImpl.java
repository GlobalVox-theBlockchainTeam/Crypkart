/*
 * Copyright (c) 8/3/18 10:42 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.VerificationDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.VerificationToken;
import org.springframework.stereotype.Repository;

@Repository("verificationDao")
public class VerificationDaoImpl extends GenericDaoImpl<VerificationToken, Integer> implements VerificationDao{

}
