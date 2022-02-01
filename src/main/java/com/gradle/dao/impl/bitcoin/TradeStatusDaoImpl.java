/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.TradeStatusDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.advertisement.TradeStatus;
import org.springframework.stereotype.Repository;

@Repository("tradeStatusDao")
public class TradeStatusDaoImpl extends GenericDaoImpl<TradeStatus, Integer> implements TradeStatusDao {

    /**
     * Gets trade status value from code
     * @param code
     * @return
     */
    @Override
    public TradeStatus getTradeStatus(int code) {
        String query = "from TradeStatus where status_code=?";
        Object[] params = new Object[1];
        params[0]=code;
        TradeStatus status = first(query, params);
        return status;
    }
}
