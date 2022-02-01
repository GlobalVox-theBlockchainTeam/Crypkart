/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;

import com.gradle.components.jms.MessageSender;
import com.gradle.entity.Currency;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.bitcoin.DirectTransaction;
import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.forms.bitcoin.ExternalTransferForm;
import com.gradle.entity.forms.bitcoin.InternalTransferForm;
import com.gradle.entity.forum.Topic;
import com.gradle.entity.msg.ChatFiles;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.PhoneVerification;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import com.gradle.exception.handler.CoinmartException;
import com.gradle.exception.handler.CoinmartNumberFormatException;
import com.gradle.services.iface.GenericService;
import com.gradle.services.iface.admin.config.AdminConfigService;
import com.gradle.services.iface.admin.config.AdminConfigValuesService;
import com.gradle.services.iface.bitcoin.*;
import com.gradle.services.iface.chat.ChatFileService;
import com.gradle.services.iface.chat.ChatService;
import com.gradle.services.iface.user.FeedBackService;
import com.gradle.services.iface.user.PhoneVerificationService;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.iface.user.UserWalletService;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.*;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;
import org.thymeleaf.util.ArrayUtils;

import javax.persistence.Table;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Application needs different kind of services throughout the application
 * This class's intention is to provide services when needed so other classes do not need to autowire them and use high memory
 */
@Component
@Scope("request")
public class ServiceUtil {

    public static final Logger logger = Logger.getLogger(ServiceUtil.class);

    @Autowired
    private Environment environment;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private EscrowService escrowService;

    @Autowired
    private DirectTransactionService directTransactionService;

    @Autowired
    private ChatFileService chatFileService;

    @Autowired
    private InternalTransferService internalTransferService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private RestOperations restTemplate;


    @Autowired
    private AdminConfigService adminConfigService;

    @Autowired
    private AdminConfigValuesService adminConfigValuesService;

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private PhoneVerificationService phoneVerificationService;

    @Autowired
    private MessageSender messageSender;

    public List<Currency> getCurrencies() {
        return currencyService.findAll();
    }


    private String userBalance;

    private String baseUrl = null;

