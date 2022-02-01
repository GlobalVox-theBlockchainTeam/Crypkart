/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Constant values of application can be defined here
 */
public  class ConstantProperties {



    public static final String ROLE_GUEST="ROLE_GUEST";
    public static final String ROLE_USER="ROLE_USER";
    public static final String ROLE_ADMIN="ROLE_ADMIN";
    public static final String ROLE_SUB_USER="ROLE_SUB_USER";

    public static final String BASE_URL="http://127.0.0.1:8880";
    public static final String SESSION_BASE_URL="BASE_URL";

    /*Wallet front page menu*/
    public static final String[] WALLET_MENU_VALUES = new String[] { "a", "b" };
    public static final Set<String> WALLET_MENU = new LinkedHashSet<>(Arrays.asList(WALLET_MENU_VALUES));

    public static final int WALLET_HOME_MAX_DISPLAY_TRANSACTIONS = 5;

    public static final int WALLET_HOME_ALL_TRANSACTION =0;

    public static final String SESSION_USER_OBJECT = "SESSION_USER_OBJECT";

    public static final String USER_WALLET_FILE_PATH = "/mnt/sda3/www/workspace/coinmart_GIT/files/";

    public static final String GOOGLE_MAP_ADDRESS_API = "googleMapApi";

    public static final String BTC_CURRENT_MARKET_RATE_API_URL = "https://bitpay.com/api/rates";

    public static final int TRADE_STATUS_COMPLETED = 1;
    public static final int TRADE_STATUS_INPROCESS = 2;
    public static final int TRADE_STATUS_PAYMENT_SENT = 3;
    public static final int TRADE_STATUS_PAYMENT_RECEIVED = 4;
    public static final int TRADE_STATUS_BITCOIN_RELEASED = 5;
    public static final int TRADE_STATUS_BITCOIN_DEAD = 6;
    public static final int TRADE_STATUS_BITCOIN_ESCROWED = 7;
    public static final int TRADE_STATUS_CANCELLED = 8;


    public static final Long PAGING_MAX_PER_PAGE=5l;

    public static final String CHAT_FILE_UPLOAD_LOCATION="/mnt/sda3/www/workspace/coinmart_GIT/upload/";


    /**
     *  Admin configuration names for each entity
     */

    /*Advertisement*/

    public static final String NEW_USER_ALLOWED_ADVERTISEMENT="new_user_max_allowed";
    public static final Integer NEW_USER_ALLOWED_ADVERTISEMENT_STATIC=5;
    public static final Integer MIN_TIMEOUT=20;
    public static final String MIN_TIMEOUT_PROPERTY="MIN_TIMEOUT";
    public static final Integer DFAULT_TIMEOUT=120;


    /*Trade*/

    public static final String MIN_BITCOIN_AMOUNT="Min_Bitcoin_Amount";
    public static final Double MIN_BITCOIN_AMOUNT_DEFAULT=0.01;

    /* Zone */
    public static final String DEFAULT_ZONE_CONFIG_PROPERTY="Default_Zone";
    public static final String DEFAULT_ZONE_CONFIG_PROPERTY_VALUE="America/New_York";



    /* Websocket notification */
    public static final String WEB_NOTIFICATION_DESTINATION="/queue/notification";


    /**
     *
     */
    public static final Double DEFAULT_BTC_TRADE_RATE = 0.75;
    public static final String BTC_TRADE_RATE_CONFIG_STRING="BTC_COMMISSION_RATE";


    /**
     * Phone verification
     */

    public static final int PHONE_VERIFICATION_OTP_EXPIRY_MINUTES= 3;


    /**
     * Database Table names
     */

    /*public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config_values";
    public static final String TABLE_ADMIN_CONFIG = "advertisement_master";
    public static final String TABLE_ADMIN_CONFIG = "bitcoin_escrow";
    public static final String TABLE_ADMIN_CONFIG = "bitcoin_internal_transfer";
    public static final String TABLE_ADMIN_CONFIG = "bitcoin_released";
    public static final String TABLE_ADMIN_CONFIG = "bitcoin_transactions";
    public static final String TABLE_ADMIN_CONFIG = "chat_files";
    public static final String TABLE_ADMIN_CONFIG = "chat_history";
    public static final String TABLE_ADMIN_CONFIG = "cms_master";
    public static final String TABLE_ADMIN_CONFIG = "country";
    public static final String TABLE_ADMIN_CONFIG = "currency";
    public static final String TABLE_ADMIN_CONFIG = "direct_transactions";
    public static final String TABLE_ADMIN_CONFIG = "forum_post";
    public static final String TABLE_ADMIN_CONFIG = "forum_section";
    public static final String TABLE_ADMIN_CONFIG = "forum_topics";
    public static final String TABLE_ADMIN_CONFIG = "password_reset_token";
    public static final String TABLE_ADMIN_CONFIG = "payment_type";
    public static final String TABLE_ADMIN_CONFIG = "phone_verification";
    public static final String TABLE_ADMIN_CONFIG = "reported_user";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";
    public static final String TABLE_ADMIN_CONFIG = "admin_config";*/



    public static final String DEFAULT_CURRENCY = "USD";
    public static final String DEFAULT_CURRENCY_PROPERTY = "DEFAULT_CURRENCY";

    public static final String COOKIE_DEFAULT_CURRENCY="COOKIE_DEFAULT_CURRENCY";
    public static final String COOKIE_USER_LOCATION_COUNTRY_CODE="USER_LOCATION_COUNTRY_CODE";


    public static final String ADMIN_CONFIG_GENERAL_TABLE = "General";
}
