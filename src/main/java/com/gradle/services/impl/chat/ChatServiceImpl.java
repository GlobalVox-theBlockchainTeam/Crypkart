/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.chat;

import com.gradle.dao.iface.chat.ChatDao;
import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.services.iface.chat.ChatService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("chatService")
@Transactional
public class ChatServiceImpl extends GenericServiceImpl<ChatHistory, Integer> implements ChatService {

    @Autowired
    private ChatDao chatDao;
    public ChatServiceImpl() {

    }

    @Autowired
    public ChatServiceImpl(@Qualifier("chatDao") GenericDao<ChatHistory, Integer> genericDao) {
        super(genericDao);
        this.chatDao = (ChatDao) genericDao;
    }
}
