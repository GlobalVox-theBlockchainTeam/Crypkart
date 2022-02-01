/*
 * Copyright (c) 3/4/18 11:19 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.user;

import com.gradle.dao.iface.user.ZonesDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.user.Zones;
import org.springframework.stereotype.Repository;

@Repository("zonesDao")
public class ZonesDaoImpl extends GenericDaoImpl<Zones, Integer> implements ZonesDao {


}
