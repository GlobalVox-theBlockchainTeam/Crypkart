/*
 * Copyright (c) 24/4/18 3:55 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.forms;

import com.gradle.entity.forms.base.BaseForm;

public class AjaxCommonForm extends BaseForm {

    private boolean success;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

