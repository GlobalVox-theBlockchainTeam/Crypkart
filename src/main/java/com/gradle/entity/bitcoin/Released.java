/*
 * Copyright (c) 6/4/18 11:28 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.bitcoin;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;


@Entity
@Table(name = "bitcoin_released")
public class Released extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trade_id", nullable = false, referencedColumnName = "id")
    private Trade trade;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "released_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String escrowAmount;

    @Column(name = "currency_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String currencyAmount;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEscrowAmount() {
        return escrowAmount;
    }

    public void setEscrowAmount(String escrowAmount) {
        this.escrowAmount = escrowAmount;
    }
}
