/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.chat;

import com.gradle.dao.iface.chat.ChatDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.msg.ChatHistory;
import org.springframework.stereotype.Repository;

@Repository("chatDao")
public class ChatDaoImpl extends GenericDaoImpl<ChatHistory, Integer> implements ChatDao {

}
