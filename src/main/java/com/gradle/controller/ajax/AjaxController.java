/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.ajax;

import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forms.AjaxCommonForm;
import com.gradle.entity.forms.CurrencyRateJsonForm;
import com.gradle.entity.forms.user.ZonesForm;
import com.gradle.entity.user.User;
import com.gradle.entity.user.Zones;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.services.iface.user.ZonesService;
import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RequestMapping(value = "/ajax")
@Controller
public class AjaxController extends AbstractBaseController {

    public static final Logger logger = Logger.getLogger(AjaxController.class);

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    private ZonesService zonesService;

    @Autowired
    private TradeService tradeService;

    /**
     * @param postForm Postform obejct contains selected currecny
     * @param model
     * @return
     * @apiNote This method calls api which fetch current rate of btc in specific currency and dump data as json
     */
    @RequestMapping(value = "/currencyrate", method = RequestMethod.POST)
    @ResponseBody
    public CurrencyRateJsonForm getCurrencyRate(@RequestBody CurrencyRateJsonForm postForm, ModelMap model) {
        CurrencyRateJsonForm form = new CurrencyRateJsonForm();
        form.setStatusCode("error");
        form.setStatusMessage(localeHelper.getApplicationPropertiesText("Error.currency.rate.api", null, "Error getting current rate"));
        try {
            final URI url = new URI(ConstantProperties.BTC_CURRENT_MARKET_RATE_API_URL);
            ResponseEntity<CurrencyRateJsonForm[]> response = restTemplate.getForEntity(url, CurrencyRateJsonForm[].class);
            List<CurrencyRateJsonForm> list = Arrays.asList(response.getBody());
            List<CurrencyRateJsonForm> found = list.stream().filter(u -> u.getCode().equalsIgnoreCase(postForm.getCode())).collect(Collectors.toList());
            if (found.size() > 0) {
                CurrencyRateJsonForm finalRate = found.get(0);
                /*double rate = Double.parseDouble( finalRate.getRate());*/
                finalRate.setStatusCode("success");
                finalRate.setStatusMessage(localeHelper.getApplicationPropertiesText("Success.currency.rate.api", null, "Success"));
                Advertise advertiser = new Advertise();
                advertiser.setUser(serviceUtil.getCurrentUser());
                advertiser.setBtcRate(finalRate.getRate());
                advertiser.setMaxLimit(finalRate.getRate());
                finalRate.setMaxRate(serviceUtil.getAdvertiseMaxLimit(advertiser).toString());
                /*DecimalFormat format = new DecimalFormat("0.00");
                finalRate.setRate(format.format(rate).toString());*/
                return finalRate;
            }
        } catch (URISyntaxException e) {
            logger.error("REST API call exception in AjaxController.getCurrencyRate() : " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("REST API call exception in AjaxController.getCurrencyRate() ArrayList out of bound exception : " + e.getMessage());
        } catch (Exception e) {
            logger.error("REST API call exception in AjaxController.getCurrencyRate() exception : " + e.getMessage());
        }
        return form;
    }

    /**
     * This method get zones according to selected country
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

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }


    @PostMapping(value = "/getTradeStatusButtons/{type}")
    public String refreshTradeStatus(@PathVariable String type, ModelMap model){
        boolean error =true;
        try{
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            if (id!=null && id > 0){
                Trade trade = tradeService.find(id);
                User currentUser = serviceUtil.getCurrentUser();
                int paymentReceiver = trade.getSeller().getId();
                int paymentSender = trade.getBuyer().getId();
                model.addAttribute("paymentSender", paymentSender);
                model.addAttribute("paymentReceiver", paymentReceiver);
                model.addAttribute("user", currentUser);
                model.addAttribute("currentUserId", currentUser.getId());
                model.addAttribute("trade", trade);
                error = false;
            }
        }catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        model.addAttribute("error", error);
        return "ajax/tradestatus";
    }

}
