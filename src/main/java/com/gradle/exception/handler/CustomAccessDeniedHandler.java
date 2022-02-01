/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.exception.handler;


import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    UserService userService;
    public static final Logger logger = Logger.getLogger(AccessDeniedHandler.class);

    /**
     * In case of unauthorized access from user this handler will be called and will redirect user on access denied page
     *
     * @param request
     * @param response
     * @param exc
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {

        String redirectPath = "/cms/denied";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Boolean isUserRole = false;
        Boolean isGuestRole = false;
        Boolean isAdmin = false;
        Boolean isChild = false;
        // User user = (User)auth.getPrincipal();
        if (auth != null) {
            // Log user name who tried to access protected content
            logger.warn("User: " + auth.getName() + " attempted to access the protected URL: " + request.getRequestURI());
            if (auth.getName() != null) {
                Collection<SimpleGrantedAuthority> oldAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
                for (SimpleGrantedAuthority authority : oldAuthorities) {
                    if (authority.getAuthority().equalsIgnoreCase("ROLE_GUEST")) {
                        isGuestRole = true;
                    }
                    if (authority.getAuthority().equalsIgnoreCase("ROLE_USER")) {
                        isUserRole = true;
                    }
                    if (authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
                        isAdmin = true;
                    }
                    if (authority.getAuthority().equalsIgnoreCase("ROLE_SUB_USER")) {
                        isChild = true;
                    }

                }
            }
        } else {
            redirectPath = "/login";
        }
        if (!isAdmin && !isUserRole && !isChild) {
            redirectPath = "/code";
        }
        response.sendRedirect(request.getContextPath() + redirectPath);
    }
}
