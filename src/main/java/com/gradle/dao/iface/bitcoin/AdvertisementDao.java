/*
 * Copyright (c) 8/3/18 10:40 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.user.User;
import com.gradle.util.CountAdType;
import org.hibernate.Criteria;
import org.hibernate.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface AdvertisementDao extends GenericDao<Advertise, Integer> {

    public List<Advertise> findPaginatedByUser(int page, int maxCount, Advertise obj, String search, User user);
    public Long countByUser(User user);
    public Long countEnabledAdvertisement(User user);
    public List<Advertise> findPaginatedByType(String query, Object[] params, int page, int maxCount);
    public List<Advertise> countByPaymentType(String groupByProperty, Object advertisementType, String restrictionProperty, Object restrictionPropertyValue);
}
