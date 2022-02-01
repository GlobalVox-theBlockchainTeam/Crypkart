/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.base.common;

import com.gradle.base.interfaces.Model;

import java.util.List;

public class GeneralSql<T> implements Model<T>{
    @Override
    public void save(T obj) {

    }

    @Override
    public void delete(T obj) {

    }

    @Override
    public void deleteAll(List<T> obj) {

    }

    @Override
    public List<T> findAll() {
        return null;
    }

    @Override
    public T findById(int id) {
        return null;
    }

    @Override
    public void update(T obj) {

    }
}
