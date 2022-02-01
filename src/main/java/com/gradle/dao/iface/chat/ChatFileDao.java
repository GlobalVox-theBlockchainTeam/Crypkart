/*
 * Copyright (c) 8/3/18 10:38 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.iface.chat;

import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.msg.ChatFiles;

import java.util.List;

public interface ChatFileDao extends GenericDao<ChatFiles, Integer> {
    public List<ChatFiles> getTradeUserFiles(int tradeId, int fromId, int toId, String orderField, String orderDirection) ;
}
