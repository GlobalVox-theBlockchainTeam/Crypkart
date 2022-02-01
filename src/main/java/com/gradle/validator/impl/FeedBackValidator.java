/*
 * Copyright (c) 19/3/18 10:57 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.validator.impl;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.services.iface.user.FeedBackService;
import com.gradle.util.LocaleHelper;
import com.gradle.util.ServiceUtil;
import com.gradle.validator.iface.FeedBackConstraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for payment type
 */
public class FeedBackValidator implements ConstraintValidator<FeedBackConstraint, FeedBack> {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private LocaleHelper localeHelper;

    @Autowired
    private ServiceUtil serviceUtil;

    private ConstraintValidatorContext cxt;

    public static final Logger logger = Logger.getLogger(FeedBackValidator.class);

    @Override
    public void initialize(FeedBackConstraint feedback) {
    }

    /**
     * @param feedBack FeedBack type object
     * @param cxt      validator context
     * @return
     */

    @Override
    public boolean isValid(FeedBack feedBack, ConstraintValidatorContext cxt) {
        boolean ret = true;

        // Trade validation can be done here
        try {
            if (feedBack.getStar()==0){
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Feedback.select.rating.error", null, "Select rating")).addPropertyNode("star").addConstraintViolation();
                ret =false;
            }

            User user = serviceUtil.getCurrentUser();
            Object[] params = new Object[3];
            if ((feedBack.getTrade().getAdvertise().getUser().getId() == user.getId())) {
                params[0] = user.getId();
                params[1] = feedBack.getTrade().getUser().getId();
            } else {
                params[0] = user.getId();
                params[1] = feedBack.getTrade().getAdvertise().getUser().getId();
            }

            params[2] = feedBack.getTrade().getId();
            FeedBack feedBackDb = feedBackService.first(" from FeedBack where user_id=? and to_user_id=? and trade_id=?", params);
            if (feedBackDb != null) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Feedback.duplicate.error", null, "Multiple feedback on same trade is not allowed")).addPropertyNode("star").addConstraintViolation();
                ret = false;
            }
            if (user.getId() != feedBack.getTrade().getUser().getId() && user.getId() != feedBack.getTrade().getAdvertise().getUser().getId()) {
                cxt.buildConstraintViolationWithTemplate(localeHelper.getApplicationPropertiesText("Feedback.authorize.error", null, "You are not authorized to submit feed back for this trade")).addPropertyNode("star").addConstraintViolation();
                ret = false;
            }

            /*
            (
                            feedBack.getTrade().getAdvertise().getUser().getId() == user.getId()
                                    &&
                            feedBack.getTrade().isFeedbackFromAdvertiser()
                    )
                            ||
                    (
                            feedBack.getTrade().getUser().getId() == user.getId()
                                    &&
                            feedBack.getTrade().isFeedbackFromTrader()
                    )
             */

        } catch (Exception e) {
            logger.error("Class : TradeValidator error  : " + e.getMessage());
            ret = false;
        } finally {

        }
        return ret;
    }
}
