/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.advertisement.Trade;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.validator.iface.TradeConstraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.util.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class TradeValidator implements ConstraintValidator<TradeConstraint, Trade> {
    @Autowired
    TradeService tradeService;

    @Autowired
    private LocaleHelper localeHelper;

    @Autowired
    private ServiceUtil serviceUtil;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(TradeValidator.class);

    @Override
    public void initialize(TradeConstraint trade) {
    }

    /**
     * @param trade Trade type object
     * @param cxt   validator context
     * @return
     */

    @Override
    public boolean isValid(Trade trade, ConstraintValidatorContext cxt) {
        boolean ret = true;

        // Trade validation can be done here
        try {

            if (trade.getAdvertise() != null && trade.getAdvertise().getUser() != null) {

                if (trade.getAdvertise().getRestrictedAmounts() != null && !trade.getAdvertise().getRestrictedAmounts().isEmpty()){
                    String[] restrictedAmounts = trade.getAdvertise().getRestrictedAmounts().split(",");
                    if (!ArrayUtils.contains(restrictedAmounts, trade.getAmount())){

                        cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Trade.error.restricted.amount", new String[]{trade.getAdvertise().getRestrictedAmounts()}, "Please choose from restricted amounts")).addPropertyNode("amount").addConstraintViolation();
                        ret=false;
                    }
                }

                if (trade.getAdvertise().getUser().getId() == serviceUtil.getCurrentUser().getId()) {
                    ret = false;
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Trade.user.trader.same", null, "Trade can not be created on your own Advertisement")).addPropertyNode("trader").addConstraintViolation();
                }
                if (trade.getAdvertise().getMinLimit() != null && trade.getAdvertise().getMaxLimit() != null
                        &&
                        !trade.getAdvertise().getMinLimit().equals("") && !trade.getAdvertise().getMaxLimit().equals("")
                        ) {
                    if (Double.parseDouble(trade.getAmount()) < Double.parseDouble(trade.getAdvertise().getMinLimit())) {
                        ret=false;
                        cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Trade.error.minimum", null, "Amount must be greater than minimum amount")).addPropertyNode("amount").addConstraintViolation();
                    }
                    if (Double.parseDouble(trade.getAmount()) > serviceUtil.getAdvertiseMaxLimit(trade.getAdvertise())) {
                        ret=false;
                        cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Trade.error.maximum", null, "Amount must be less than minimum amount")).addPropertyNode("amount").addConstraintViolation();
                    }
                }else {
                    cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Trade.amount.empty", null, "Amount can not be null")).addPropertyNode("amount").addConstraintViolation();
                }
            }
        } catch (Exception e) {
            logger.error("Class : TradeValidator error  : " + e.getMessage());
            ret = false;
        } finally {

        }
        return ret;
    }
}
