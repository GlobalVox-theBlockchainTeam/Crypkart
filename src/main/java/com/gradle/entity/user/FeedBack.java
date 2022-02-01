/*
 * Copyright (c) 16/3/18 2:20 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.user;


import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.base.BaseModel;
import com.gradle.validator.iface.FeedBackConstraint;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "user_feedback")
@FeedBackConstraint
public class FeedBack extends BaseModel implements Cloneable, Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @NotNull
    @Column(name = "rating_star", nullable = false)
    private int star;

    @NotEmpty
    @Column(name = "comment", columnDefinition = "text")
    @HtmlValidateConstraint(whiteListType = "none")
    private String comment;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "to_user_id")
    private User userTo;

    @ManyToOne(targetEntity = Trade.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "trade_id")
    private Trade trade;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userFrom) {
        this.user = userFrom;
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
}
