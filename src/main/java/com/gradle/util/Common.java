/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.user.User;
import com.gradle.exception.handler.CoinmartException;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Few common methods which can be used through whole application
 * Not going to explain them :D
 */
public class Common {


    public static final Logger logger = Logger.getLogger(Common.class);
    public static final String DATE_PATTERN_SERVICE = "YYYY-MM-dd'T'HH:mm:ss.000";

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    public static ServletContext servletContext;

    public static String generateToken() {
        String token = UUID.randomUUID().toString();
        return token;
    }

    /**
     * Custom method to set page title
     *
     * @param title
     * @param model
     */
    public static void setPageTitle(String title, ModelMap model) {
        model.addAttribute("defaultPageTitle", title);
    }


    public static String getContextPath() {
        return servletContext.getContextPath();
    }

    /**
     * @param type - type of network you want to get parameter of. it can be test, main or reg.
     * @return
     */
    public static NetworkParameters getNetworkParameter(String type) {
        if (type.equalsIgnoreCase("test")) {
            return TestNet3Params.get();
        } else if (type.equalsIgnoreCase("prod")) {
            return MainNetParams.get();
        } else {
            return RegTestParams.get();
        }
    }


    /**
     * Convert price amount from string to amount
     * removes all ","
     *
     * @param price
     * @return
     */
    public static String plainStringPrice(String price) {
        String formattedPrice = "";
        if (price != null && !price.equals("")) {
            formattedPrice = price.replaceAll(",", "");
        }
        return formattedPrice;
    }

    /**
     * Formate numbers in 2 decimal
     *
     * @param number
     * @return
     */
    public static String formatPrice(String number) {
        try {
            number = Common.plainStringPrice(number);
            double amount = Double.parseDouble(number);
            DecimalFormat formatter = new DecimalFormat("#,##0");
            return formatter.format(amount);
        } catch (NumberFormatException e) {
            logger.error("Number formate exception for number : " + number + " : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error formatting number " + number + " : " + e.getMessage());
        }
        return "";
    }





    /**
     * Formate bitcoin Amount in 8 decimal points
     *
     * @param number
     * @return
     */
    public static String formateBitcoinAmount(String number) {
        try {
            double amount = Double.parseDouble(number);
            DecimalFormat formatter = new DecimalFormat("#,##0.00000000");
            return formatter.format(amount);
        } catch (NumberFormatException e) {
            logger.error("Number formate exception for number : " + number + " : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error formatting number " + number + " : " + e.getMessage());
        }
        return "";
    }

    /**
     * Check content type of uploaded file to verify correct file type
     *
     * @param contentType
     * @param ALLOWED_FILE_TYPES
     * @return
     */
    public static Boolean isValidContentType(String contentType, String[] ALLOWED_FILE_TYPES) {
        if (!Arrays.asList(ALLOWED_FILE_TYPES).contains(contentType)) {
            return false;
        }
        return true;
    }


    /*public static Boolean isValidTradeAmount(User user, User advertiser, Trade trade, ServiceUtil serviceUtil) {

        try {
            Double finalBitcoinAmount = Double.valueOf(Common.plainStringPrice(advertiser.getFinalBitcoinAmount())).doubleValue();
            Double rate = Double.valueOf(Common.plainStringPrice(trade.getAdvertise().getBtcRate())).doubleValue();
            Double maxAllowedAmount = (finalBitcoinAmount * rate);
            Double tradeAmount = Double.valueOf(Common.plainStringPrice(trade.getAmount())).doubleValue();
            if (maxAllowedAmount > tradeAmount) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }*/


    public static String getAdminConfigValue(List<AdminConfigValues> adminConfigValuesList, String property, String defaultValue) {
        if (adminConfigValuesList.stream().filter(o -> o.getName().equalsIgnoreCase(property)).findFirst().isPresent()) {
            AdminConfigValues adminConfigValue = adminConfigValuesList.stream().filter(o -> o.getName().equalsIgnoreCase(property)).findFirst().get();
            return adminConfigValue.getValue();
        }
        return defaultValue;
    }


    /**
     * Get total minutes after trade was created
     *
     * @param trade
     * @return
     */
    public static Integer getSpentMinutes(Trade trade) {
        Integer minutes = 0;
        try {
            LocalDateTime createdDate = trade.getBitcoinEscrowTime();
            LocalDateTime currentTime = new LocalDateTime();
            minutes = Minutes.minutesBetween(createdDate, currentTime).getMinutes();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return minutes;
    }

    /**
     * Check if seller is allowed to cancel the trade ?
     * In trade there is a field timeout. If that is over seller can cancel the trade
     *
     * @param trade
     * @return
     */
    public static boolean isSellerAllowedToCancel(Trade trade) {
        try {
            Integer spentMinutes = Common.getSpentMinutes(trade);
            Integer allowedTimeout = trade.getAdvertise().getTimeout();
            if (spentMinutes > allowedTimeout && trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_BITCOIN_ESCROWED) {
                return true;
            }
        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return false;
    }

    /**
     * Convert time in user specific timezone
     *
     * @param time
     * @param user
     * @return
     */
    public static String getTimeInUserSpecificZone(LocalDateTime time, User user) {
        try {
            DateTimeFormatter utcFormatter = DateTimeFormat.forPattern(DATE_PATTERN_SERVICE).withLocale(Locale.US).withZoneUTC();
            DateTimeZone userZone = DateTimeZone.forID(user.getZone().getName());
            DateTimeFormatter indianZoneFormatter = utcFormatter.withZone(userZone);
            //String utcText = "2015-08-23 10:34:40";
            String utcText = time.toString();
            DateTime parsed = utcFormatter.parseDateTime(utcText);
            String date = indianZoneFormatter.print(parsed);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MMMM dd, yyyy HH:mm:ss");
            DateTime userSpecificDate = new DateTime(date);
// Printing the date
            return dtfOut.print(userSpecificDate);
        } catch (Exception e) {
            logger.equals(e.getMessage() + e.getStackTrace());
        }
        return "-";
    }

    /**
     * Check if provided string is Double value or not
     *
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static Double strToDouble(String str){
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {

        }
        return 0d;
    }


    /**
     * Check if bit coin wallet address is in right format or not
     *
     * @param address
     * @param params
     * @return
     */
    public static boolean isValidBitcoinAddress(String address, NetworkParameters params) {
        try {
            Address address1 = Address.fromBase58(params, address);
            return true;
        } catch (AddressFormatException e) {
            return false;
        }
    }


    /**
     * Generate OPT for specific length
     *
     * @param len
     * @return
     */
    public static String OTP(int len) {
        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return String.copyValueOf(otp);
    }


    public static boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }

}