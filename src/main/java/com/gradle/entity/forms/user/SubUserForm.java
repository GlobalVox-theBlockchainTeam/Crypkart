/*
 * Copyright (c) 26/4/18 5:36 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms.user;

import com.gradle.entity.forms.base.BaseForm;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;


public class SubUserForm extends BaseForm {

    @NotEmpty
    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "Only alphanumeric string allowed")
    private String username;

    @NotEmpty
    @Email
    private String email;

    private boolean enabled;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

