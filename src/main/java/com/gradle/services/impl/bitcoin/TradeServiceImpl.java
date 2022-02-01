/*
 * Copyright (c) 8/3/18 10:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.TradeDao;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.advertisement.TradeStatus;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("tradeService")
@Transactional
public class TradeServiceImpl extends GenericServiceImpl<Trade, Integer> implements TradeService {

    @Autowired
    private TradeDao tradeDao;



    public TradeServiceImpl() {

    }

    @Autowired
    public TradeServiceImpl(@Qualifier("tradeDao") GenericDao<Trade, Integer> genericDao) {
        super(genericDao);
        this.tradeDao = (TradeDao) genericDao;
    }

    @Override
    public TradeStatus getTradeStatus(int code) {
        return tradeDao.getTradeStatus(code);
    }

    @Override
    @Transactional
    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount) {
        return tradeDao.getUsersTrades(orderField, orderDirection, paging, page, maxCount);
    }

    @Override
    @Transactional
    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount, String type) {
        return tradeDao.getUsersTrades(orderField, orderDirection, paging, page, maxCount, type);
    }

    @Override
    public Long countByUser(User user, String statuses) {
        return tradeDao.countByUser(user, statuses);
    }
}
