/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl;


import com.gradle.services.iface.ICaptchaService;
import com.gradle.services.impl.user.ReCaptchaAttemptService;
import com.gradle.util.GoogleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service("captchaService")
public class CaptchaService implements ICaptchaService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);


    private HttpServletRequest request;

    @Autowired
    private Environment captchaSettings;

    @Autowired
    private ReCaptchaAttemptService reCaptchaAttemptService;

    @Autowired
    private RestOperations restTemplate;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Override
    public boolean processResponse(final String response, HttpServletRequest request) {
        this.request = request;
        LOGGER.debug("Attempting to validate response {}", response);
        boolean ret = false;
        if (reCaptchaAttemptService.isBlocked(getClientIP())) {
            //throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
        }

        if (!responseSanityCheck(response)) {
            //throw new ReCaptchaInvalidException("Response contains invalid characters");
        }

        final URI verifyUri = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

            LOGGER.debug("Google's response: {} ", googleResponse.toString());

            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
            }else {
                ret = true;
            }
        } catch (RestClientException rce) {
            //throw new ReCaptchaUnavailableException("Registration unavailable at this time.  Please try again later.", rce);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
        return ret;

    }

    private boolean responseSanityCheck(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public String getReCaptchaSite() {
        return captchaSettings.getRequiredProperty("captch.site.key");
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSettings.getRequiredProperty("captch.site.secret");
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}