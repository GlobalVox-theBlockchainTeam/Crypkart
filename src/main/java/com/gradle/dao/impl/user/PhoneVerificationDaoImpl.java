/*
 * Copyright (c) 1/5/18 10:11 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.PhoneVerificationDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.PhoneVerification;
import org.springframework.stereotype.Repository;

@Repository("phoneVerificationDao")
public class PhoneVerificationDaoImpl extends GenericDaoImpl<PhoneVerification, Integer> implements PhoneVerificationDao {

}
