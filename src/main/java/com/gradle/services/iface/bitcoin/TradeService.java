/*
 * Copyright (c) 8/3/18 10:44 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.bitcoin;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.advertisement.TradeStatus;
import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;

import java.util.List;

public interface TradeService extends GenericService<Trade,Integer> {
    public TradeStatus getTradeStatus(int code);

    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount);
    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount, String type);
    public Long countByUser(User user, String statuses);

}
