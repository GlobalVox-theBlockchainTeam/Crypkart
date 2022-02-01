/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller;


import com.gradle.components.jms.MessageSender;
import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.Currency;
import com.gradle.entity.Mail;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.forms.advertise.SearchForm;
import com.gradle.entity.frontend.CMS;
import com.gradle.entity.user.*;
import com.gradle.enums.advertisement.AdType;
import com.gradle.events.event.OnLoginSuccessEvent;
import com.gradle.events.event.OnRegistrationCompleteEvent;
import com.gradle.services.iface.CMSService;
import com.gradle.services.iface.bitcoin.AdvertisementService;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.services.iface.user.CountriesService;
import com.gradle.services.iface.user.ZonesService;
import com.gradle.services.mail.EmailService;
import com.gradle.util.ActiveSessionManager;
import com.gradle.util.Common;
import com.gradle.util.CountAdType;
import com.gradle.util.LocaleHelper;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.advertise.Verify;

import com.gradle.util.constants.ConstantProperties;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class HomeController extends AbstractBaseController {

    private static final Logger logger = Logger.getLogger(HomeController.class);

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    ActiveSessionManager activeSessionManager;

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private CMSService cmsService;

    @Autowired
    private ZonesService zonesService;

    @Autowired
    private EmailService mailService;

    @Autowired
    private CountriesService countriesService;


    @Autowired
    private MessageSender messageSender;

    @Autowired
    private AuthenticationManager authenticationManager;


    /*@GetMapping(value = "/email")
    public String email(ModelMap model) throws Exception {
        Mail mail = new Mail();
        mail.setFrom("jobs@bitwiseonline.com");
        mail.setTo("anand4686@gmail.com");
        mail.setContent("your registration successful");
        mail.setSubject("Registration complete");
        Map<String, Object> mdl = new HashMap<String, Object>();
        mdl.put("firstName", "Yashwant");
        mdl.put("lastName", "Chavan");
        mdl.put("location", "Pune");
        mdl.put("signature", "www.technicalkeeda.com");
        mdl.put("subject", mail.getSubject());
        mail.setModel(mdl);
        //mailService.sendMailWithInline(mail);
        model.addAttribute("msg", "email sent");

        String appUrl = "";
        // Send varification email
        User user = userService.find(4);
        //eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, LocaleHelper.getLocale(), appUrl));
        return "list";
    }*/


    /**
     * @param user               : New User object
     * @param model              : ModelMap
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/reg", method = RequestMethod.GET)
    public String registration(User user, ModelMap model, RedirectAttributes redirectAttributes) {

        if (!Common.isLoggedIn()) {

            /*List<Zones> zones = zonesService.findAll();*/
            List<Countries> countries = countriesService.findAll();

            model.addAttribute("registerCssClass", "active");
            /*model.addAttribute("zones", zones);*/
            /*model.addAttribute("countries", countries);
            model.addAttribute("currencies", currencyService.findAll());*/
            model.addAttribute("loadCountries", 0);

            /*String query = "update UserWallet set current_address=0 where user_id=?";
            Object[] params = new Object[1];
            params[0] = user.getId();
            userWalletService.executeQuery(query, params);*/

            return "user/register";
        }
        return "redirect:/home";
    }


    /**
     * @param user               :   New User object
     * @param model              :   ModelMap
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/user/verification/regenerate", method = RequestMethod.GET)
    public String regenerateTokenForm(User user, ModelMap model, RedirectAttributes redirectAttributes) {
        return "user/verification/regenerate";
    }


    /**
     * @param user               :   POST User object
     * @param result             :   Validation result
     * @param model              :   ModelMap
     * @param redirectAttributes :
     * @param request            :   HttpServletRequest
     * @param response           :   HttpServletResponse
     * @return
     */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationPost(@Valid @ModelAttribute("user") User user, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {


        if (Common.isLoggedIn()) {
            return "redirect:/home";
        }
        alerts.clearAlert();
        String ret = "user/register";

        // If validation error redirect back with error message
        if (result.hasErrors()) {
            alerts.setError("register.required.fields");
        } else if (user.getPassword() == null || user.getPassword().trim().equals("")) {
            result.rejectValue("password", "", "Password is required");
        } else {

            // Encrypt password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Get Guest role id and set it to user
            Object[] params = new Object[1];
            params[0] = "ROLE_USER";
            Role role = roleService.first(" from Role where role_name=?", params);
            List<Role> roles = new ArrayList<Role>();
            roles.add(role);
            user.setRoles(roles);
            if (user.getZone() == null) {
                AdminConfigUtil<Zones> adminConfigUtil = new AdminConfigUtil<Zones>();
                List<AdminConfigValues> configValues = adminConfigUtil.getAdminConfigValues(serviceUtil, new Zones());
                String defaultZone = Common.getAdminConfigValue(configValues, ConstantProperties.DEFAULT_ZONE_CONFIG_PROPERTY, ConstantProperties.DEFAULT_ZONE_CONFIG_PROPERTY_VALUE);
                Object[] zoneParams = new Object[1];
                zoneParams[0] = defaultZone;
                Zones zone = zonesService.first("from Zones where zone_name=?", zoneParams);
                user.setZone(zone);
            }

            // Saving user
            userService.saveOrUpdate(user);
            alerts.setSuccess("registration.success");
            request.getSession().setAttribute("RegistrationMessage", localeHelper.getApplicationPropertiesText("registration.success", null, "Registration successful. Verification link has been sent to your email id"));

            try {
                UserWallet userWallet = new UserWallet();
                userWallet.setUid(user.getId());
                messageSender.sendMessage(userWallet);
            } catch (Exception e) {
                logger.error(e.getMessage() + e.getStackTrace());
            }


            ret = "redirect:/login";
            try {
                String appUrl = request.getContextPath();
                // Send varification email
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, LocaleHelper.getLocale(), appUrl));
            } catch (Exception me) {
                logger.error(me.getMessage());
                alerts.setError("registration.error.sending.email");

            }
        }
        List<Zones> zones = zonesService.findAll();
        List<Countries> countries = countriesService.findAll();

        model.addAttribute("registerCssClass", "active");
        model.addAttribute("zones", zones);
        model.addAttribute("countries", countries);
        model.addAttribute("currencies", currencyService.findAll());
        model.addAttribute("registerCssClass", "active");
        model.addAttribute("loadCountries", 1);
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return ret;
    }


    /**
     * @param user               : POST User object
     * @param result             : Validation result
     * @param model
     * @param redirectAttributes
     * @param request
     * @return
     */

    @RequestMapping(value = "/user/verification/regenerate/token", method = RequestMethod.POST)
    public String regenerateToken(@ModelAttribute("user") User user, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        alerts.clearAlert();
        if (user.getUsername().equals(null) || user.getUsername().isEmpty()) {
            alerts.setError("user.username.empty");
        } else {
            Object[] params = new Object[2];
            params[0] = user.getUsername();
            params[1] = user.getUsername();
            // get user from email id or username
            User findUser = userService.first(" from User where username=? or email=?", params);
            if (findUser != null) {
                String appUrl = request.getContextPath();

                // Resend verification email
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(findUser, LocaleHelper.getLocale(), appUrl));
                alerts.setSuccess("user.verificationtoken.regenerated");
            } else {
                alerts.setError("user.no.user");
            }
        }
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        return "user/verification/regenerate";
    }


    /**
     * This method is being called after login attempt by Spring security
     *
     * @param error              if there is an error while logging user in
     * @param type               type of error
     * @param logout             is user logged out
     * @param model
     * @param user               Current user
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "page", required = false) String page,
                        ModelMap model, User user, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String ret = (page != null) ? "user/postadlogin" : "user/login";
        alerts.clearAlert();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        // Check if there is an error while loggin in
        if (error != null) {
            if (type != null && type.equalsIgnoreCase("captcha")) { // Throw captcha error if captcha validation failed
                alerts.setError("Captch.required");
                model.addAttribute("captchaTokenError", "Captcha required");
            } else if (type != null && type.equalsIgnoreCase("status")) { // throw user status error if user is still not enabled
                alerts.setError("User.status.disabled");
            } else
                alerts.setError("Incorrect.login");
        }
        model.addAttribute("loginCssClass", "active");
        Common.setPageTitle("Login", model);
        Object registration = request.getSession().getAttribute("RegistrationMessage");
        if (registration != null) {
            alerts.setSuccess(registration.toString());
            request.getSession().removeAttribute("RegistrationMessage");
        }
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        //alerts.setAlertRedirectAttribute(redirectAttributes);
        return ret;
    }


    /**
     * @param user               : User object
     * @param result             : Validation result
     * @param request            :
     * @param model
     * @param token              : Generated token for user
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/user/registration/email/verification")
    public String confirmRegistration(@ModelAttribute("user") User user, BindingResult result, WebRequest request, ModelMap model, @RequestParam("token") String token, RedirectAttributes redirectAttributes, HttpServletRequest servletRequest) {
        alerts.clearAlert();
        String ret = "user/verification/regenerate";
        Locale locale = request.getLocale();

        Object[] params = new Object[1];
        params[0] = token;

        // get verification token details from table
        VerificationToken verificationToken = verificationService.first("from VerificationToken where token=?", params);
        if (verificationToken == null) { // if null redirect back
            alerts.setError("Can not verify token. You may generate new by entering your username/email");
        } else {


            // Get user from verification token
            User verificationTokenUser = verificationToken.getUser();
            Calendar cal = Calendar.getInstance();

            // check if token is still valid
            // if not redirect back and ask to generate token again
            if ((verificationToken.getExpiryDate().toDate().getTime() - cal.getTime().getTime()) <= 0) {
                alerts.setError("Verification token expired");
            } else {

                // if verification successful set user status to enabled
                verificationTokenUser.setEnabled(true);
                userService.saveOrUpdate(verificationTokenUser);
                alerts.setSuccess("Verification successful. You are automatically logged in");
                /*User modelUser = new User();
                model.addAttribute("user", modelUser);*/
                model.addAttribute("loginCssClass", "active");
                authWithAuthManager(servletRequest, verificationToken.getUser());
                ret = "redirect:/home";
            }
        }
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return ret;
    }


    public void authWithAuthManager(HttpServletRequest request, User user) {
        String password = user.getPassword();
        try {
            String otp = Common.OTP(8);
            String encPassword = passwordEncoder.encode(otp);
            user.setPassword(encPassword);
            List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
            for (Role role : user.getRoles()) {
                updatedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
            }
            userService.saveOrUpdate(user);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(), otp, updatedAuthorities);
            user.setPassword(password);

            //authToken.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(authToken);
            userService.saveOrUpdate(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        } finally {
            try {
                user.setPassword(password);
                userService.saveOrUpdate(user);
            } catch (Exception e) {

            }
        }
    }


    /**
     * This method will be called when there is access denied exception for user
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/accessDenied")
    public String accessDenied(ModelMap model) {
        return "denied";
    }


    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String testCheckbox(ModelMap model, HttpServletRequest request) {
        String[] banks = request.getParameterValues("bank");
        Map bank = request.getParameterMap();
        return null;
    }


    /**
     * Home page of application
     *
     * @param user
     * @param model
     * @param result
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String defaultPage(User user, ModelMap model, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {


        /*UserWallet userWallet = new UserWallet();
        userWallet.setUid(serviceUtil.getCurrentUser().getId());
        messageSender.sendMessage(userWallet);*/



        Currency currentCurrency = serviceUtil.getCurrentCurrency(request);
        // Getting most used payment types
        List<PaymentType> paymentTypes = paymentTypeService.getPaymentTypes(7);
        //String res = restTemplate.getForObject("http://localhost/localbtc/deletecolumn.php",String.class);
        //String url = "http://api.msg91.com/api/sendhttp.php?authkey=112278Auw1fQ4YH3g572c36e2&mobiles=9687048668&message=This+is+your+OTP&route=4&country=91&encrypt=&sender=COINMT";
        //String res = restTemplate.getForObject(url, String.class);

        serviceUtil.getCurrentCurrency(request);
        model.addAttribute("title1", "Coinmart Home");
        model.addAttribute("currencies", serviceUtil.getCurrencies());


        List<Advertise> advertiseList;
        List<Advertise> advertisementBuyList;
        User currentUser = serviceUtil.getCurrentUser();

        // getting all advertisement for local sell
        if (currentUser != null) {
            advertisementBuyList = serviceUtil.getPaginatedAdvertiseByType(AdType.BUY.toString(), 0, 1, recordPerPage, currentCurrency.getId());
            advertiseList = serviceUtil.getPaginatedAdvertiseByType(AdType.SELL.toString(), 0, 1, recordPerPage, currentCurrency.getId());
        } else {
            Object[] params = new Object[1];
            List<AdminConfigValues> adminConfigValuesList = serviceUtil.getAdminConfigValues("General");
            params[0] = currentCurrency.getCurrencyCode();
            Currency usaCurrencyId = currencyService.first(" from Currency where currencyCode=?", params);
            advertiseList = serviceUtil.getPaginatedAdvertiseByType(AdType.SELL.toString(), 0, 1, recordPerPage, currentCurrency.getId());
            advertisementBuyList = serviceUtil.getPaginatedAdvertiseByType(AdType.BUY.toString(), 0, 1, recordPerPage, currentCurrency.getId());
        }
        // getting all advertisement for local sell
        //List<Advertise> advertiseList = serviceUtil.getAdvertiseByType(AdType.SELL.toString(), 0);

        advertiseList.sort(Advertise.AdvertiseAscComparator);
        // getting all advertisement for local buy
        //List<Advertise> advertisementBuyList = serviceUtil.getAdvertiseByType(AdType.BUY.toString(), 0);
        advertisementBuyList.sort(Advertise.AdvertiseDescComparator);
        // getting all payment types
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();



        List<Advertise> advertiseListCountBuy = advertisementService.countByPaymentType("paymentType", AdType.BUY, "currency", currentCurrency);
        List<Advertise> advertiseListCountSell = advertisementService.countByPaymentType("paymentType", AdType.SELL, "currency", currentCurrency);

        // getting all currencies
        List<Currency> currencyList = currencyService.findAll();

        // setting everything to model

        model.addAttribute("currencyList", currencyList);
        model.addAttribute("paymentTypeList", paymentTypeList);
        model.addAttribute("advertisementList", advertiseList);
        model.addAttribute("advertisementBuyList", advertisementBuyList);
        model.addAttribute("searchPaymentTypes", paymentTypes);
        model.addAttribute("user", user);
        model.addAttribute("verify", new Verify());
        model.addAttribute("ptypeId", 0);
        model.addAttribute("advertiseListCountBuy", advertiseListCountBuy);
        model.addAttribute("advertiseListCountSell", advertiseListCountSell);
        model.addAttribute("totalAdvertisement", advertiseList.size() + advertisementBuyList.size());
        model.addAttribute("searchForm", new SearchForm());
        return "home/home";
    }


    @GetMapping(value = "/gtpl1")
    @ResponseBody
    public String gtpl() {
        return "Worked";
    }

    @RequestMapping(value = "/ums/{id}")
    public String getPMSPage(@PathVariable String id, ModelMap model, RedirectAttributes redirectAttributes) {

        String query = " from CMS where pageId=?";
        Object[] params = new Object[1];
        params[0] = id;
        CMS cms = cmsService.first(query, params);
        model.addAttribute("cms", cms);
        model.addAttribute("title", localeHelper.getApplicationPropertiesText(cms.getPageTitleCode(), null, cms.getPageId()));
        return "home/cms";
    }

    /**
     * Home page of application
     *
     * @param user
     * @param model
     * @param result
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = {"/home"}, method = RequestMethod.GET)
    public String home(User user, ModelMap model, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
/*
        UserWallet userWallet1 = new UserWallet();
        userWallet1.setUid(serviceUtil.getCurrentUser().getId());
        messageSender.sendMessage(userWallet1);*/

        Currency currentCurrency = serviceUtil.getCurrentCurrency(request);
        UserWallet userWallet1 = new UserWallet();
        userWallet1.setUid(serviceUtil.getCurrentUser().getId());
        messageSender.sendMessage(userWallet1);

        String walletAddress = "NA";
        user = serviceUtil.getCurrentUser();
        UserWallet currentWallet = userWalletService.getCurrentUserWallet(serviceUtil.getCurrentUser());
        if (currentWallet != null) {
            walletAddress = currentWallet.getWalletAddress();
        } else {
            try {
                UserWallet userWallet = new UserWallet();
                userWallet.setUid(user.getId());
                messageSender.sendMessage(userWallet);
            } catch (Exception e) {
                walletAddress = "-";
            }
        }
        // Getting most used payment types
        List<PaymentType> paymentTypes = paymentTypeService.getPaymentTypes(7);
        //String res = restTemplate.getForObject("http://localhost/localbtc/deletecolumn.php",String.class);
        //String url = "http://api.msg91.com/api/sendhttp.php?authkey=112278Auw1fQ4YH3g572c36e2&mobiles=9687048668&message=This+is+your+OTP&route=4&country=91&encrypt=&sender=COINMT";
        //String res = restTemplate.getForObject(url, String.class);


        List<User> users = userService.findAll();
        model.addAttribute("title1", "Coinmart Home");
        model.addAttribute("users", users);
        model.addAttribute("currencies", serviceUtil.getCurrencies());

        List<Advertise> advertiseList;
        List<Advertise> advertisementBuyList;
        User currentUser = serviceUtil.getCurrentUser();
        // getting all advertisement for local sell
        /*if (currentUser != null) {
            advertisementBuyList = serviceUtil.getPaginatedAdvertiseByType("Buy", 0, 1, recordPerPage, currentUser.getCurrency().getId());
            advertiseList = serviceUtil.getPaginatedAdvertiseByType("Sell", 0, 1, recordPerPage, currentUser.getCurrency().getId());
        } else {
            advertiseList = serviceUtil.getAdvertiseByType(AdType.SELL.toString(), 0);
            advertisementBuyList = serviceUtil.getAdvertiseByType(AdType.BUY.toString(), 0);
        }*/
        if (currentUser != null) {
            advertisementBuyList = serviceUtil.getPaginatedAdvertiseByType(AdType.BUY.toString(), 0, 1, recordPerPage, currentCurrency.getId());
            advertiseList = serviceUtil.getPaginatedAdvertiseByType(AdType.SELL.toString(), 0, 1, recordPerPage, currentCurrency.getId());
        } else {
            Object[] params = new Object[1];
            List<AdminConfigValues> adminConfigValuesList = serviceUtil.getAdminConfigValues("General");
            params[0] = currentCurrency.getCurrencyCode();
            Currency usaCurrencyId = currencyService.first(" from Currency where currencyCode=?", params);
            advertiseList = serviceUtil.getPaginatedAdvertiseByType(AdType.SELL.toString(), 0, 1, recordPerPage, currentCurrency.getId());
            advertisementBuyList = serviceUtil.getPaginatedAdvertiseByType(AdType.BUY.toString(), 0, 1, recordPerPage, currentCurrency.getId());
        }


        advertiseList.sort(Advertise.AdvertiseAscComparator);
        // getting all advertisement for local buy

        advertisementBuyList.sort(Advertise.AdvertiseDescComparator);
        // getting all payment types
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        // getting all currencies
        List<Currency> currencyList = currencyService.findAll();


        if (user != null) {
            Set<String> activeUsers = activeSessionManager.getAllExceptCurrentUser(user.getUsername());
            model.addAttribute("activeUsers", activeUsers);
        }

        // setting everything to model

        if (localeHelper.isShowLastLogin()) {
            String fromIp = (user.getLastLoginIp() != null) ? " from IP : " + user.getLastLoginIp() : "";
            alerts.setSuccess("Last logged in time : " + Common.getTimeInUserSpecificZone(user.getLastLoginAt(), user) + fromIp);
            user.setLastLoginIp(request.getRemoteAddr());
            userService.saveOrUpdate(user);
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            localeHelper.setShowLastLogin(false);
        }


        List<Advertise> advertiseListCountBuy = advertisementService.countByPaymentType("paymentType", AdType.BUY, "currency", serviceUtil.getCurrentCurrency(request));
        List<Advertise> advertiseListCountSell = advertisementService.countByPaymentType("paymentType", AdType.SELL, "currency", serviceUtil.getCurrentCurrency(request));

        model.addAttribute("currencyList", currencyList);
        model.addAttribute("paymentTypeList", paymentTypeList);
        model.addAttribute("advertisementList", advertiseList);
        model.addAttribute("advertisementBuyList", advertisementBuyList);
        model.addAttribute("searchPaymentTypes", paymentTypes);
        model.addAttribute("advertiseListCountBuy", advertiseListCountBuy);
        model.addAttribute("advertiseListCountSell", advertiseListCountSell);
        model.addAttribute("user", user);
        model.addAttribute("verify", new Verify());
        model.addAttribute("ptypeId", 0);
        model.addAttribute("walletAddress", walletAddress);
        model.addAttribute("totalAdvertisement", advertiseList.size() + advertisementBuyList.size());


        model.addAttribute("searchForm", new SearchForm());
        return "home/home";
    }


    /**
     * @param user               : User object
     * @param result             : Validation result
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/save/user", method = RequestMethod.POST)
    public String save(@ModelAttribute("user") @Valid User user, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
        List<User> users = null;

        // Redirect back if validation error
        if (result.hasErrors()) {
            alerts.setError("Error.saving.user");

        } else {
            // update user if there is no validation error
            User updateUser = userService.find(user.getId());

            user.setCreatedAt(updateUser.getCreatedAt());
            user.setPassword(user.getPassword());
            userService.saveOrUpdate(user);
            alerts.setSuccess("User saved successfully");

        }
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/registration";
    }


    /**
     * CMS Page display
     */

