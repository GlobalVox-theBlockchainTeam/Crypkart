/*
 * Copyright (c) 5/3/18 5:55 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.advertisement;

import com.gradle.entity.base.BaseModel;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trade_status")
public class TradeStatus extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "status_code", columnDefinition = "int(11) default 2", nullable = false)
    private int statusCode;

    @Column(name = "status_label", nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String statusLabel;

    @OneToMany(mappedBy = "tradeStatus", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Trade> tradeList = new ArrayList<Trade>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}
