/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.base.interfaces;

import java.util.List;

public interface Model<E> {

    void save(E user);

    void delete(E user);

    void deleteAll(List<E> list);

    List<E> findAll();

    E findById(int id);

    void update(E obj);
}
