/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.base;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;


/**
 * This is our base model and should be extended in each model
 * It will create status, created_at and updated_at fields in child class
 * It will also call prePersist method in which we will set created_at of updated_at values
 */
@MappedSuperclass
public class BaseModel implements BaseModelInterface {

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "created_at", updatable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    protected LocalDateTime createdAt;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "updated_at")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    protected LocalDateTime updatedAt;

    @Column(name = "status", columnDefinition = "bit(1) default false")
    protected boolean status;


    @Transient
    protected String visibleId;


    @Override
    @PrePersist
    public void preInsert() {

        this.createdAt = new LocalDateTime();
    }

    @Override
    @PreUpdate
    public void preUpdate() {

        this.updatedAt = new LocalDateTime();
    }

    @Override
    @PreRemove
    public void preDelete() {
        this.createdAt = null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getVisibleId() {
        return visibleId;
    }

    public void setVisibleId(String visibleId) {
        this.visibleId = visibleId;
    }



}
