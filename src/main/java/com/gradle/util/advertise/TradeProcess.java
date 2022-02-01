/*
 * Copyright (c) 6/4/18 10:54 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util.advertise;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.user.User;
import com.gradle.exception.handler.CoinmartNumberFormatException;
import com.gradle.util.Alerts;
import com.gradle.util.Common;
import com.gradle.util.ServiceUtil;
import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * This is a helper class for Trade process
 * Specifically for Status change in trades
 */
public class TradeProcess {

    public static final Logger logger = Logger.getLogger(TradeProcess.class);

    @Autowired
    private Alerts alerts;


    private ServiceUtil serviceUtil;


    public TradeProcess(ServiceUtil serviceUtil){
        this.serviceUtil = serviceUtil;
    }


    /**
     * process escrow event for specific trade
     * @param trade
     * @param currentUser
     * @return
     */
    public boolean processEscrow(Trade trade, User currentUser) {
        try {

            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_INPROCESS && trade.getSeller().getId() == currentUser.getId()) {

                NumberFormat format = NumberFormat.getInstance(Locale.US);


                // Current Available Bitcoin Amount for buyer
                Double finalBitcoinAmount = serviceUtil.getCurrentUserBalance();

                // Trade amount in currency (convert to btc if needed)
                Double tradeAmount = Double.parseDouble(format.parse(trade.getAmount()).toString());

                // Rate of btc for Advertisement
                Double btcRate = Double.parseDouble(format.parse(trade.getAdvertise().getBtcRate()).toString());

                // calculate transaction amount in currency by dividing trade amount with btc rate
                Double finalTransactionBtcAmount = (tradeAmount / btcRate);

                // Deduct trade bitcoins from available bitcoins for seller
                Double finalAvailableBalance = finalBitcoinAmount - finalTransactionBtcAmount - serviceUtil.getCommisionAmount(Double.parseDouble(trade.getBtcAmount()));

                // Max and Min limit of trade on Advertisement
                Double maxLimit = Double.valueOf(Common.plainStringPrice(trade.getAdvertise().getMaxLimit())).doubleValue();
                Double minLimit = Double.valueOf(Common.plainStringPrice(trade.getAdvertise().getMinLimit())).doubleValue();


                btcRate = Double.valueOf(Common.plainStringPrice(trade.getAdvertise().getBtcRate())).doubleValue();

                // Final amount in currency(eg. USD) which is equal to btc rate for advertisement
                Double finalAvailableAmount = finalAvailableBalance * btcRate;

                /**
                 *
                 * Final max amount limit in currency(eg. USD) which is equal to btc rate for seller
                 * like if seller will have 1 btc after this trade, and rate of btc in USD = $1000
                 * then max limit he can have is $1000 in doller and 1 btc in btc
                 */
                Double finalAvailableMaxLimit = (maxLimit > finalAvailableAmount) ? finalAvailableAmount : maxLimit;

                trade.setBitcoinEscrowTime(new LocalDateTime());
                if ((minLimit > finalAvailableAmount)) {
                    trade.getAdvertise().setHidden(true);
                } /*else {
                    trade.getAdvertise().setMaxLimit(Common.plainStringPrice(finalAvailableMaxLimit.toString()));
                }*/
                if (finalAvailableBalance <= 0) {
                    return false;
                }
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_BITCOIN_ESCROWED);
                return true;
            }
        }
        catch (CoinmartNumberFormatException e){
            logger.error(e.getMessage() + e.getStackTrace());
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }


    public boolean processBitcoinReceived(Trade trade, User currentUser) {
        try {
            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_BITCOIN_RELEASED && trade.getBuyer().getId() == currentUser.getId()) {
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_COMPLETED);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean processPaymentReceived(Trade trade, User currentUser) {
        try {
            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_PAYMENT_SENT && trade.getSeller().getId() == currentUser.getId()) {
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_PAYMENT_RECEIVED);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean processSendPayment(Trade trade, User currentUser) {
        try {
            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_BITCOIN_ESCROWED && trade.getBuyer().getId() == currentUser.getId()) {
                trade.setPaymentSentTime(new LocalDateTime());
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_PAYMENT_SENT);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    public boolean processCancel(Trade trade, User currentUser) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_BITCOIN_ESCROWED && currentUser.getId() == trade.getBuyer().getId()) {
                /*Double finalBitcoinAmount = serviceUtil.getCurrentUserBalance();
                Double finalBitcoinAmountInEscrow = serviceUtil.getTotalEscrowedBitcoins();
                Double finalBitcoinRevertedAmount = finalBitcoinAmount + finalBitcoinAmountInEscrow;
                trade.getSeller().setFinalBitcoinAmount(finalBitcoinRevertedAmount.toString());*/
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_CANCELLED);
                return true;
            } else if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_BITCOIN_ESCROWED && currentUser.getId() == trade.getSeller().getId()
                    &&
                    Common.isSellerAllowedToCancel(trade)
                    ) {
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_CANCELLED);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean processBitcoinReleased(Trade trade, User currentUser) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_PAYMENT_SENT && currentUser.getId() == trade.getSeller().getId()) {
                trade.setBitcoinReleaseTime(new LocalDateTime());
                Integer minutes = Minutes.minutesBetween(trade.getPaymentSentTime(), trade.getBitcoinReleaseTime()).getMinutes();
                trade.setBitcoinReleaseMinutes(minutes.toString());

            }
            Double finalBitcoinAmount = serviceUtil.getCurrentUserBalance();
            Double tradeAmount = Double.parseDouble(format.parse(trade.getAmount()).toString());
            Double btcRate = Double.parseDouble(format.parse(trade.getAdvertise().getBtcRate()).toString());
            Double finalTransactionBtcAmount = (tradeAmount / btcRate);
            Double finalAvailableBalance = finalBitcoinAmount + finalTransactionBtcAmount;
            if (finalAvailableBalance > 0) {
                //trade.getBuyer().setFinalBitcoinAmount(finalAvailableBalance.toString());
                trade.getTradeStatus().setStatusCode(ConstantProperties.TRADE_STATUS_COMPLETED);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
