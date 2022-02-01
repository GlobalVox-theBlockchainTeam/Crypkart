/*
 * Copyright (c) 9/4/18 11:20 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.user;

import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.PasswordResetToken;
import com.gradle.entity.user.ReportedUser;
import com.gradle.entity.user.User;
import com.gradle.events.event.PasswordResetCompleteEvent;
import com.gradle.exception.handler.CoinmartException;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.services.iface.user.*;
import com.gradle.util.Common;
import com.gradle.util.Paging;
import com.gradle.util.constants.ConstantProperties;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequestMapping(value = "/user")
@Controller
public class UserController extends AbstractBaseController {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    private ReportedUserService reportedUserService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CountriesService countriesService;

    @Autowired
    private ZonesService zonesService;

    @Autowired
    private Validator validator;

    @Autowired
    private RestOperations restTemplate;


    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private Environment environment;

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }

    /**
     * @param username           username
     * @param model
     * @param redirectAttributes
     * @return
     */
    /*@PreAuthorize("hasRole('USER')")*/
    @GetMapping(value = "/profile/{username}")
    public String userProfile(
            @PathVariable String username,
            ModelMap model, RedirectAttributes redirectAttributes) {

        User user = serviceUtil.getUserFromUsernameOrEmail(username);
        String averageTime = serviceUtil.getAverageBitcoinReleaseTime(user);
        String minTime = serviceUtil.getMinimumReleaseTime(user);
        String maxTime = serviceUtil.getMaximumReleaseTime(user);
        User currentUser = serviceUtil.getCurrentUser();
        Double averageBuy = serviceUtil.getAverageBitcoinBuy(user);
        Double averageSell = serviceUtil.getAverageBitcoinSell(user);
        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("feedbacks", serviceUtil.getUserFeedbacks(user));
        model.addAttribute("avgTime", averageTime);
        model.addAttribute("minTime", minTime);
        model.addAttribute("maxTime", maxTime);
        model.addAttribute("averageBuy", averageBuy);
        model.addAttribute("averageSell", averageSell);
        model.addAttribute("title", "About " + user.getUsername());

        return "user/profile";
    }


    /**
     * @param type               trade id on which user is submitting feedback
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = "/feedback/{type}")
    public String createFeedback(@PathVariable String type, ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            if (id != null && id != 0) {
                Trade trade = tradeService.find(id);
                if (trade != null) {
                    User user = serviceUtil.getCurrentUser();
                    int feedbackUserId = (user.getId() == trade.getTrader().getId()) ? trade.getUser().getId() : trade.getTrader().getId();
                    User feedbackUser = userService.find(feedbackUserId);
                    model.addAttribute("user", user);
                    model.addAttribute("feedbackUser", feedbackUser);
                    model.addAttribute("feedBack", new FeedBack());
                    model.addAttribute("trade", trade);
                    return "user/feedback/create";
                }
            }
        } catch (Exception e) {

        }
        alerts.setWarning("General.error.msg");
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/home";
    }

    /**
     * @param feedBack           feedback object submitted by user
     * @param result
     * @param model
     * @param redirectAttributes
     * @param type               trade id on which feedback is getting submitted
     * @return
     */
    @PostMapping(value = "/feedback/{type}")
    public String submitFeedback(@ModelAttribute("feedBack") @Valid FeedBack feedBack, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes,
                                 @PathVariable String type) {
        Integer id = 0;
        try {
            id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            if (!result.hasErrors() && id != null && id != 0) {
                Trade trade = tradeService.find(id);
                if (trade != null && trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_COMPLETED) {
                    User user = serviceUtil.getCurrentUser();
                    int feedbackUserId = (user.getId() == trade.getTrader().getId()) ? trade.getUser().getId() : trade.getTrader().getId();
                    User feedbackUser = userService.find(feedbackUserId);
                    feedBack.setUser(user);
                    feedBack.setUserTo(feedbackUser);
                    feedBack.setTrade(trade);
                    feedBackService.save(feedBack);
                    Trade feedbackTrade = feedBack.getTrade();
                    if (feedbackTrade.getAdvertise().getUser().getId() == user.getId()) {
                        feedbackTrade.setFeedbackFromAdvertiser(true);
                        tradeService.saveOrUpdate(feedbackTrade);
                    } else {
                        feedbackTrade.setFeedbackFromTrader(true);
                        tradeService.saveOrUpdate(feedbackTrade);
                    }
                    alerts.setSuccess("Feedback.successfully.created");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                    return "redirect:/trade/list";

                }
            } else if (result.hasErrors()) {
                Trade trade = tradeService.find(id);
                if (trade != null) {
                    User user = serviceUtil.getCurrentUser();
                    int feedbackUserId = (user.getId() == trade.getTrader().getId()) ? trade.getUser().getId() : trade.getTrader().getId();
                    User feedbackUser = userService.find(feedbackUserId);
                    model.addAttribute("user", user);
                    model.addAttribute("feedbackUser", feedbackUser);
                    model.addAttribute("feedBack", feedBack);
                    model.addAttribute("trade", trade);
                    alerts.setError("Error.saving.pay");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                    return "user/feedback/create";
                }
            }
        } catch (Exception e) {
            alerts.setError("Feedback.exception.error");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/user/feedback/" + type;
        }
        alerts.setWarning("General.error.msg");
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/home";
    }


    /**
     * @param page               page no for paging
     * @param maxCount           max record per page
     * @param search             search string
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = {"/feedbacks", "/feedbacks/{page}", "/feedbacks/{page}/{maxCount}"})
    public String feedbackListReceived(
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            ModelMap model, RedirectAttributes redirectAttributes) {

        /*AdminConfigUtil<FeedBack> adminConfigUtil = new AdminConfigUtil<FeedBack>();
        AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new FeedBack());
        Integer recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();*/
        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new FeedBack()) : maxCount.get();
        Integer pageNo = 1;

        if (page.isPresent()) {
            pageNo = page.get();
        }
        try {
            User user = serviceUtil.getCurrentUser();
            List<FeedBack> feedBackList = feedBackService.findPaginatedByUser(pageNo.intValue(), recordPerPage, new FeedBack(), null, user, "to_user_id");
            Long count = feedBackService.countByUser(user, "userTo");
            model.addAttribute("feedbackList", feedBackList);
            model.addAttribute("user", user);
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
            model.addAttribute("listType", "received");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "user/feedback/list";

    }

    /**
     * @param page               page no for paging
     * @param maxCount           max record per page
     * @param search             search string
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = {"/feedbacks/given", "/feedbacks/given/{page}", "/feedbacks/given/{page}/{maxCount}"})
    public String feedbackListGiven(
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            ModelMap model, RedirectAttributes redirectAttributes) {


        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new FeedBack()) : maxCount.get();
        /*AdminConfigUtil<FeedBack> adminConfigUtil = new AdminConfigUtil<FeedBack>();
        AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new FeedBack());
        Integer recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();*/


        Integer pageNo = 1;

        if (page.isPresent()) {
            pageNo = page.get();
        }
        try {
            User user = serviceUtil.getCurrentUser();
            List<FeedBack> feedBackList = feedBackService.findPaginatedByUser(pageNo.intValue(), recordPerPage, new FeedBack(), null, user, "user_id");
            Long count = feedBackService.countByUser(user, "user");
            model.addAttribute("feedbackList", feedBackList);
            model.addAttribute("user", user);
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
            model.addAttribute("listType", "given");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "user/feedback/list";

    }

    /**
     * @param type               user id
     * @param reportedUser       user id who is gettign
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = "/report/{type}")
    public String reportUser(
            @PathVariable String type, ReportedUser reportedUser,
            ModelMap model, RedirectAttributes redirectAttributes) {

        Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
        User reportUser = userService.find(id);
        User currentUser = serviceUtil.getCurrentUser();

        model.addAttribute("user", reportUser);
        model.addAttribute("currentUser", currentUser);
        return "user/reported/create";
    }


    /**
     * @param reportedUser
     * @param result
     * @param type
     * @param redirectAttributes
     * @param model
     * @return
     */
    @PostMapping(value = "/report/{type}")
    public String reportUserSubmit(@ModelAttribute("reportedUser") @Valid ReportedUser reportedUser,
                                   BindingResult result,
                                   @PathVariable String type,
                                   RedirectAttributes redirectAttributes,
                                   ModelMap model) {
        try {
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            User reportUser = userService.find(id);
            User currentUser = serviceUtil.getCurrentUser();

            model.addAttribute("user", reportUser);
            model.addAttribute("currentUser", currentUser);
            if (result.hasErrors()) {
                alerts.setError("General.error.msg");
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
                return "user/reported/create";
            }

            User user = userService.find(id);
            reportedUser.setUser(serviceUtil.getCurrentUser());
            reportedUser.setReportedUser(user);
            reportedUserService.save(reportedUser);
            String[] args = new String[1];
            args[0] = reportedUser.getReportedUser().getUsername();
            alerts.setSuccess("Report.user.save.success", args);
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();

        } catch (Exception e) {
            alerts.setError("Report.user.save.error");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/user/report/" + type;
        }
        return "redirect:/user/profile/" + reportedUser.getReportedUser().getUsername();
    }


    /**
     * @param page               page no
     * @param maxCount           record per page
     * @param search             search string
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = {"/reported/users", "/reported/users/{page}", "/reported/users/{page}/{maxCount}"})
    public String reportedUserList(
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            ModelMap model, RedirectAttributes redirectAttributes) {
        Integer pageNo = 1;
        /*AdminConfigUtil<ReportedUser> adminConfigUtil = new AdminConfigUtil<ReportedUser>();
        AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new ReportedUser());
        Integer recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();*/
        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new ReportedUser()) : maxCount.get();

        if (page.isPresent()) {
            pageNo = page.get();
        }
        try {
            User user = serviceUtil.getCurrentUser();
            List<ReportedUser> reportedUsers = reportedUserService.findPaginatedByUser(pageNo.intValue(), recordPerPage, new ReportedUser(), "", serviceUtil.getCurrentUser());
            Long count = reportedUserService.countByUser(user);
            model.addAttribute("reportedUsersList", reportedUsers);
            model.addAttribute("user", user);
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "user/reported/list";

    }

    /**
     * @param type
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = "/reported/view/{type}")
    public String reportedUserView(@PathVariable String type, ModelMap model, RedirectAttributes redirectAttributes) {
        ReportedUser reportedUser = null;
        try {
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            reportedUser = reportedUserService.find(id);
            model.addAttribute("reportedUser", reportedUser);
            model.addAttribute("title", reportedUser.getReportedUser().getUsername());

        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
        }
        return "user/reported/view";
    }

    /**
     * @param type
     * @param model
     * @param redirectAttributes
     * @return
     */
    @GetMapping(value = "/feedback/view/{type}")
    public String feedBackView(@PathVariable String type, ModelMap model, RedirectAttributes redirectAttributes) {
        FeedBack feedBack = null;
        try {
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            feedBack = feedBackService.find(id);
            model.addAttribute("feedBack", feedBack);
            model.addAttribute("title", feedBack.getUserTo().getUsername());

        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
        }
        return "user/feedback/view";
    }


    @GetMapping(value = "/settings")
    public String userSettings(
            ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = serviceUtil.getCurrentUser();
            model.addAttribute("user", currentUser);
            model.addAttribute("title", currentUser.getUsername() + " Settings");
            model.addAttribute("currencies", currencyService.findAll());
            model.addAttribute("countries", countriesService.findAll());
            model.addAttribute("zones", zonesService.findAll());


        } catch (Exception e) {

        }
        return "user/settings/userform";
    }

    @PostMapping(value = "/settings")
    public String userEdit(
            @ModelAttribute("user") User user,
            BindingResult result,
            ModelMap model, RedirectAttributes redirectAttributes) {
        try {


            User currentUser = serviceUtil.getCurrentUser();
            User dbUser = userService.find(currentUser.getId());
            if (dbUser != null) {
                dbUser.setFirstName(user.getFirstName());
                dbUser.setLastName(user.getLastName());
                dbUser.setPhone(user.getPhone());
                dbUser.setCurrency(user.getCurrency());
                dbUser.setCountry(user.getCountry());
                dbUser.setZone(user.getZone());
                dbUser.setSellingVacation(user.isSellingVacation());
                dbUser.setBuyingVacation(user.isBuyingVacation());
                dbUser.setAccountDeleted(user.isAccountDeleted());
                dbUser.setEscrowSms(user.isEscrowSms());
                dbUser.setSendNewTradeSms(user.isSendNewTradeSms());
                dbUser.setEnableWebNotification(user.isEnableWebNotification());
                dbUser.setConfirmPassword(dbUser.getPassword());
                dbUser.setDisableSensitiveInformationFromEmail(user.isDisableSensitiveInformationFromEmail());
                Set<ConstraintViolation<User>> violations = validator.validate(dbUser);
                if (violations.size() <= 0) {
                    userService.saveOrUpdate(dbUser);
                    alerts.setSuccess("User.save.success");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                } else {

                    for (ConstraintViolation violation : violations) {
                        result.rejectValue(violation.getPropertyPath().toString(), "", violation.getMessage());
                    }
                    alerts.setError("General.error.msg");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                    //return "redirect:/user/settings";
                }
            } else {
                alerts.setError("User.not.found");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/home";
            }
            model.addAttribute("user", user);
            model.addAttribute("title", currentUser.getUsername() + " Settings");
            model.addAttribute("currencies", currencyService.findAll());
            model.addAttribute("countries", countriesService.findAll());
            model.addAttribute("zones", zonesService.findAll());


        } catch (Exception e) {
            alerts.setError("User.save.error");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            logger.error(e.getMessage());
        }
        return "user/settings/userform";
    }


    @RequestMapping(value = {"/ga/{type}", "/ga"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String topt(@PathVariable Optional<String> type, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String requestType = "";
        try {
            if (type.isPresent()) {
                requestType = type.get();
            }
            User currentUser = serviceUtil.getCurrentUser();
            //userService.refresh(currentUser);

            String secretKey;
            String url = "";

            if (requestType.equalsIgnoreCase("save")) {
                currentUser.setGoogleAuthenticatorKey(request.getParameter("secretkey"));
                userService.saveOrUpdate(currentUser);
                secretKey = currentUser.getGoogleAuthenticatorKey();
                url = GoogleAuthenticatorKey.getQRBarcodeURL(currentUser.getUsername(), currentUser.getEmail(), secretKey);
            } else if (requestType.equalsIgnoreCase("disable")) {
                String codeParameter = (!Common.isDouble(request.getParameter("code"))) ? "0" : request.getParameter("code");
                int code = Integer.parseInt(codeParameter);
                GoogleAuthenticator ga = new GoogleAuthenticator();
                GoogleAuthenticatorKey gk = ga.createCredentials();
                secretKey = gk.getKey();
                url = GoogleAuthenticatorKey.getQRBarcodeURL(currentUser.getUsername(), currentUser.getEmail(), secretKey);
                if (code != 0 && currentUser.getGoogleAuthenticatorKey() != null) {
                    if (ga.authorize(currentUser.getGoogleAuthenticatorKey(), code)) {
                        currentUser.setGoogleAuthenticatorKey(null);
                        userService.saveOrUpdate(currentUser);
                    } else {
                        alerts.setError("Please enter valid code");
                        secretKey = currentUser.getGoogleAuthenticatorKey();
                        url = GoogleAuthenticatorKey.getQRBarcodeURL(currentUser.getUsername(), currentUser.getEmail(), secretKey);
                    }
                } else {
                    alerts.setError("Please enter valid code");
                }
            } else if (requestType.equalsIgnoreCase("change")) {
                int code = Integer.parseInt(request.getParameter("code"));
                GoogleAuthenticator ga = new GoogleAuthenticator();
                GoogleAuthenticatorKey gk = ga.createCredentials();
                secretKey = gk.getKey();
                url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(currentUser.getUsername(), currentUser.getEmail(), gk);
                if (code != 0 && currentUser.getGoogleAuthenticatorKey() != null) {
                    if (ga.authorize(currentUser.getGoogleAuthenticatorKey(), code)) {
                        currentUser.setGoogleAuthenticatorKey(secretKey);
                        userService.saveOrUpdate(currentUser);
                    } else {
                        alerts.setError("Please enter valid code");
                        secretKey = currentUser.getGoogleAuthenticatorKey();
                        url = GoogleAuthenticatorKey.getQRBarcodeURL(currentUser.getUsername(), currentUser.getEmail(), secretKey);
                    }
                } else {
                    alerts.setError("Please enter valid code");
                }

            } else if (requestType.equalsIgnoreCase("verify")) {
                if (currentUser.getGoogleAuthenticatorKey() != null) {
                    int code = 0;
                    try {
                        code = Integer.parseInt(request.getParameter("code"));
                    } catch (Exception e) {
                        logger.error(e.getMessage() + e.getStackTrace());
                    }
                    GoogleAuthenticator ga = new GoogleAuthenticator();
                    Boolean val = ga.authorize(currentUser.getGoogleAuthenticatorKey(), code);
                    if (val) {
                        alerts.setSuccess("Verification successful");
                    } else {
                        alerts.setError("Verification failed");
                    }
                    secretKey = currentUser.getGoogleAuthenticatorKey();
                    url = GoogleAuthenticatorKey.getQRBarcodeURL(currentUser.getUsername(), currentUser.getEmail(), secretKey);
                } else {
                    alerts.setError("Please save your security key first by clicking on save button");
                    GoogleAuthenticator ga = new GoogleAuthenticator();
                    GoogleAuthenticatorKey gk = ga.createCredentials();
                    secretKey = gk.getKey();
                    url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(currentUser.getUsername(), currentUser.getEmail(), gk);
                }
            } else if (currentUser.getGoogleAuthenticatorKey() != null && !currentUser.getGoogleAuthenticatorKey().isEmpty()) {
                secretKey = currentUser.getGoogleAuthenticatorKey();
                url = GoogleAuthenticatorKey.getQRBarcodeURL(currentUser.getUsername(), currentUser.getEmail(), secretKey);
            } else {
                GoogleAuthenticator ga = new GoogleAuthenticator();
                GoogleAuthenticatorKey gk = ga.createCredentials();
                secretKey = gk.getKey();
                url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(currentUser.getUsername(), currentUser.getEmail(), gk);
            /*currentUser.setGoogleAuthenticatorKey(secretKey);
            userService.saveOrUpdate(currentUser);*/

            }
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            model.addAttribute("secretkey", secretKey);
            model.addAttribute("user", currentUser);
            model.addAttribute("url", url);

        } catch (CoinmartException e) {
            logger.error(e.getMessage() + e.getStackTrace());
            model.addAttribute("secretkey", "");
            model.addAttribute("user", "");
            model.addAttribute("url", "");
            alerts.setError("General.error.msg");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
        }
        return "user/settings/ga";
    }


    @GetMapping(value = "/forgot")
    public String forgotPasswordForm(ModelMap model) {
        model.addAttribute("user", new User());
        return "user/fpassword";
    }

    @RequestMapping(value = "/forgot/password", method = RequestMethod.POST)
    public String resetPassword(@ModelAttribute User userForm, HttpServletRequest request, ModelMap model, RedirectAttributes redirectAttributes) {
        if (userForm.getEmail() != null && !userForm.getEmail().isEmpty()) {
            try {
                User user = serviceUtil.getUserFromUsernameOrEmail(userForm.getEmail());
                if (user == null) {
                    alerts.setError("User not found");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                } else if (user != null && user.getParent() != null) {
                    alerts.setError("You are not allowed to change the password! Ask your parent user to change the password");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                } else {
                    String token = UUID.randomUUID().toString();
                    String appUrl = request.getContextPath();
                    PasswordResetToken passwordResetToken = new PasswordResetToken();
                    passwordResetToken.setUser(user);
                    passwordResetToken.setToken(token);
                    passwordResetService.save(passwordResetToken);
                    eventPublisher.publishEvent(new PasswordResetCompleteEvent(user, localeHelper.getCurrentLocale(), appUrl, token));
                    alerts.setSuccess("Reset email sent");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                }
                //return "redirect:/login";
            } catch (Exception e) {
                alerts.setError("Error sending email");
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
                logger.error(e.getMessage() + e.getStackTrace());
            }
        } else {
            alerts.setError("Please enter valid email id");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/user/forgot";
        }
        return "user/fpassword";
    }

    @RequestMapping(value = "/password/reset/verification/{token}", method = RequestMethod.GET)
    public String resetPassword(@PathVariable String token, ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            Object[] params = new Object[1];
            params[0] = token;
            PasswordResetToken passwordResetToken = passwordResetService.first("from PasswordResetToken where token=?", params);
            if (passwordResetToken != null) {
                User user = userService.find(passwordResetToken.getUser().getId());
                if (user != null) {
                    model.addAttribute("user", new User());
                    model.addAttribute("token", token);
                    return "user/resetpassword";
                } else {
                    alerts.setError("User not found");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                }
            } else {
                alerts.setError("Token not found");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "redirect:/reg";
    }


    @RequestMapping(value = "/password/reset/verification/{token}", method = RequestMethod.POST)
    public String resetPassword(@ModelAttribute User user, @PathVariable String token, ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            Object[] params = new Object[1];
            params[0] = token;
            PasswordResetToken passwordResetToken = passwordResetService.first("from PasswordResetToken where token=?", params);
            if (passwordResetToken != null) {
                User userDb = userService.find(passwordResetToken.getUser().getId());
                if (userDb != null) {
                    userDb.setPassword(passwordEncoder.encode(user.getPassword()));
                    userService.saveOrUpdate(userDb);
                    alerts.setSuccess("Password reset successful");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                    return "user/resetpasswordsuccess";
                } else {
                    alerts.setError("User not found");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                }
            } else {
                alerts.setError("Token not found");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();

            }
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return "redirect:/reg";
    }


    @RequestMapping(value = {"/phone/verification", "/phone/verification/{type}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String phoneVerification(@PathVariable Optional<String> type, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String caseType = "";
        String returnType="";
        if (type.isPresent()) {
            caseType = type.get();
            returnType = caseType;
        }

        User currentUser = serviceUtil.getCurrentUser();
        switch (caseType) {
            case "":
                break;
            case "send":
            case "resend":
                if (
                        currentUser.getCountryCode() != null &&
                                currentUser.getPhone() != null &&
                                !currentUser.getCountryCode().isEmpty() &&
                                !currentUser.getCountryCode().equalsIgnoreCase("") &&
                                !currentUser.getPhone().isEmpty() &&
                                !currentUser.getPhone().equalsIgnoreCase("")
                        ) {
                    Boolean res = serviceUtil.generateOtp(currentUser);
                    if (res) {
                        String[] args = new String[1];
                        args[0] = currentUser.getPhone();
                        alerts.setSuccess("User.phone.verification.otp.sent", args);
                    } else {
                        alerts.setError("User.phone.verification.error");
                        returnType="";
                    }

                } else {
                    alerts.setError("User.phone.verification.phone.error");
                    returnType="";
                }
                break;
            case "submit":
                if (serviceUtil.validateOtp(currentUser, request.getParameter("verification_code"))) {
                    alerts.setSuccess("User.phone.verification.successful");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                    return "redirect:/home";
                } else {
                    alerts.setError("User.phone.verification.otp.invalid");
                }
                break;
            default:
                alerts.setError("General.error.msg");
                break;
        }
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        model.addAttribute("user", serviceUtil.getCurrentUser());
        model.addAttribute("type", returnType);
        return "user/verification/phone";
    }


}
