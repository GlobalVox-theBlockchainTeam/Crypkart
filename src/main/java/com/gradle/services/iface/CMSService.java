/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface;

import com.gradle.entity.frontend.CMS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CMSService extends GenericService<CMS,Integer> {
    Page<CMS> findByCMS(Pageable pageable);

}
