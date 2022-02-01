/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;


import com.gradle.services.iface.user.SecurityService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("secutiryService")
public class SecutiryServiceImpl implements SecurityService {
    /*@Autowired
    private DaoAuthenticationProvider authenticationManager;

    @Autowired
    private UserService userService;*/

    private static final Logger logger = Logger.getLogger(SecutiryServiceImpl.class);


    /*@Override
    public String findLoggedInUsername() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails)userDetails).getUsername();
        }

        return null;
    }*/


   /* @Override
    public void autologin(String username, String password) {
        UserDetails userDetails = userService.loadUserByUsername("test");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.debug(String.format("Auto login %s successfully!", username));
        }
    }*/

}
