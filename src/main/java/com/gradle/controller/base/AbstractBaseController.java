/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.base;

import com.gradle.components.encrypter.PathVariableEncrypt;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.services.iface.admin.config.AdminConfigService;
import com.gradle.services.iface.admin.config.AdminConfigValuesService;
import com.gradle.services.iface.bitcoin.PaymentTypeService;
import com.gradle.services.iface.user.RoleService;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.iface.user.UserWalletService;
import com.gradle.services.iface.user.VerificationService;
import com.gradle.services.mail.EmailService;
import com.gradle.util.Alerts;
import com.gradle.util.Common;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import org.apache.log4j.Logger;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This is an Abstract base controller which can not be initialized
 * In any controller we will extend this as it will help getting common services and methods which already autowired in this class
 */
public abstract  class AbstractBaseController extends AbstractController {

    protected final NetworkParameters netParams = Common.getNetworkParameter("test");
    protected final NetworkParameters regNetParams = Common.getNetworkParameter("reg");
    protected final NetworkParameters prodNetParams = Common.getNetworkParameter("prod");

    public final static int recordPerPage = 10;

    public final static Logger logger = Logger.getLogger(AbstractBaseController.class);

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }


    @Autowired
    protected PaymentTypeService paymentTypeService;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    @Autowired
    protected UserService userService;

    @Autowired
    protected LocaleHelper localeHelper;

    @Autowired
    protected RoleService roleService;


    @Autowired
    protected EmailService emailService;


    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    protected VerificationService verificationService;

    @Autowired
    protected ServiceUtil serviceUtil;

    @Autowired
    protected UserWalletService userWalletService;

    @Autowired
    protected Alerts alerts;

    @Autowired
    protected AdminConfigService adminConfigService;

    @Autowired
    protected AdminConfigValuesService adminConfigValuesService;

    @Autowired
    protected PathVariableEncrypt pathVariableEncrypt;

    @Autowired
    protected SimpMessagingTemplate webSocket;

    public abstract AdminConfig getAdminConfig();


    /**
     *
     * @param request
     * @param ex
     * @return
     * @implNote This is common exception handler overridden method which will be called whenever there will be any kind of Exception
     * and will display custom created error page instead of error trace on frontend for better user experience
     */
    /*@ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex){
        logger.error(localeHelper.getControllerName() + " : Entry");
        logger.error(localeHelper.getMethodName() + " : Entry");
        logger.error("Requested URL="+request.getRequestURL());
        logger.error("Unexpected error : " ,ex);
        logger.error(localeHelper.getMethodName() + "  : Exit");
        logger.error(localeHelper.getControllerName() + "  : Exit");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.setViewName("error");
        return modelAndView;
    }*/



}
