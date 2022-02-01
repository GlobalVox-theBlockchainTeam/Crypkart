/*
 * Copyright (c) 6/4/18 11:30 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.ReleasedDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.bitcoin.Released;
import org.springframework.stereotype.Repository;

@Repository("releasedDao")
public class ReleasedDaoImpl extends GenericDaoImpl<Released, Integer> implements ReleasedDao{
}
