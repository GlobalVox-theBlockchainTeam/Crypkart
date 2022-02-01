/*
 * Copyright (c) 8/3/18 10:51 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.forum;

import com.gradle.dao.iface.forum.SectionDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.forum.Section;
import org.springframework.stereotype.Repository;

@Repository("sectionDao")
public class SectionDaoImpl extends GenericDaoImpl<Section, Integer> implements SectionDao {

}
