/*
 * Copyright (c) 8/3/18 10:40 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.advertisement.TradeStatus;
import com.gradle.entity.user.User;

import java.util.List;

public interface TradeDao extends GenericDao<Trade, Integer> {

    public TradeStatus getTradeStatus(int code);
    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount, String... type);
    public Long countByUser(User user, String... statuses);

}
