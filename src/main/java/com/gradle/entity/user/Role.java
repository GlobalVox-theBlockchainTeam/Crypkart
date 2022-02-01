/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.user;

import com.gradle.entity.base.BaseModel;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import java.util.Set;
/**
 * Role Entity
 * For more details check (roles table in database)
 */

@Table(name = "roles")
@Entity
public class Role extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int id;

    @Column(name = "role_name")
    @HtmlValidateConstraint(whiteListType = "none")
    private String role;

    @ManyToMany(mappedBy = "roles",fetch = FetchType.EAGER)
    private Set<User> users;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String roleName) {
        this.role = roleName;
    }


    @Override
    public boolean equals(Object o){
        if(o instanceof Role){
            Role toCompare = (Role) o;
            return (id==toCompare.id);//return this.id.equals(toCompare.id);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return 1;
    }


}
