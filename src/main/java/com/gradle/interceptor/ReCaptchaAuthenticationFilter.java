/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.interceptor;

import com.gradle.entity.user.Role;
import com.gradle.entity.user.User;
import com.gradle.services.impl.CaptchaService;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.constants.ConstantProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This is Authentication filter class
 * Before Spring Security calls Authentication method our custom method will be called
 * Here we can have extra verifications like Captcha verification (We are doing it here :) )
 */

public class ReCaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private DaoAuthenticationProvider manager;

    private RememberMeServices rememberMeServices = new NullRememberMeServices();
    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();


    /**
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String googleCaptcha = request.getParameter("g-recaptcha-response");
        Set<GrantedAuthority> grantedAuthorities = null;
        UsernamePasswordAuthenticationToken token = null;
        String username = "";
        String password = "";
        int ret = 0;
        if (true) {
            //if (captchaService.processResponse(googleCaptcha, request) ) { // Check if captcha verification was success
            boolean resul = captchaService.processResponse(googleCaptcha, request);
            Object[] params = new Object[2];
            params[0] = obtainUsername(request);
            params[1] = obtainUsername(request);
            User user = userService.first(" from User where username=? or email=?", params);
            if (user != null) {
                username = user.getUsername();
                password = obtainPassword(request);
                if (!user.isEnabled() || user.isAccountDeleted()) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                    request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", null);
                    request.getSession().setAttribute("type", "status");
                    request.getSession().setAttribute("error", "yes");
                    throw new UsernameNotFoundException("Please input correct credentials");
                }
                grantedAuthorities = new HashSet<>();
                if (user.getGoogleAuthenticatorKey() != null) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(ConstantProperties.ROLE_GUEST));
                } else {
                    for (Role role : user.getRoles()) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
                    }
                }
                ret = 1;
            }
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", null);
            request.setAttribute("type", "captcha");
            request.setAttribute("error", "yes");
            throw new UsernameNotFoundException("Please input correct credentials");
        }
        if (ret != 1) {
            SecurityContextHolder.getContext().setAuthentication(null);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", null);
            request.setAttribute("error", "yes");
            throw new UsernameNotFoundException("Please input correct credentials");
        }


        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password, grantedAuthorities);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
        Authentication authenticationResult = manager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticationResult);


        return manager.authenticate(authentication);
        //return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * on successful authentication this will be called
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

//        super.successfulAuthentication(request,response,chain,authResult);

        /*if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        rememberMeServices.loginSuccess(request, response, authResult);*/

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }

        Object[] params = new Object[2];
        params[0] = obtainUsername(request);
        params[1] = obtainUsername(request);
        User user = userService.first(" from User where username=? or email=?", params);

        



        //successHandler.onAuthenticationSuccess(request, response, authResult);
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        if (user.getGoogleAuthenticatorKey() != null) {
            // Authentication was success so we will redirect to application home
            redirectStrategy.sendRedirect(request, response, "/code");
        } else {
            // Authentication was success so we will redirect to application home
            redirectStrategy.sendRedirect(request, response, "/home");
        }


    }

    /**
     * * on unsuccessful authentication this will be called
     *
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        //super.unsuccessfulAuthentication(request,response,failed);
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request, response, "/login?error=" + request.getSession().getAttribute("error") + "&type=" + request.getSession().getAttribute("type"));

        // Check the reason of failed authentication and redirect accordingly
        /*if (request.getAttribute("captcha") != null)
            redirectStrategy.sendRedirect(request, response, "/login?error=yes&type=captcha");
        else if (request.getAttribute("status") != null)
            redirectStrategy.sendRedirect(request, response, "/login?error=yes&type=captcha");
        else
            redirectStrategy.sendRedirect(request, response, "/login?error=yes&type=credential");*/


    }

}