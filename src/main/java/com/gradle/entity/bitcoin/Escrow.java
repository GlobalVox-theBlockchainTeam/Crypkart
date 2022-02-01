/*
 * Copyright (c) 5/4/18 5:00 PM Bitwise Ventures
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
@Table(name = "bitcoin_escrow")
public class Escrow extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trade_id", nullable = false, referencedColumnName = "id")
    private Trade trade;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false, referencedColumnName = "id")
    private User buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false, referencedColumnName = "id")
    private User seller;

    @Column(name = "escrowed_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String escrowAmount;

    @Column(name = "released_status", columnDefinition = "bit(1) default 0")
    @HtmlValidateConstraint(whiteListType = "none")
    private boolean released;


    @Column(name = "coinmart_commision", columnDefinition = "varchar(255) DEFAULT 0")
    private String commisionAmount;

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



    public String getEscrowAmount() {
        return escrowAmount;
    }

    public void setEscrowAmount(String escrowAmount) {
        this.escrowAmount = escrowAmount;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getCommisionAmount() {
        return commisionAmount;
    }

    public void setCommisionAmount(String commisionAmount) {
        this.commisionAmount = commisionAmount;
    }

    @Override
    public void preDelete() {
        this.createdAt = null;
    }
}
