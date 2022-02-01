/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.user;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;


/**
 * UserRole Entity
 * For more details check (user_roles table in database)
 */
public class UserRole  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotEmpty
    @Column(name = "user_id", nullable = false)
    @ManyToMany
    private int userId;

    @NotEmpty
    @Column(name = "role_id", nullable = false)
    @ManyToMany
    private int roleId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
