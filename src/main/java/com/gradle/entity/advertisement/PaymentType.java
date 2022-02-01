/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.advertisement;


import com.gradle.entity.base.BaseModel;
import com.gradle.validator.iface.HtmlValidateConstraint;
import com.gradle.validator.iface.PaymentTypeConstraint;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * PaymentType Entity
 * For more details check (payment_type table in database)
 */

@Entity
@Table(name = "payment_type")
@PaymentTypeConstraint
public class PaymentType extends BaseModel{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    @NotEmpty
    @Column(name = "payment_type_name" , unique = true)
    @HtmlValidateConstraint(whiteListType = "none")
    private String paymentTypeName;

    @Column(name = "search_count")
    private long searchCount;

    @Type(type = "text")
    @Column(name = "payment_type_information")
    @HtmlValidateConstraint(whiteListType = "none")
    private String paymentInfo;


    /*@OneToMany(mappedBy = "paymentType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<Advertise> List = new ArrayList<Advertise>();*/
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }

    public long getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(long searchCount) {
        this.searchCount = searchCount;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    @Override
    @PrePersist
    public void preInsert() {
        this.status=true;
        this.createdAt = new LocalDateTime();
    }
}
