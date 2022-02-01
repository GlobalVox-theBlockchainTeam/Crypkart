/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.chat;

import com.gradle.dao.iface.chat.ChatFileDao;
import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.msg.ChatFiles;
import com.gradle.services.iface.chat.ChatFileService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("chatFileService")
@Transactional
public class ChatFileServiceImpl extends GenericServiceImpl<ChatFiles, Integer> implements ChatFileService {

    @Autowired
    private ChatFileDao chatFileDao;

    public ChatFileServiceImpl() {

    }

    @Autowired
    public ChatFileServiceImpl(@Qualifier("chatFileDao") GenericDao<ChatFiles, Integer> genericDao) {
        super(genericDao);
        this.chatFileDao = (ChatFileDao) genericDao;
    }

    public List<ChatFiles> getTradeUserFiles(int tradeId, int fromId, int toId, String orderField, String orderDirection) {
        return this.chatFileDao.getTradeUserFiles(tradeId, fromId, toId, orderField, orderDirection);
    }
}
