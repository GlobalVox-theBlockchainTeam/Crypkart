/*
 * Copyright (c) 6/3/18 6:59 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.msg;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

@Entity
@Table(name = "chat_files")
public class ChatFiles extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "file_paht")
    private String path;

    @Column(name = "file_name")
    private String originalName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_user_id", nullable = false, referencedColumnName = "id")
    private User userFrom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_user_id", nullable = false, referencedColumnName = "id")
    private User userTo;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trade_id", nullable = false, referencedColumnName = "id")
    private Trade trade;

    @DateTimeFormat(pattern = "dd/MM/yyyy h:i:s")
    @Column(name = "updload_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime uploadTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}
