/*
 * Copyright (c) 11/4/18 10:00 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.bitcoin;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.HtmlValidateConstraint;

import javax.persistence.*;


@Entity
@Table(name = "bitcoin_internal_transfer")
public class InternalTransfer extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false, referencedColumnName = "id")
    private User buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false, referencedColumnName = "id")
    private User seller;

    @Column(name = "amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String bitcoinAmount;

    @Column(name = "remarks")
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBitcoinAmount() {
        return bitcoinAmount;
    }

    public void setBitcoinAmount(String bitcoinAmount) {
        this.bitcoinAmount = bitcoinAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
