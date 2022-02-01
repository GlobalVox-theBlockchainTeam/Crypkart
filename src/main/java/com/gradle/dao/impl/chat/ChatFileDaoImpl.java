/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.chat;

import com.gradle.dao.iface.chat.ChatFileDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.msg.ChatFiles;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("chatFileDao")
public class ChatFileDaoImpl extends GenericDaoImpl<ChatFiles, Integer> implements ChatFileDao{

    /**
     * Gets uploaded file while chatting for a trade
     * @param tradeId
     * @param fromId
     * @param toId
     * @param orderField
     * @param orderDirection
     * @return
     */
    @Override
    @Transactional
    public List<ChatFiles> getTradeUserFiles(int tradeId, int fromId, int toId, String orderField, String orderDirection) {

        try {

            orderField = (orderField != null && !orderField.equals("")) ? orderField : "id";
            orderDirection = (orderDirection != null && !orderDirection.equals("")) ? orderDirection : "ASC";

            Criteria criteria = getCurrentSession().createCriteria(daoType.getName());
            Criterion traderIdCriteria = Restrictions.eq("trade.id", tradeId);

            // below will create this condition (to_user_id=? and from_user_id=?)
            Criterion fromIdCriteria = Restrictions.eq("userFrom.id", fromId);
            Criterion toIdCriteria = Restrictions.eq("userTo.id", toId);
            LogicalExpression andExpression1 = Restrictions.and(fromIdCriteria, toIdCriteria);

            //below will generate (to_user_id=? and from_user_id=?)
            Criterion fromToIdCriteria = Restrictions.eq("userFrom.id", toId);
            Criterion toFromIdCriteria = Restrictions.eq("userTo.id", fromId);
            LogicalExpression andExpression2 = Restrictions.and(fromToIdCriteria, toFromIdCriteria);

            // below will generate ((to_user_id=? and from_user_id=?) or (to_user_id=? and from_user_id=?))
            LogicalExpression orExpression = Restrictions.or(andExpression1, andExpression2);
            LogicalExpression finalExpression = Restrictions.and(traderIdCriteria, orExpression);

            // full query
            // "from ChatFiles where trade_id=? and ((to_user_id=? and from_user_id=?) or (to_user_id=? and from_user_id=?))"
            criteria.add(finalExpression);

            // sorting data
            if (orderDirection.equals("ASC")) {
                criteria.addOrder(Order.asc(orderField));
            } else {
                criteria.addOrder(Order.desc(orderField));
            }

            // Unique rows
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            List<ChatFiles> chatFilesList = criteria.list();
            return chatFilesList;
        } catch (Exception e) {
           // logger.error(e.getMessage());

        }

        return null;
    }

}