/*    @RequestMapping(value = "/cms/{id}")
    public String getCMSPage(@PathVariable String id, ModelMap model, RedirectAttributes redirectAttributes) {

        String query = " from CMS where pageId=?";
        Object[] params = new Object[1];
        params[0] = id;
        CMS cms = cmsService.first(query, params);
        model.addAttribute("cms", cms);
        model.addAttribute("title", localeHelper.getApplicationPropertiesText(cms.getPageTitleCode(), null, cms.getPageId()));
        return "home/cms";
    }*/


    /**
     * @return Admin configuration
     */
    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }


    @GetMapping("/code")
    public String codeVerification(HttpServletRequest request, ModelMap model, RedirectAttributes redirectAttributes) {
        return "user/verification/code";
    }

    @PostMapping("/code")
    public String codeVerificationSubmit(HttpServletRequest request, ModelMap model, RedirectAttributes redirectAttributes) {

        User currentUser = serviceUtil.getCurrentUser();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        String googleAuthKey = currentUser.getGoogleAuthenticatorKey();
        try {
            if (currentUser.getParent() != null) {
                googleAuthKey = currentUser.getParent().getGoogleAuthenticatorKey();
            }
            if (true || ga.authorize(googleAuthKey, Integer.parseInt(request.getParameter("verification_code")))) {
                Collection<SimpleGrantedAuthority> oldAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ANOTHER");
                List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        /*updatedAuthorities.add(authority);
        updatedAuthorities.addAll(oldAuthorities);*/
                for (Role role : currentUser.getRoles()) {
                    updatedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
                }

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                                SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                                updatedAuthorities)
                );

                alerts.setSuccess("Verification successful");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                try {
                    String appUrl = request.getContextPath();


                    // Send varification email
                    eventPublisher.publishEvent(new OnLoginSuccessEvent(currentUser, LocaleHelper.getLocale(), appUrl));
                } catch (Exception me) {
                    logger.error(me.getMessage());
                    alerts.setError("registration.error.sending.email");

                }
                return "redirect:/home";
            } else {
                alerts.setError("Verification failed");
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
            alerts.setError("Verification failed");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
        }

        return "user/verification/code";
    }
}