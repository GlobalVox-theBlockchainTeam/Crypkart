/*
 * Copyright (c) 24/4/18 5:38 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.user;

import com.gradle.entity.user.PasswordResetToken;
import com.gradle.services.iface.GenericService;

public interface PasswordResetService extends GenericService<PasswordResetToken, Integer> {
}
