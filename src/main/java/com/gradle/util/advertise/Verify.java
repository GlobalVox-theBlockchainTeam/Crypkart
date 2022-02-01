/*
 * Copyright (c) 16/3/18 4:01 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util.advertise;

import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.AdvertisementService;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.util.Alerts;
import com.gradle.util.Common;
import com.gradle.util.ServiceUtil;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Verification helper for Advertisement and Trades
 * Like if user is allowed to create advertisement or trade
 */
public class Verify {

    public static final Logger logger = Logger.getLogger(Verify.class);
    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private Alerts alerts;


    /**
     * checks if user can trade ?
     *
     * @param advertise
     * @param user
     * @return
     */
    public boolean canTrade(Advertise advertise, User user) {


            return (((advertise.isIdentifiedPeopleOnly() == (user.isEnabled() && advertise.isIdentifiedPeopleOnly()))) &&
                    ((advertise.isSmsVerificationRequired() == (user.isPhoneVerified() && advertise.isSmsVerificationRequired()))) &&
                    ((advertise.isTrustedPeopleOnly() == (user.isTrusted() && advertise.isTrustedPeopleOnly())))) && (advertise.isStatus()) && (!advertise.isHidden())
                    &&
                    (!advertise.getUser().isAccountDeleted())
                    &&
                    (!user.isAccountDeleted())
                    &&
                    (sellingOrBuyingVacationCheck(advertise, user));

    }


    /**
     * Checks if user can create Advertisement ?
     * @param user
     * @return
     */
    public boolean canCreateAdvertise(User user) {
        if (user.getParent() == null) {

            if (this.getMaxAllowedAdvertisement(user) > this.getLiveAdvertisementCount(user)) {
                return true;
            }
            alerts.setError("Advertise.new.user.allowed.number");
        } else {
            alerts.setError("Advertise.create.invalid.role");
        }
        return false;

    }

    /**
     * Gets allowed max Advertisement creation for user
     * First it will check database for this configuration
     * else will get static configuration from
     * @see ConstantProperties
     * @param user
     * @return
     */
    public Integer getMaxAllowedAdvertisement(User user) {
        try {
            AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
            List<AdminConfigValues> adminConfigValuesList = adminConfigUtil.getAdminConfigValues(serviceUtil, new Advertise());
            String value = Common.getAdminConfigValue(adminConfigValuesList, ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT, ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT_STATIC.toString());
            Integer maxAllowedAdvertise = Integer.parseInt(value);
            return maxAllowedAdvertise;
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return 0;
    }

    /**
     * Get count of enabled advertisement for current user
     * @param user
     * @return
     */
    public Long getLiveAdvertisementCount(User user) {
        try {
            if (user.getParent() != null)
                return advertisementService.countEnabledAdvertisement(user.getParent());
            else
                return advertisementService.countEnabledAdvertisement(user);
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return 0l;
    }

    public boolean sellingOrBuyingVacationCheck(Advertise advertise, User user) {
        return ((advertise.getAdType().getValue().equalsIgnoreCase("Buy") && !advertise.getUser().isBuyingVacation()) ||
                (advertise.getAdType().getValue().equalsIgnoreCase("Sell") && !advertise.getUser().isSellingVacation()));
    }

    public boolean isTradeAmountAllowed(Trade trade){
        try{
            if (trade.getAdvertise().getAdType().getValue().equalsIgnoreCase("sell")){
                return true;
            }
            Double availableBtc = serviceUtil.getCurrentUserBalance();
            Double maxLimit = Double.parseDouble(Common.plainStringPrice(trade.getAdvertise().getMaxLimit()));
            Double tradeAmount = Double.parseDouble(Common.plainStringPrice(trade.getAmount()));
            Double advertiseBtcRate = Double.parseDouble(Common.plainStringPrice(trade.getAdvertise().getBtcRate()));
            Double commision = serviceUtil.getCommisionAmount(tradeAmount);
            Double availableBtcInAmount = availableBtc * advertiseBtcRate;
            Double finalAmountWithCommision = availableBtcInAmount - commision;
            if (finalAmountWithCommision >= tradeAmount)
                return true;
        }catch (Exception e){

        }
        return false;
    }

}
