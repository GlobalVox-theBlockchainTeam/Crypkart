/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.TradeDao;
import com.gradle.dao.iface.bitcoin.TradeStatusDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.advertisement.TradeStatus;
import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.ServiceUtil;
import com.gradle.util.constants.ConstantProperties;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("tradeDao")
public class TradeDaoImpl extends GenericDaoImpl<Trade, Integer> implements TradeDao {
    @Autowired
    private TradeStatusDao tradeStatusDao;

    @Autowired
    private UserService userService;

    @Override
    public TradeStatus getTradeStatus(int code) {
        TradeStatus status = tradeStatusDao.getTradeStatus(code);
        return status;
    }

    /**
     *
     * @param orderField Order field
     * @param orderDirection Order direction
     * @param paging required paging or not
     * @param page page no
     * @param maxCount record per page
     * @param type this is optional field which will be passed when need to get specific status's trades
     * @return
     */
    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount, String... type) {
        String listType="All";
        if (type.length>0){
            listType = type[0];
        }
        User user = getCurrentUser();
        Long firstRecord = (page * maxCount) - maxCount;

        orderField = (orderField != null && !orderField.equals("")) ? orderField : "id";
        orderDirection = (orderDirection != null && !orderDirection.equals("")) ? orderDirection : "ASC";
        Query query = null;
        if (listType.equalsIgnoreCase("All")) {
            query = getCurrentSession().createQuery(" from Trade where (user_id=? or advertiser_id=?) order by "
                    + orderField
                    + " "
                    + orderDirection);

        }else{
            int tradeStatusId = 1;
            if (listType.equalsIgnoreCase("Completed")){
                query = getCurrentSession().createQuery(" from Trade where (user_id=? or advertiser_id=?) and trade_status_id=1    order by "
                        + orderField
                        + " "
                        + orderDirection);
            }else if (listType.equalsIgnoreCase("process")){
                query = getCurrentSession().createQuery(" from Trade where (user_id=? or advertiser_id=?) and trade_status_id in (2,3,4,5,7)   order by "
                        + orderField
                        + " "
                        + orderDirection);
            }
            else {
                query = getCurrentSession().createQuery(" from Trade where (user_id=? or advertiser_id=?) and trade_status_id=8    order by "
                        + orderField
                        + " "
                        + orderDirection);
            }

        }
        query.setParameter(0, user.getId());
        query.setParameter(1, user.getId());
        query.setFirstResult(firstRecord.intValue());
        query.setMaxResults(maxCount);


        List<Trade> tradeList = query.list();
        return tradeList;
    }

    /**
     * Count of trades for specific user for specific status
     * @param user
     * @param statuses
     * @return
     */
    @Override
    public Long countByUser(User user, String... statuses) {

        Criteria criteria = getCurrentSession().createCriteria(daoType);
        Criterion userCriterion =  Restrictions.eq("user", user);
        Criterion traderCriterion =  Restrictions.eq("trader", user);
        Criterion status;
        if (statuses.length>0){
            Criterion or = Restrictions.or(userCriterion, traderCriterion);
            if (statuses[0].equalsIgnoreCase("completed")){
                TradeStatus tradeStatus = getTradeStatus(ConstantProperties.TRADE_STATUS_COMPLETED);
                status = Restrictions.eq("tradeStatus", tradeStatus);

            }else if (statuses[0].equalsIgnoreCase("process")){
                TradeStatus inProcess = getTradeStatus(ConstantProperties.TRADE_STATUS_INPROCESS);
                TradeStatus paymentSent = getTradeStatus(ConstantProperties.TRADE_STATUS_PAYMENT_SENT);
                TradeStatus paymentReceived = getTradeStatus(ConstantProperties.TRADE_STATUS_PAYMENT_RECEIVED);
                TradeStatus bitcoinEscrowed = getTradeStatus(ConstantProperties.TRADE_STATUS_BITCOIN_ESCROWED);
                TradeStatus bitcoinReleased = getTradeStatus(ConstantProperties.TRADE_STATUS_BITCOIN_RELEASED);
                Object[] tradeStatuses = new Object[5];
                tradeStatuses[0]=inProcess;
                tradeStatuses[1]=paymentSent;
                tradeStatuses[2]=paymentReceived;
                tradeStatuses[3]=bitcoinEscrowed;
                tradeStatuses[4]=bitcoinReleased;
                status = Restrictions.in("tradeStatus",tradeStatuses);


            }else{
                TradeStatus tradeStatus = getTradeStatus(ConstantProperties.TRADE_STATUS_CANCELLED);
                status = Restrictions.eq("tradeStatus", tradeStatus);

            }
            criteria.add(Restrictions.and(or,status));
        }else{
            criteria.add(Restrictions.or(userCriterion, traderCriterion));
        }

        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    /**
     * Count of trades for specific user
     * @param user
     * @return
     */
    @Override
    public Long countByUser(User user) {
        Criteria criteria = getCurrentSession().createCriteria(daoType);
        Criterion userCriterion =  Restrictions.eq("user", user);
        Criterion traderCriterion =  Restrictions.eq("trader", user);
        criteria.add(Restrictions.or(userCriterion, traderCriterion));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    /**
     * getting current user
     * @return
     */
    public User getCurrentUser() {
        User user = null;

        if (user == null) { // if this is called first time in current session get user and set to session
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                Object[] params = new Object[2];
                params[0] = currentUserName;
                params[1] = currentUserName;
                user = userService.first(" from User where username=? or email=?", params);
            }
        }
        // return current user
        return user;
    }

}
