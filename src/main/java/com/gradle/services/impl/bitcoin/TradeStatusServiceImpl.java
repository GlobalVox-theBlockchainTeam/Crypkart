/*
 * Copyright (c) 29/3/18 5:28 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.bitcoin.TradeStatusDao;
import com.gradle.entity.advertisement.TradeStatus;
import com.gradle.services.iface.bitcoin.TradeStatusService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("tradeStatusService")
@Transactional
public class TradeStatusServiceImpl extends GenericServiceImpl<TradeStatus, Integer> implements TradeStatusService {

    @Autowired
    private TradeStatusDao tradeStatusDao;



    public TradeStatusServiceImpl() {

    }

    @Autowired
    public TradeStatusServiceImpl(@Qualifier("tradeStatusDao") GenericDao<TradeStatus, Integer> genericDao) {
        super(genericDao);
        this.tradeStatusDao = (TradeStatusDao) genericDao;
    }

}
