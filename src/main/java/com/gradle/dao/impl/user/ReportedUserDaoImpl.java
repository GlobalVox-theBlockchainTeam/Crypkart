/*
 * Copyright (c) 28/3/18 3:20 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.ReportedUserDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.ReportedUser;
import org.springframework.stereotype.Repository;

@Repository("reportedUserDao")
public class ReportedUserDaoImpl extends GenericDaoImpl<ReportedUser, Integer> implements ReportedUserDao {

}
