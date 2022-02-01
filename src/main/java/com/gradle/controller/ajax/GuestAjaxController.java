/*
 * Copyright (c) 24/4/18 4:10 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.ajax;

import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.Currency;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forms.AjaxCommonForm;
import com.gradle.entity.forms.CurrencyRateJsonForm;
import com.gradle.entity.forms.user.CountryForm;
import com.gradle.entity.forms.user.CurrencyForm;
import com.gradle.entity.forms.user.ZonesForm;
import com.gradle.entity.user.Countries;
import com.gradle.entity.user.User;
import com.gradle.entity.user.Zones;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.services.iface.user.CountriesService;
import com.gradle.services.iface.user.ZonesService;
import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This AjaxController can be accessed by any type of user.
 * Use this whenever you need to allow access of the method to all users including guest
 * Else
 * @see AjaxController
 */
@RequestMapping(value = "/beforeloginajax")
@Controller
public class GuestAjaxController extends AbstractBaseController {

    public static final Logger logger = Logger.getLogger(GuestAjaxController.class);

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private ZonesService zonesService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CountriesService countriesService;


    /**
     * Gets zones according to selected country
     * @param postForm
     * @param model
     * @return
     */
    @RequestMapping(value = "/zones", method = RequestMethod.POST)
    @ResponseBody
    public ZonesForm getZones(@RequestBody ZonesForm postForm, ModelMap model) {
        try {
            postForm.setStatusCode("error");
            String query = " from Zones where country_code=?";
            Object[] params = new Object[1];
            params[0] = postForm.getCountryCode();
            List<Zones> zones = zonesService.queryWithParameter(query, params);
            postForm.setZones(zones);
            postForm.setStatusCode("success");
            postForm.setStatusMessage(localeHelper.getApplicationPropertiesText("Success.currency.rate.api", null, "Success"));
            return postForm;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return postForm;
    }


    /**
     *
     * @param postForm - CurrencyForm
     * @param model
     * @return
     *
     * @see  com.gradle.entity.Currency
     */
    @RequestMapping(value = "/currencies", method = RequestMethod.POST)
    @ResponseBody
    public CurrencyForm getCurrencies(@RequestBody CurrencyForm postForm, ModelMap model) {
        try {
            postForm.setStatusCode("error");
            String query = " from Currency where country_code=?";
            Object[] params = new Object[1];
            params[0] = postForm.getCountryCode();
            List<Currency> currencies = currencyService.queryWithParameter(query, params);
            postForm.setCurrencies(currencies);
            postForm.setStatusCode("success");
            postForm.setStatusMessage(localeHelper.getApplicationPropertiesText("Success.currency.rate.api", null, "Success"));
            return postForm;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return postForm;
    }


    @RequestMapping(value = "/countries", method = RequestMethod.POST)
    @ResponseBody
    public CountryForm getCountries(@RequestBody CountryForm postForm, ModelMap model) {
        try {
            postForm.setStatusCode("error");
            List<Countries> countries = countriesService.findAll();
            postForm.setCountries(countries);
            postForm.setStatusCode("success");
            postForm.setStatusMessage(localeHelper.getApplicationPropertiesText("Success.currency.rate.api", null, "Success"));
            return postForm;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return postForm;
    }






    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }

    /**
     * Checks if username already exist in database
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/duplicateuser", method = RequestMethod.POST)
    @ResponseBody
    public AjaxCommonForm isUsernameDuplicate(@RequestBody User user, ModelMap model) {
        String username = user.getUsername();
        AjaxCommonForm ajaxCommonForm = new AjaxCommonForm();
        ajaxCommonForm.setMsg("Username : " + username + " is Valid");
        ajaxCommonForm.setSuccess(true);
        try {
            Object[] params = new Object[1];
            params[0] = username;
            String query = " from User where username=?";
            user = userService.first(query, params);
            if (user != null) {
                ajaxCommonForm.setSuccess(false);
                ajaxCommonForm.setMsg("Username : " + username + " already Exist");
            }

        } catch (Exception e) {
            logger.error("Error getting user from user name : " + username + " : " + e.getMessage());
            ajaxCommonForm.setSuccess(false);
            ajaxCommonForm.setMsg("Username : " + username + " already Exist");
        }
        return ajaxCommonForm;
    }

    /**
     * Checks if email id already exist in database
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/duplicateemail", method = RequestMethod.POST)
    @ResponseBody
    public AjaxCommonForm isEmailDuplicate(@RequestBody User user, ModelMap model) {
        String email = user.getEmail();
        AjaxCommonForm ajaxCommonForm = new AjaxCommonForm();
        ajaxCommonForm.setMsg("Email : " + email + " is Valid");
        ajaxCommonForm.setSuccess(true);
        try {
            Object[] params = new Object[1];
            params[0] = email;
            String query = " from User where email=?";
            user = userService.first(query, params);
            if (user != null) {
                ajaxCommonForm.setSuccess(false);
                ajaxCommonForm.setMsg("Email : " + email + " already Exist");
            }

        } catch (Exception e) {
            logger.error("Error getting user from user name : " + email + " : " + e.getMessage());
            ajaxCommonForm.setSuccess(false);
            ajaxCommonForm.setMsg("Email : " + email + " already Exist");
        }
        return ajaxCommonForm;
    }

}
