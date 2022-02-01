/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.editor;

import com.gradle.entity.user.Role;
import com.gradle.services.iface.user.RoleService;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

public class SampleEditor extends PropertyEditorSupport {




    private final RoleService roleService;

    public SampleEditor(RoleService roleService, Class collectionType) {
        super(collectionType);
        this.roleService= roleService;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        //Object obj = getValue();
        List<Role> list = new ArrayList<Role>();
        for (String str : text.split(",")) {
            list.add(this.roleService.find(Integer.parseInt(str)));
        }
        setValue((Object) list);
    }

    @Override
    public String getAsText() {
        return super.getAsText();
    }
}
