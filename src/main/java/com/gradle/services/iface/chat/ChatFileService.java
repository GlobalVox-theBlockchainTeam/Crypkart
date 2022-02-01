/*
 * Copyright (c) 8/3/18 10:45 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.chat;

import com.gradle.entity.msg.ChatFiles;
import com.gradle.services.iface.GenericService;

import java.util.List;

public interface ChatFileService extends GenericService<ChatFiles,Integer> {
    public List<ChatFiles> getTradeUserFiles(int tradeId, int fromId, int toId, String orderField, String orderDirection) ;
}