    /**
     * Getting current user who is logged in
     * check if user has been set to session, if is already set then return that object else get new object and set it to session
     */
    @Transactional
    public User getCurrentUser() {
        try {
            //User user = (User) request.getSession().getAttribute(ConstantProperties.SESSION_USER_OBJECT);
            User user = null;

            if (user == null) { // if this is called first time in current session get user and set to session
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (!(authentication instanceof AnonymousAuthenticationToken)) {
                    String currentUserName = authentication.getName();
                    Object[] params = new Object[2];
                    params[0] = currentUserName;
                    params[1] = currentUserName;
                    User findUser = userService.first(" from User where username=? or email=?", params);
                    if (findUser != null) {
                        user = findUser;
                        request.getSession().setAttribute(ConstantProperties.SESSION_USER_OBJECT, user);
                    }
                }
            }
            // return current user
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return null;
    }


    /**
     * @param type          buy or sell
     * @param paymentTypeId Type of payment (Paypal, Netbanking etc..)
     * @return
     */
    public List<Advertise> getAdvertiseByType(String type, int paymentTypeId) {
        try {
            User currentUser = getCurrentUser();
            StringBuilder condition = new StringBuilder("");
            Object[] userParams = new Object[3];
            int paramsCount = 0;
            if (currentUser != null) {
                if (!currentUser.isPhoneVerified())
                    condition.append(" and sms_verification_required=0");
                if (!currentUser.isTrusted())
                    condition.append(" and trusted_people_only=0");
                if (!currentUser.isEnabled())
                    condition.append(" and trusted_people_only=0");
            }
            if (paymentTypeId != 0) {
                String query = "";
                if (currentUser != null) {
                    Object[] params = new Object[4];
                    params[0] = type;
                    params[1] = paymentTypeId;
                    params[2] = currentUser.getId();
                    params[3] = type;
                    query = " from Advertise where ( advertisement_type=? and payment_type_id=? " + condition.toString() + ") or (user_id=? and advertisement_type=?)";
                    return advertisementService.queryWithParameter(query, params);
                } else {
                    Object[] params = new Object[2];
                    params[0] = type;
                    params[1] = paymentTypeId;
                    query = " from Advertise where ( advertisement_type=? and payment_type_id=? " + condition.toString() + ")";
                    return advertisementService.queryWithParameter(query, params);
                }

            } else {
                if (currentUser != null) {
                    Object[] params = new Object[3];
                    params[0] = type;
                    params[1] = currentUser.getId();
                    params[2] = type;
                    String query = " from Advertise where  ( advertisement_type=? " + condition.toString() + ") or (user_id=? and advertisement_type=?)";
                    return advertisementService.queryWithParameter(query, params);
                } else {
                    Object[] params = new Object[1];
                    params[0] = type;
                    String query = " from Advertise where  ( advertisement_type=? " + condition.toString() + ")";
                    return advertisementService.queryWithParameter(query, params);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return null;
    }

    /**
     * @param type          buy or sell
     * @param paymentTypeId Type of payment (Paypal, Netbanking etc..)
     * @return
     */
    public List<Advertise> getPaginatedAdvertiseByType(String type, int paymentTypeId, int page, int maxCount, int currencyId) {
        try {
            List<Object> conditionParams = new ArrayList<>();
            String tempCondition = "";
            User currentUser = getCurrentUser();
            StringBuilder condition = new StringBuilder(" from Advertise where ( advertisement_type=? ");
            conditionParams.add(type);
            if (paymentTypeId != 0) {
                condition.append(" and payment_type_id=? ");
                conditionParams.add(paymentTypeId);
            }
            if (currencyId != 0) {
                condition.append(" and currency_id=? ");
                conditionParams.add(currencyId);
            }
            condition.append(") and ((1=1 ");
            if (currentUser != null) {
                if (!currentUser.isPhoneVerified())
                    condition.append(" and sms_verification_required=0");
                if (!currentUser.isTrusted())
                    condition.append(" and trusted_people_only=0");
                if (!currentUser.isEnabled())
                    condition.append(" and trusted_people_only=0");
            }
            condition.append(" and 1=1)");
            if (currentUser != null) {
                condition.append(" or ( user_id=? )");
                conditionParams.add(currentUser.getId());
            }
            condition.append(")");

            String query = condition.toString();
            Object[] params = conditionParams.toArray();
            return advertisementService.findPaginatedByType(query, params, page, maxCount);
            /*if (paymentTypeId != 0) {
                String query="";
                if (currentUser!=null) {
                    Object[] params = new Object[5];
                    params[0] = type;
                    params[1] = paymentTypeId;
                    params[2] = currentUser.getId();
                    params[3] = type;
                    params[4] = paymentTypeId;
                    query = " from Advertise where ( advertisement_type=? and payment_type_id=? " + condition.toString() + ") or (user_id=? and advertisement_type=? and payment_type_id=?)";
                    return advertisementService.findPaginatedByType(query, params, page, maxCount);
                }
                else {
                    Object[] params = new Object[2];
                    params[0] = type;
                    params[1] = paymentTypeId;
                    query = " from Advertise where ( advertisement_type=? and payment_type_id=? " + condition.toString() + ")";
                    return advertisementService.findPaginatedByType(query, params, page, maxCount);
                }

            } else {
                if (currentUser!=null) {
                    Object[] params = new Object[3];
                    params[0] = type;
                    params[1] = currentUser.getId();
                    params[2] = type;
                    String query = " from Advertise where  ( advertisement_type=? " + condition.toString() + ") or (user_id=? and advertisement_type=?)";
                    return advertisementService.findPaginatedByType(query, params, page, maxCount);
                }else{
                    Object[] params = new Object[1];
                    params[0] = type;
                    String query = " from Advertise where  ( advertisement_type=? " + condition.toString() + ")";
                    return advertisementService.findPaginatedByType(query, params, page, maxCount);
                }
            }*/

        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return null;
    }


    public ChatHistory getChatHistoryForTrade(int fromId, int toId, int tradeId) {
        ChatHistory chatMessage = null;
        try {
            Object[] primaryParams = new Object[5];
            primaryParams[0] = tradeId;
            primaryParams[1] = fromId;
            primaryParams[2] = toId;
            primaryParams[3] = toId;
            primaryParams[4] = fromId;
            String primaryQuery = " from ChatHistory where trade_id=? and ((to_user_id=? and from_user_id=?) or (to_user_id=? and from_user_id=?))";
            chatMessage = chatService.first(primaryQuery, primaryParams);
        } catch (Exception e) {
            logger.error("Error getting chat message from tradeID : " + tradeId + " : " + e.getMessage());
        }
        return chatMessage;
    }


    /**
     * Get User object from username or email
     *
     * @param username
     * @return
     */
    @Transactional
    public User getUserFromUsernameOrEmail(String username) {
        User user = null;
        try {
            Object[] params = new Object[2];
            params[0] = username;
            params[1] = username;
            String query = " from User where username=? or email=?";

            user = userService.first(query, params);
            Hibernate.initialize(user.getAdvertiseList());
            Hibernate.initialize(user.getTradeList());

        } catch (Exception e) {
            logger.error("Error getting user from user name : " + username + " : " + e.getMessage());
        }
        return user;
    }


    public List<Trade> getOutgoingTrades(String orderField, String orderDirection) {
        User user = getCurrentUser();

        // Creating criteria object from current session
        Criteria criteria = tradeService.getCriteria(new Trade());
        // compare user id
        criteria.add(Restrictions.eq("user.id", user.getId()));

        // Order by
        orderField = (orderField != null && !orderField.equals("")) ? orderField : "id";
        orderDirection = (orderDirection != null && !orderDirection.equals("")) ? orderDirection : "ASC";
        if (orderDirection.equals("ASC")) {
            criteria.addOrder(Order.asc(orderField));
        } else {
            criteria.addOrder(Order.desc(orderField));
        }
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Trade> tradeList = tradeService.getByCriteria(criteria);
        return tradeList;
    }

    public List<Trade> getUsersTrades(String orderField, String orderDirection, boolean paging, Long page, int maxCount) {
        try {
            User user = getCurrentUser();

            // Creating criteria object from current session
            Criteria criteria = tradeService.getCriteria(new Trade());
            if (paging) {
                Long firstRecord = (page * maxCount) - maxCount;
                criteria.setFirstResult(firstRecord.intValue());
                criteria.setMaxResults(maxCount);
            }
            // compare user id
            Criterion userCriteria = Restrictions.eq("user.id", user.getId());
            Criterion traderCriteria = Restrictions.eq("trader.id", user.getId());
        /*criteria.add(Restrictions.eq("user.id", user.getId()));
        criteria.add(Restrictions.eq("trader.id", user.getId()));
        */
            criteria.add(Restrictions.or(userCriteria, traderCriteria));

            // Order by
            orderField = (orderField != null && !orderField.equals("")) ? orderField : "id";
            orderDirection = (orderDirection != null && !orderDirection.equals("")) ? orderDirection : "ASC";
            if (orderDirection.equals("ASC")) {
                criteria.addOrder(Order.asc(orderField));
            } else {
                criteria.addOrder(Order.desc(orderField));
            }
            criteria.setResultTransformer(Criteria.PROJECTION);

            List<Trade> tradeList = tradeService.getByCriteria(criteria);
            return tradeList;
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }

        return null;
    }

    public List<Trade> getIncomingTrades(String orderField, String orderDirection) {
        User user = getCurrentUser();

        // Creating criteria object from current session
        Criteria criteria = tradeService.getCriteria(new Trade());
        // compare user id
        criteria.add(Restrictions.eq("trader.id", user.getId()));

        // Order by
        orderField = (orderField != null && !orderField.equals("")) ? orderField : "id";
        orderDirection = (orderDirection != null && !orderDirection.equals("")) ? orderDirection : "ASC";
        if (orderDirection.equals("ASC")) {
            criteria.addOrder(Order.asc(orderField));
        } else {
            criteria.addOrder(Order.desc(orderField));
        }
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Trade> tradeList = tradeService.getByCriteria(criteria);
        return tradeList;
    }


    /**
     * Get user feedbacks for trades
     *
     * @param user
     * @return
     */
    public String[] getUserFeedbacks(User user) {
        String[] feedbacks = new String[3];
        feedbacks[0] = "0";
        feedbacks[1] = "0";
        feedbacks[2] = "0";
        try {
            String query = "select count(*) from FeedBack where to_user_id=? and rating_star=5";
            Object[] params = new Object[1];
            params[0] = user.getId();
            Long positiveFeedbackCount = feedBackService.countQuery(query, params);
            query = "select count(*) from FeedBack where to_user_id=? and rating_star=4";
            Long neutralFeedbackCount = feedBackService.countQuery(query, params);
            query = "select count(*) from FeedBack where to_user_id=? and rating_star=3";
            Long negativeFeedbackCount = feedBackService.countQuery(query, params);
            feedbacks[0] = positiveFeedbackCount.toString();
            feedbacks[1] = neutralFeedbackCount.toString();
            feedbacks[2] = negativeFeedbackCount.toString();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return feedbacks;
    }


    /**
     * Get all database tables list
     *
     * @return
     */
    public List<Object> getAllTables() {
        return userService.queryAsObjectList("SHOW TABLES");
    }


    /**
     * Check if user is blocked
     *
     * @param userId
     * @return
     */
    public boolean isBlockedUser(int userId) {
        String query = "select count(*) from FeedBack where to_user_id=? and rating_star = 3";
        Object[] params = new Object[1];
        params[0] = userId;
        Long count = feedBackService.countQuery(query, params);
        if (count > 0) {
            return true;
        }
        return false;
    }


    /**
     * Get average bitcoin release time according to the time between payment sent and bitcoin release
     *
     * @param user
     * @return
     */
    public String getAverageBitcoinReleaseTime(User user) {
        Double averageTime = 0d;
        try {
            averageTime = tradeService.avgByUser(user, "bitcoinReleaseMinutes", "seller");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return averageTime.toString();
    }

    public String getMinimumReleaseTime(User user) {
        Double min = 0d;
        try {
            Projection projection = Projections.min("bitcoinReleaseMinutes");
            min = tradeService.getByProjection(user, "seller", projection);
        } catch (CoinmartException e) {
            logger.error(e.getMessage());
        }
        return min.toString();
    }

    public String getMaximumReleaseTime(User user) {
        Double max = 0d;
        try {
            Projection projection = Projections.max("bitcoinReleaseMinutes");
            max = tradeService.getByProjection(user, "seller", projection);
        } catch (CoinmartException e) {
            logger.error(e.getMessage());
        }
        return max.toString();
    }


    /**
     * Get admin coinfig for specific table
     *
     * @param tableName
     * @return
     */
    public AdminConfig getAdminConfig(String tableName) {
        try {
            List<AdminConfig> adminConfigList = adminConfigService.getConfig(tableName);
            if (adminConfigList.size() > 0) {
                return adminConfigList.get(0);
            }
            return new AdminConfig();
        } catch (CoinmartException e) {
            logger.error("getAdminConfig " + e.getMessage() + e.getStackTrace());
            throw new CoinmartException("getAdminConfig " + e.getMessage() + e.getStackTrace());
        }
    }

    /**
     * Get admin config values for specific table
     *
     * @param tableName
     * @return
     */
    public List<AdminConfigValues> getAdminConfigValues(String tableName) {
        try {
            List<AdminConfigValues> adminConfigValuesList = adminConfigService.getConfigValues(tableName);
            if (adminConfigValuesList.size() > 0) {
                return adminConfigValuesList;
            }
            return new ArrayList<AdminConfigValues>();
        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
            throw new CoinmartException("Error in getAdminConfigValues" + e.getMessage());
        }
    }

    @Transactional
    public AdminConfig getAdminConfigAndValues(Object obj) {
        try {
            List<AdminConfig> adminConfigList = adminConfigService.getConfig(obj.getClass().getAnnotationsByType(Table.class)[0].name());
            if (adminConfigList.size() > 0) {
                Hibernate.initialize(adminConfigList.get(0).getAdminConfigValuesList());
                return adminConfigList.get(0);
            }
        } catch (CoinmartException e) {
            logger.error("getAdminConfig " + e.getMessage() + e.getStackTrace());
            throw new CoinmartException("getAdminConfig " + e.getMessage() + e.getStackTrace());
        }
        return null;
    }

    /**
     * Get base url from configuration
     *
     * @return
     */
    public String getBaseUrl() {
        String baseUrl = this.baseUrl;
        try {
            if (baseUrl == null) {
                String query = "from AdminConfig where data_table=?";
                Object[] params = new Object[1];
                params[0] = "General";
                AdminConfig adminConfig = adminConfigService.first(query, params);
                if (adminConfig != null) {
                    query = "from AdminConfigValues where admin_config_id=?";
                    params[0] = adminConfig.getId();
                    List<AdminConfigValues> adminConfigValue = adminConfigValuesService.queryWithParameter(query, params);
                    if (adminConfigValue != null && adminConfigValue.size() > 0) {

                        baseUrl = Common.getAdminConfigValue(adminConfigValue, ConstantProperties.SESSION_BASE_URL, ConstantProperties.BASE_URL);
                        this.baseUrl = baseUrl;
                    }
                }
            }
        } catch (CoinmartException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {

        } finally {
            if (baseUrl == null)
                baseUrl = ConstantProperties.BASE_URL;
        }
        return baseUrl;
    }


    public Double getAverageBitcoinBuy(User user) {
        Double avg = 0d;
        try {
            avg = tradeService.avgByUser(user, "btcAmount", "buyer");
        } catch (Exception e) {
            logger.error(e.getMessage() + " Enable to get average");
        }
        return avg;
    }

    public Double getAverageBitcoinSell(User user) {
        Double avg = 0d;
        try {
            avg = tradeService.avgByUser(user, "btcAmount", "seller");
        } catch (Exception e) {
            logger.error(e.getMessage() + " Enable to get average");
        }
        return avg;
    }


    public String getTotalBitcoinEscrow() {
        try {

            String query = " from Escrow where user_id=? and trade_id=?";
            Object[] params = new Object[2];
        } catch (Exception e) {
            logger.error(e.getMessage() + " ");
        }

        return null;
    }


    public boolean escrowBitcoins(Trade trade) {
        try {
            Object[] params = new Object[2];
            params[0] = trade.getId();
            params[1] = trade.getSeller().getId();
            Escrow escrow = escrowService.first(" from Escrow where trade_id=? and seller_id=?", params);
            if (escrow == null) {
                escrow = new Escrow();
                escrow.setEscrowAmount(trade.getBtcAmount());
                escrow.setCommisionAmount(getCommisionAmount(Double.parseDouble(trade.getBtcAmount())).toString());
                escrow.setSeller(trade.getSeller());
                escrow.setBuyer(trade.getBuyer());
                escrow.setTrade(trade);
            } else {
                escrow.setEscrowAmount(trade.getBtcAmount());
                escrow.setCommisionAmount(getCommisionAmount(Double.parseDouble(trade.getBtcAmount())).toString());
            }
            escrowService.saveOrUpdate(escrow);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public boolean releaseBitcoins(Trade trade) {
        try {
            Object[] params = new Object[2];
            params[0] = trade.getId();
            params[1] = trade.getSeller().getId();
            Escrow escrow = escrowService.first(" from Escrow where trade_id=? and seller_id=?", params);
            if (escrow != null) {
                escrow.setReleased(true);
                escrowService.saveOrUpdate(escrow);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public boolean unEscrowBitcoins(Trade trade) {
        try {
            Object[] params = new Object[2];
            params[0] = trade.getId();
            params[1] = trade.getSeller().getId();
            Escrow escrow = escrowService.first(" from Escrow where trade_id=? and seller_id=?", params);
            if (escrow != null) {
                escrow.setEscrowAmount("0");
                escrow.setCommisionAmount("0");
                Set<Escrow> escrowSet = new HashSet<Escrow>();
                escrowSet.add(escrow);
                trade.setEscrowSet(escrowSet);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    public Double getTotalEscrowedBitcoins() {
        try {
            Double internalTransactionBitcoinTotal = escrowService.getByProjection(getCurrentUser(), "seller", Projections.sum("escrowAmount"), false);
            Double internalTransactionBitcoinCommisionTotal = escrowService.getByProjection(getCurrentUser(), "seller", Projections.sum("commisionAmount"), false);
            return internalTransactionBitcoinTotal;
        } catch (CoinmartNumberFormatException e) {
            logger.error("Parsing error " + e.getMessage() + e.getStackTrace());
            //throw new CoinmartNumberFormatException("Error formating number in : getTotalEscrowedBitcoins");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0d;
    }


    /**
     * Get current users wallet balance
     * it gets all internal and external transfers and calculates spent and received bitcoins
     *
     * @return
     */
    public Double getCurrentUserBalance() {
        Double finalBalance = 0d;
        try {
            User currentUser = getCurrentUser();
            Double internalTransactionBitcoinTotal = escrowService.getByProjection(currentUser, "buyer", Projections.sum("escrowAmount"), true);
            Double internalSpentBitcoins = escrowService.getByProjection(currentUser, "seller", Projections.sum("escrowAmount"), true);
            Double inEscrowBitcoins = escrowService.getByProjection(currentUser, "seller", Projections.sum("escrowAmount"), false);
            Double inEscrowCommission = escrowService.getByProjection(currentUser, "seller", Projections.sum("commisionAmount"), false);
            Double externalTransactionBitcoinTotal = directTransactionService.getByProjection(currentUser, "user", Projections.sum("bitcoinAmount"), false);
            Double externalSpent = directTransactionService.getByProjection(currentUser, "user", Projections.sum("bitcoinAmount"), true);
            Double internalDirectTransferCredit = internalTransferService.getByProjection(currentUser, "buyer", Projections.sum("bitcoinAmount"));
            Double internalDirectTransferDebit = internalTransferService.getByProjection(currentUser, "seller", Projections.sum("bitcoinAmount"));

            finalBalance = internalTransactionBitcoinTotal + externalTransactionBitcoinTotal - internalSpentBitcoins - externalSpent - inEscrowBitcoins - inEscrowCommission + internalDirectTransferCredit - internalDirectTransferDebit;
        } catch (CoinmartNumberFormatException e) {
            logger.error("Parsing error " + e.getMessage() + e.getStackTrace());
            //throw new CoinmartNumberFormatException("Error formating number in : getCurrentUserBalance");
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {

        }
        return finalBalance;
    }

    /**
     * Same as getCurrentUserBalance() just allows to pass User object as parameter
     *
     * @param user
     * @return
     */
    public Double getUserBalance(User user) {
        try {
            Double internalTransactionBitcoinTotal = escrowService.getByProjection(user, "buyer", Projections.sum("escrowAmount"), true);
            Double internalSpentBitcoins = escrowService.getByProjection(user, "seller", Projections.sum("escrowAmount"), true);
            Double inEscrowBitcoins = escrowService.getByProjection(user, "seller", Projections.sum("escrowAmount"), false);
            Double externalTransactionBitcoinTotal = directTransactionService.getByProjection(user, "user", Projections.sum("bitcoinAmount"), false);
            Double inEscrowCommission = escrowService.getByProjection(user, "seller", Projections.sum("commisionAmount"), false);
            Double externalSpent = directTransactionService.getByProjection(user, "user", Projections.sum("bitcoinAmount"), true);
            Double internalDirectTransferCredit = internalTransferService.getByProjection(user, "buyer", Projections.sum("bitcoinAmount"));
            Double internalDirectTransferDebit = internalTransferService.getByProjection(user, "seller", Projections.sum("bitcoinAmount"));
            return internalTransactionBitcoinTotal + externalTransactionBitcoinTotal - internalSpentBitcoins - externalSpent - inEscrowBitcoins - inEscrowCommission + internalDirectTransferCredit - internalDirectTransferDebit;
        } catch (CoinmartNumberFormatException e) {
            logger.error("Parsing error " + e.getMessage() + e.getStackTrace());
            //throw new CoinmartNumberFormatException("Error formating number in : getUserBalance");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0d;
    }

    /**
     * @param advertise
     * @return
     */
    public Double getAdvertiseMaxLimit(Advertise advertise) {
        try {
            if (advertise.getAdType().getValue().equalsIgnoreCase("buy")){
                return Double.parseDouble(Common.plainStringPrice(advertise.getMaxLimit()));
            }
            Double userBtcBalance = getUserBalance(advertise.getUser());
            Double btcRate = Double.valueOf(Common.plainStringPrice(advertise.getBtcRate())).doubleValue();
            Double advertiseMaxLimit = Double.valueOf(Common.plainStringPrice(advertise.getMaxLimit())).doubleValue();
            Double advertiseBtc = advertiseMaxLimit / btcRate;
            Double commissionAmount = getCommisionAmount(advertiseBtc);
            Double finalBtc = userBtcBalance - commissionAmount;
            Double finalAllowedMaxLimitAmount = (((finalBtc) * btcRate) > advertiseMaxLimit) ? advertiseMaxLimit : (finalBtc * btcRate);
            return (finalAllowedMaxLimitAmount >= 0) ? finalAllowedMaxLimitAmount : 0d;

        } catch (CoinmartNumberFormatException e) {
            logger.error(e.getMessage() + e.getStackTrace());
            //throw new CoinmartNumberFormatException("Error formating number in : getCommisionAmount btcRate");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0d;
    }


    /**
     * Is advertise is allowed to view on user's screen
     *
     * @param advertise
     * @return
     */
    public boolean isAdvertiseAllowed(Advertise advertise) {
        try {
            Double maxAllowedBtcAcmount = getAdvertiseMaxLimit(advertise);
            Double advertiseMaxAmount = Double.parseDouble(Common.plainStringPrice(advertise.getMaxLimit()));
            if (maxAllowedBtcAcmount >= advertiseMaxAmount)
                return true;
        } catch (CoinmartNumberFormatException e) {

            logger.error("Number format exception isAdvertiseAllowed" + e.getMessage() + e.getStackTrace());
        } catch (Exception e) {
            logger.error("exception isAdvertiseAllowed" + e.getMessage() + e.getStackTrace());
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return false;
    }




    /**
     * @param amount
     * @return
     */
    public Double getCommisionAmount(Double amount) {
        try {
            return ((getCommisionRate() * amount) / 100);
        } catch (CoinmartNumberFormatException e) {
            throw new CoinmartNumberFormatException("Number format exception getCommisionAmount" + e.getStackTrace());
        } catch (Exception e) {
            throw new CoinmartException("Number format exception getCommisionAmount" + e.getStackTrace());
        }
    }

    /**
     * Get commission rate from configuration table
     *
     * @return
     */
    public Double getCommisionRate() {
        Double retVal = 0d;
        try {
            List<AdminConfigValues> adminConfigValuesList = getAdminConfigValues("General");
            String value = Common.getAdminConfigValue(adminConfigValuesList, ConstantProperties.BTC_TRADE_RATE_CONFIG_STRING, ConstantProperties.DEFAULT_BTC_TRADE_RATE.toString());
            try {
                retVal = Double.parseDouble(value);
            } catch (NumberFormatException p) {
                logger.error(p.getMessage() + " Error while parsing ConstantProperties.DEFAULT_BTC_TRADE_RATE");
                //throw new CoinmartNumberFormatException("Error formating number in : getCommisionAmount" + value);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (retVal == 0)
                retVal = ConstantProperties.DEFAULT_BTC_TRADE_RATE;
        }
        return retVal;
    }


    /**
     * Get amount of minimum transaction for bitcoins
     *
     * @return
     */
    public Double getMinimumAllowedBtcTransaction() {
        Double minAllowedBitcoinTransaction = 0d;
        try {
            List<AdminConfigValues> adminConfigValuesList = getAdminConfigValues("General");
            String value = Common.getAdminConfigValue(adminConfigValuesList, ConstantProperties.MIN_BITCOIN_AMOUNT, ConstantProperties.MIN_BITCOIN_AMOUNT_DEFAULT.toString());
            try {
                minAllowedBitcoinTransaction = Double.parseDouble(value);
            } catch (CoinmartNumberFormatException p) {
                logger.error(p.getMessage() + " Error while parsing ConstantProperties.MIN_BITCOIN_AMOUNT_DEFAULT");
                //throw new CoinmartNumberFormatException("Error formating number in : getMinimumAllowedBtcTransaction" + minAllowedBitcoinTransaction);
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        } finally {
            if (minAllowedBitcoinTransaction == 0)
                minAllowedBitcoinTransaction = ConstantProperties.MIN_BITCOIN_AMOUNT_DEFAULT;
        }
        return minAllowedBitcoinTransaction;
    }


    /**
     * Chceck if internal transfer is allowed for user
     *
     * @param user
     * @param form
     * @return
     */
    public boolean canDoInternalTransfer(User user, InternalTransferForm form) {
        try {
            if (!user.isAccountDeleted() && user.isEnabled()) {
                Double availableBalance = getUserBalance(user);
                Double transferBtcAmount = Double.valueOf(Common.plainStringPrice(form.getBtcAmount())).doubleValue();
                if (availableBalance > transferBtcAmount && getMinimumAllowedBtcTransaction() < transferBtcAmount) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Check if user can do external transfer
     *
     * @param user
     * @param form
     * @return
     */
    public boolean canDoExternalTransfer(User user, ExternalTransferForm form) {
        try {
            if (!user.isAccountDeleted() && user.isEnabled()) {
                Double availableBalance = getUserBalance(user);
                Double transferBtcAmount = Double.valueOf(Common.plainStringPrice(form.getBtcAmount())).doubleValue();
                if (availableBalance > transferBtcAmount && getMinimumAllowedBtcTransaction() < transferBtcAmount) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return false;
    }


    /**
     * Get last advertisement sequence id
     * we are using YY (year's last tow digit) and sequence from 1 for advertisement id
     * eg. for year 2018 for first advertisement in system we will have : 181 as advertisement id
     *
     * @return
     */
    public String getLastAdvertisementSequenceId() {
        try {
            Object[] params = new Object[0];
            Advertise advertise = advertisementService.last("id");
            if (advertise != null)
                return advertise.getAdvertisementSequenceId();
        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "0";
    }

    /**
     * generate new advertisement id by getting and setting last advertisement sequence
     *
     * @return
     */
    public String getNewAdvertisementId() {
        try {
            DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
            String year = df.format(Calendar.getInstance().getTime());
            Integer id = Integer.parseInt(getLastAdvertisementSequenceId()) + 1;
            return year + id.toString();
        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return null;
    }


    /**
     * Get last advertisement sequence id
     * we are using YY (year's last tow digit) and sequence from 1 for advertisement id
     * eg. for year 2018 for first advertisement in system we will have : 181 as advertisement id
     *
     * @return
     */
    public String getLastTradeSequenceId() {
        try {
            Object[] params = new Object[0];
            Trade trade = tradeService.last("id");
            if (trade != null)
                return trade.getTradeSequenceId();
        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "0";
    }

    /**
     * generate new advertisement id by getting and setting last advertisement sequence
     *
     * @return
     */
    public String getNewTradeId() {
        try {
            DateFormat yr = new SimpleDateFormat("yy"); // Just the year, with 2 digits
            DateFormat mn = new SimpleDateFormat("mm"); // Just the year, with 2 digits
            String year = yr.format(Calendar.getInstance().getTime());
            String month = mn.format(Calendar.getInstance().getTime());
            Integer id = Integer.parseInt(getLastTradeSequenceId()) + 1;
            return year + month + id.toString();
        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return null;
    }


    public String getPhoneVerificationUrl(User currentUser, String otp) {
        try {
            String authKey = environment.getProperty("smsAuthKey");
            String url = environment.getProperty("smsAuthUrl");
            String rout = environment.getProperty("smsAuthRoute");
            String sender = environment.getProperty("smsAuthSender");
            String mobile = currentUser.getPhone();
            String countryCode = currentUser.getCountryCode();
            String msg = "Your OTP to verify your phone for Coin Mart is : " + otp;
            String authUrl = url + "authkey=" + authKey + "&mobiles=" + mobile + "&message=" + msg + "&route=" + rout + "&country=" + countryCode + "&encrypt=&sender=" + sender;
            return authUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Generate otp
     *
     * @param user
     * @return
     */
    public Boolean generateOtp(User user) {
        try {
            String query = " from PhoneVerification where user_id=?";
            Object[] params = new Object[1];
            params[0] = user.getId();
            PhoneVerification phoneVerification = phoneVerificationService.first(query, params);
            String otp = Common.OTP(6);
            if (phoneVerification != null) {
                phoneVerification.setUserOtp(otp);
            } else {
                phoneVerification = new PhoneVerification();
                phoneVerification.setUser(user);
                phoneVerification.setUserOtp(otp);
            }
            String authUrl = getPhoneVerificationUrl(user, otp);
            String res = restTemplate.getForObject(authUrl, String.class);
            phoneVerificationService.saveOrUpdate(phoneVerification);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return false;
    }

    /**
     * Validate generated OTP
     *
     * @param user
     * @param otp
     * @return
     */
    public Boolean validateOtp(User user, String otp) {
        try {
            String query = " from PhoneVerification where user_id=?";
            Object[] params = new Object[1];
            params[0] = user.getId();
            PhoneVerification phoneVerification = phoneVerificationService.first(query, params);
            if (phoneVerification != null) {
                if (phoneVerification.getUserOtp().equals(otp)) {
                    LocalDateTime date1 = (phoneVerification.getUpdatedAt() != null) ? phoneVerification.getUpdatedAt() : phoneVerification.getCreatedAt();
                    LocalDateTime date2 = new LocalDateTime();
                    Duration duration = new Duration(date1.toDateTime(), date2.toDateTime());
                    Long minutes = duration.getStandardMinutes();
                    if (minutes < ConstantProperties.PHONE_VERIFICATION_OTP_EXPIRY_MINUTES) {
                        user.setPhoneVerified(true);
                        userService.saveOrUpdate(user);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return false;
    }

    /**
     * Get user's current allocated wallet
     *
     * @return
     */
    public String getUserCurrentWalletAddress() {
        try {
            User user = getCurrentUser();
            if (user != null) {
                UserWallet userWallet = userWalletService.getCurrentUserWallet(user);
                if (userWallet != null) {
                    return userWallet.getWalletAddress();
                }
            }
        } catch (Exception e) {

        }
        return "-";
    }


    @Transactional
    public Integer getRecordPerPage(Object obj) {
        try {
            AdminConfig adminConfig = getAdminConfigAndValues(obj);
            return (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();

        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
    }


    /**
     * @return
     */
    public Currency getCurrentCurrency(HttpServletRequest request) {
        Currency currency = new Currency();
        String defaultCurrency = null;
        try {
            try {
                defaultCurrency = Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().equals(ConstantProperties.COOKIE_DEFAULT_CURRENCY))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
                if (defaultCurrency == null) {
                    defaultCurrency = Arrays.stream(request.getCookies())
                            .filter(c -> c.getName().equals(ConstantProperties.COOKIE_USER_LOCATION_COUNTRY_CODE))
                            .findFirst()
                            .map(Cookie::getValue)
                            .orElse(null);
                    if (defaultCurrency!= null){
                        defaultCurrency = java.util.Currency.getInstance(new Locale("", defaultCurrency)).getCurrencyCode();
                    }
                }

                //defaultCurrency = WebUtils.getCookie(request, ConstantProperties.COOKIE_DEFAULT_CURRENCY).getValue();
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            if (defaultCurrency == null) {
                if (Common.isLoggedIn()) {
                    User currentUser = getCurrentUser();
                    return currentUser.getCurrency();
                } else {
                    List<AdminConfigValues> adminConfigValues = getAdminConfigValues(ConstantProperties.ADMIN_CONFIG_GENERAL_TABLE);
                    String currencyCode = Common.getAdminConfigValue(adminConfigValues, ConstantProperties.DEFAULT_CURRENCY_PROPERTY, ConstantProperties.DEFAULT_CURRENCY);
                    Object[] params = new Object[1];
                    params[0] = currencyCode;
                    currency = currencyService.first("from Currency where currency_code=?", params);
                    if (currency == null) {
                        Object[] currencyParams = new Object[0];
                        currency = currencyService.first("from Currency", params);
                    }
                }
            } else {
                Object[] params = new Object[1];
                params[0] = defaultCurrency;
                currency = currencyService.first("from Currency where currency_code=?", params);
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return currency;
    }


}




