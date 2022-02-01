/*
 * Copyright (c) 9/4/18 11:19 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.advertise;

import com.gradle.components.encrypter.PathVariableEncrypt;
import com.gradle.components.jms.MessageSender;
import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.Currency;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.forms.advertise.SearchForm;
import com.gradle.entity.user.User;
import com.gradle.enums.advertisement.AdType;
import com.gradle.services.iface.bitcoin.*;
import com.gradle.services.iface.chat.ChatFileService;
import com.gradle.services.iface.user.CountriesService;
import com.gradle.util.ActiveSessionManager;
import com.gradle.util.Common;
import com.gradle.util.Paging;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.advertise.Verify;
import com.gradle.util.constants.ConstantProperties;
import com.gradle.util.export.WriteCsvToResponse;
import com.gradle.util.websocket.WebsocketHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.io.IOException;
import java.util.*;




/**
 * This class is a controller to control advertise related actions
 */
@Controller
@RequestMapping(value = "/advertise")
public class AdvertisementController extends AbstractBaseController {
    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private PaymentTypeService paymentTypeService;


    @Autowired
    private PathVariableEncrypt pathVariableEncrypt;

    @Autowired
    ActiveSessionManager activeSessionManager;

    @Autowired
    private Validator validator;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeStatusService tradeStatusService;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private EscrowService escrowService;

    @Autowired
    private WebsocketHelper websocketHelper;

    @Autowired
    CountriesService countriesService;

    @Autowired
    private ChatFileService chatFileService;

    @Autowired
    private Verify verify;

    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif", "application/pdf"};

    private String tradeId;

    private static Logger logger = Logger.getLogger(AdvertisementController.class);

    /**
     * @param advertise           : Advertise Object
     * @param result              : Result
     * @param model               : ModelMap
     * @param redirectAttributes:
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/list", "/list/{page}", "/list/{page}/{maxCount}"}, method = RequestMethod.GET)
    public String advertiseList(
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            Advertise advertise, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) throws Exception {

        Integer pageNo = 1;

        //messageSender.sendMessage(countriesService.findAll().get(0));

        // Getting records per page configuration from admin config

        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new Advertise()) : maxCount.get();

        if (page.isPresent()) {
            pageNo = page.get();
        }
        User user = serviceUtil.getCurrentUser();
        List<Advertise> advertiseList = advertisementService.findPaginatedByUser(pageNo.intValue(), recordPerPage, new Advertise(), null, user);
        //Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH+user.getEmail()+".wallet"));
        // Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH + user.getEmail() + ".wallet"));


        Long countParent = advertisementService.countByUser(user.getParent());
        Long countChild = advertisementService.countByUser(user);
        Long count = countChild + countParent;
        User currentUser = serviceUtil.getCurrentUser();
        model.addAttribute("advertiseList", advertiseList);

        //model.addAttribute("currentWaddress", wallet.currentReceiveAddress());
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
        model.addAttribute("user", currentUser);
        model.addAttribute("maxAllowed", verify.getMaxAllowedAdvertisement(currentUser));
        model.addAttribute("count", verify.getLiveAdvertisementCount(currentUser));

        return "advertise/list";
    }


    /**
     * @param type               - Encrypted Advertisement ID
     * @param model              - ModelMap object
     * @param redirectAttributes - Redirect Attribute object
     * @return
     */
    @GetMapping(value = "/toggle/{type}")
    public String toggleAdvertiseStatus(
            @PathVariable String type,
            ModelMap model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            Advertise advertise = advertisementService.find(id);
            /*AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
            List<AdminConfigValues> adminConfigValuesList = adminConfigUtil.getAdminConfigValues(serviceUtil, new Advertise());*/

            AdminConfig adminConfig = serviceUtil.getAdminConfigAndValues(new Advertise());
            List<AdminConfigValues> adminConfigValuesList;
            String value;
            if (adminConfig != null) {
                adminConfigValuesList = adminConfig.getAdminConfigValuesList();
                value = Common.getAdminConfigValue(adminConfigValuesList, ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT, ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT_STATIC.toString());
            }else{
                value = ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT_STATIC.toString();
            }

            Integer maxAllowedAdvertise = Integer.parseInt(value);
            Long count = advertisementService.countEnabledAdvertisement(serviceUtil.getCurrentUser());
            if (count < maxAllowedAdvertise || advertise.isStatus()) {
                advertise.setStatus(!advertise.isStatus());
                advertisementService.saveOrUpdate(advertise);
                alerts.setSuccess("Advertise.status.changed");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
            } else {
                alerts.setError("Advertise.new.user.allowed.number");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
            }
        } catch (Exception e) {
            alerts.setError("Advertise.status.change.error");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
        }

        return "redirect:/advertise/list";
    }

    /**
     * @param advertise          : Advertise Object
     * @param result             : Result
     * @param model              : ModelMap
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String createAdvertiseForm(Advertise advertise, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
        User currentUser = serviceUtil.getCurrentUser();
        advertise = (model.containsKey("advertise")) ? (Advertise) model.get("advertise") : new Advertise();
        /*User user = serviceUtil.getCurrentUser();
        model.addAttribute("advertise", advertise);
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        List<Currency> currencyList = currencyService.findAll();
        model.addAttribute("currencyList", currencyList);
        model.addAttribute("paymentTypeList", paymentTypeList);
        model.addAttribute(ConstantProperties.GOOGLE_MAP_ADDRESS_API, "true");
        model.addAttribute("user", user);


        return "advertise/postad";*/
        //Advertise advertise = advertisementService.find(Integer.parseInt(pathVariableEncrypt.decrypt(id)));
        /*AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
        List<AdminConfigValues> adminConfigValuesList = adminConfigUtil.getAdminConfigValues(serviceUtil, new Advertise());
        String value = Common.getAdminConfigValue(adminConfigValuesList, ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT, ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT_STATIC.toString());
        Integer maxAllowedAdvertise = Integer.parseInt(value);
        Long count = advertisementService.countEnabledAdvertisement(serviceUtil.getCurrentUser());*/

        if (!verify.canCreateAdvertise(currentUser)) {

            alerts.setAlertRedirectAttribute(redirectAttributes);
            redirectAttributes.addFlashAttribute("advertise", advertise);
            alerts.clearAlert();
            return "redirect:/advertise/list";
        }
        if (advertise.getTimeout() == 0) advertise.setTimeout(20);
        model.addAttribute("advertise", advertise);
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        List<Currency> currencyList = currencyService.findAll();
        model.addAttribute("currencyList", currencyList);
        model.addAttribute("paymentTypeList", paymentTypeList);
        model.addAttribute(ConstantProperties.GOOGLE_MAP_ADDRESS_API, "true");
        model.addAttribute("user", serviceUtil.getCurrentUser());
        return "advertise/create";
    }

    /**
     * @param advertise          : Advertise Object
     * @param result             : Result
     * @param model              : ModelMap
     * @param redirectAttributes
     * @return
     */

    @RequestMapping(value = "/form/submit", method = RequestMethod.POST)
    public String createAdvertise(@Valid @ModelAttribute("advertise") Advertise advertise, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
        User user = serviceUtil.getCurrentUser();
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        List<Currency> currencyList = currencyService.findAll();
        AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
        adminConfigUtil.getAdminConfigValues(serviceUtil, new Advertise());
        // if validation failed, set error message and redirect
        if (result.hasErrors()) {
            alerts.setError("Advertise.saving.error");
        } else { // if no validation error save advertise and redirect with success message

            Double finalBitcoinAmount = Double.parseDouble(user.getFinalBitcoinAmount());
            Double tradeAmount = Double.parseDouble(advertise.getMaxLimit());
            Double btcRate = Double.parseDouble(advertise.getBtcRate());
            Double finalTransactionBtcAmount = finalBitcoinAmount - (btcRate / tradeAmount);
            if (finalTransactionBtcAmount > 0 || advertise.getAdType().getValue().contains("BUY")) {
                advertise.setUser(serviceUtil.getCurrentUser());
                advertisementService.saveOrUpdate(advertise);
                alerts.setSuccess("Advertise.saving.success");
            } else {
                alerts.setError("Advertise.bitcoin.not.available");
            }
        }

        model.addAttribute("currencyList", currencyList);
        model.addAttribute("paymentTypeList", paymentTypeList);
        model.addAttribute("user", user);
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        model.addAttribute(ConstantProperties.GOOGLE_MAP_ADDRESS_API, "true");
        alerts.clearAlert();
        return "advertise/postad";
    }


    /**
     * @param id
     * @param page               Page No
     * @param maxCount           Records per page
     * @param model              ModelMap
     * @param redirectAttributes
     * @param request            HttpServletRequest
     * @return
     */
    @RequestMapping(value = {
            "/sell",
            "/buy",
            "/sell/{page}/{maxCount}",
            "/buy/{page}/{maxCount}",
            "/sellonline",
            "/buyonline",
            "/sell/{id}",
            "/buy/{id}",
            "/sell/{id}/{page}/{maxCount}",
            "/buy/{id}/{page}/{maxCount}",
            "/sellonline/{id}",
            "/buyonline/{id}"
    },
            method = RequestMethod.GET)
    public String sellCoinsByType(@PathVariable Optional<Integer> id,
                                  @PathVariable Optional<Integer> page,
                                  @PathVariable Optional<Integer> maxCount,
                                  ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {


        Integer pageNo = 1;
        if (page.isPresent()) {
            pageNo = page.get();
        }
        /*Integer recordPerPage=ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
        if (maxCount.isPresent()){
            recordPerPage = maxCount.get();
        }else {
            AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<>();
            AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new Advertise());
            recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
        }*/
        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new Advertise()) : maxCount.get();
        Currency currentCurrency = serviceUtil.getCurrentCurrency(request);

        int paymentId = 0;
        if (id.isPresent()) {
            paymentId = id.get();
            if (paymentId != 0) {
                PaymentType payment = paymentTypeService.find(paymentId);
                payment.setSearchCount(payment.getSearchCount() + 1);
                paymentTypeService.saveOrUpdate(payment);
            }
        }


        String[] paths = request.getServletPath().split("/");
        List<PaymentType> paymentTypes = paymentTypeService.getPaymentTypes(7);



        // Set different type in map with keys to use later in this method
        Map<String, String> pathMap = new HashMap<>();
        pathMap.put("buy", AdType.SELL.toString());
        pathMap.put("sell", AdType.BUY.toString());


        List<Advertise> advertiseList;
        List<Advertise> advertisementBuyList;
        User currentUser = serviceUtil.getCurrentUser();
        Object[] params;
        if (paymentId != 0) {
            params = new Object[3];
            params[1] = paymentId;
            params[2] = serviceUtil.getCurrentCurrency(request).getId();
        } else {
            params = new Object[2];
            params[1] = serviceUtil.getCurrentCurrency(request).getId();
        }
        // getting all advertisement for local sell
        if (currentUser != null) {

            advertiseList = serviceUtil.getPaginatedAdvertiseByType(pathMap.get(paths[2]), (paymentId != 0) ? paymentId : 0, pageNo, recordPerPage, currentCurrency.getId());
        } else {
            advertiseList = serviceUtil.getAdvertiseByType(pathMap.get(paths[2]), (paymentId != 0) ? paymentId : 0);

        }
//        List<Advertise> advertiseList = serviceUtil.getPaginatedAdvertiseByType(pathMap.get(paths[2]), paymentId, pageNo, recordPerPage, 0);

        params[0] = pathMap.get(paths[2]);
        String additionalCondition = (paymentId != 0) ? " and payment_type_id=?" : "";
        additionalCondition+=" and currency_id=?";

        Long count = advertisementService.countQuery("select count(*) from Advertise where advertisement_type=?" + additionalCondition, params);




        AdType adType = (pathMap.get(paths[2]).equalsIgnoreCase("sell")) ? AdType.SELL : AdType.BUY;
        List<Advertise> advertiseListCount = advertisementService.countByPaymentType("paymentType", adType, "currency", currentCurrency);


        model.addAttribute("listType", paths[2]);
        model.addAttribute("advertisementList", advertiseList);
        model.addAttribute("searchPaymentTypes", paymentTypes);
        model.addAttribute("paymentTypeList", paymentTypes);
        model.addAttribute("pagePath", paths[2]);
        model.addAttribute("verify", new Verify());
        model.addAttribute("advertiseListCount", advertiseListCount);
        model.addAttribute("user", serviceUtil.getCurrentUser());
        model.addAttribute("ptypeId", paymentId);
        model.addAttribute("button", (pathMap.get(paths[2]).contains("BUY")) ? "Sell" : "Buy");
        model.addAttribute("currencies", currencyService.findAll());
        /*model.addAttribute("ptypecount", listpaymentTypeCount);*/
        if (paymentId != 0) {
            String urlAry[] = {"/advertise/" + paths[2] + "/" + paymentId + "/"};
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue(), urlAry));
        } else
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));

        return "advertise/buysell";
    }


    /**
     * @param advertise          : Advertise Objectƒ
     * @param result             : Result
     * @param model              : ModelMap
     * @param redirectAttributes
     * @return
     */

    @RequestMapping(value = "/edit/submit", method = RequestMethod.POST)
    public String editAdvertise(@ModelAttribute("advertise") Advertise advertise, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
        AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
        List<AdminConfigValues> adminConfigValuesList = adminConfigUtil.getAdminConfigValues(serviceUtil, new Advertise());
        Integer maxAllowedAdvertise = ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT_STATIC;
        if (adminConfigValuesList.stream().filter(o -> o.getName().equalsIgnoreCase(ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT)).findFirst().isPresent()) {
            AdminConfigValues adminConfigValue = adminConfigValuesList.stream().filter(o -> o.getName().equalsIgnoreCase(ConstantProperties.NEW_USER_ALLOWED_ADVERTISEMENT)).findFirst().get();
            maxAllowedAdvertise = Integer.parseInt(adminConfigValue.getValue());

        }
        User user = serviceUtil.getCurrentUser();
        if (advertise.getId() <= 0) {
            advertise.setAdvertisementId(serviceUtil.getNewAdvertisementId());
            Integer nextAdvertiseSequence = Integer.parseInt(serviceUtil.getLastAdvertisementSequenceId()) + 1;
            advertise.setAdvertisementSequenceId(nextAdvertiseSequence.toString());
        }

        Set<ConstraintViolation<Advertise>> violations = validator.validate(advertise);

        if (violations.size() > 0) {

            for (ConstraintViolation violation : violations) {
                result.rejectValue(violation.getPropertyPath().toString(), "", violation.getMessage());
            }

            alerts.setError("Advertise.saving.error");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            model.addAttribute("advertise", advertise);
            List<PaymentType> paymentTypeList = paymentTypeService.findAll();
            List<Currency> currencyList = currencyService.findAll();
            model.addAttribute("currencyList", currencyList);
            model.addAttribute("paymentTypeList", paymentTypeList);
            model.addAttribute(ConstantProperties.GOOGLE_MAP_ADDRESS_API, "true");
            model.addAttribute("edit", true);
            model.addAttribute("user", serviceUtil.getCurrentUser());
            return "advertise/create";
        } else {
            try {


                Long count = advertisementService.countEnabledAdvertisement(serviceUtil.getCurrentUser());
                if (count < maxAllowedAdvertise || advertise.getId() > 0) {


                    /*NumberFormat format = NumberFormat.getInstance(Locale.US);
                    Double finalBitcoinAmount = serviceUtil.getCurrentUserBalance();*/
                    advertise.setUser(user);
                    /*Double tradeAmount = serviceUtil.getAdvertiseMaxLimit(advertise);
                    Double btcRate = Double.parseDouble(format.parse(advertise.getBtcRate()).toString());
                    Double finalTransactionBtcAmount = finalBitcoinAmount - (tradeAmount / btcRate);
                    Double maxAllowedAdvertiseAmount = serviceUtil.getAdvertiseMaxLimit(advertise);*/
                    if (serviceUtil.isAdvertiseAllowed(advertise) || advertise.getAdType().getValue().contains("Buy")) {
                        advertise.setUser(serviceUtil.getCurrentUser());
                        if (count < maxAllowedAdvertise)
                            advertisementService.saveOrUpdate(advertise);
                        else {
                            Advertise advertiseDb = advertisementService.find(advertise.getId());
                            advertise.setStatus(advertiseDb.isStatus());
                            advertise.setHidden(advertiseDb.isHidden());
                            advertisementService.saveOrUpdate(advertise);
                        }
                        alerts.setSuccess("Advertise.saving.success");
                    } else {
                        alerts.setError("Advertise.bitcoin.not.available");
                        alerts.setAlertRedirectAttribute(redirectAttributes);
                        redirectAttributes.addFlashAttribute("advertise", advertise);
                        alerts.clearAlert();
                        return "redirect:/advertise/form";
                    }
                } else {
                    alerts.setError("Advertise.new.user.allowed.number");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    redirectAttributes.addFlashAttribute("advertise", advertise);
                    alerts.clearAlert();
                    return "redirect:/advertise/form";
                }
            } /*catch (ParseException e) {
                logger.error(e.getMessage());
                alerts.setError("Advertise.saving.error");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/advertise/form";
            }*/ catch (NumberFormatException ne) {
                logger.error(ne.getMessage());
                alerts.setError("Advertise.saving.error");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/advertise/form";
            }
        }
        try {
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            advertise.setUser(serviceUtil.getCurrentUser());
            advertisementService.saveOrUpdate(advertise);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return "redirect:/advertise/list";
    }


    /**
     * @param id                 : Advertisement id to be edited--  640200
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editAdvertiseForm(@PathVariable("id") String id, ModelMap model, RedirectAttributes redirectAttributes) {
        Advertise advertise = advertisementService.find(Integer.parseInt(pathVariableEncrypt.decrypt(id)));
        advertise.setVisibleId(id);
        model.addAttribute("advertise", advertise);
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        List<Currency> currencyList = currencyService.findAll();
        model.addAttribute("currencyList", currencyList);
        model.addAttribute("paymentTypeList", paymentTypeList);
        model.addAttribute("user", serviceUtil.getCurrentUser());
        model.addAttribute("edit", true);
        model.addAttribute(ConstantProperties.GOOGLE_MAP_ADDRESS_API, "true");
        return "advertise/create";
    }


    /**
     * @param id                 : Advertisement id to be deleted
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteAdvertise(@PathVariable("id") String id, ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            Advertise advertise = advertisementService.find(Integer.parseInt(pathVariableEncrypt.decrypt(id)));
            advertisementService.delete(advertise);
            alerts.setSuccess("Advertise.delete.success");
        } catch (Exception e) {
            logger.error(e.getMessage() + e.getStackTrace());
            alerts.setError("Advertise.delete.error");
        }
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/advertise/list";
    }


    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }


    /**
     * @param model              : ModelMap object
     * @param redirectAttributes : Redirect attributes object
     * @param response           : will generate csv file with advertise records for logged in user
     */
    @GetMapping(value = "/download/csv", produces = "text/csv")
    @ResponseBody
    public void advertiseDownload(ModelMap model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        List<Advertise> advertiseList = advertisementService.findAll();
        try {
            WriteCsvToResponse<Advertise> csvWriter = new WriteCsvToResponse<Advertise>();
            String[] columns = new String[9];
            columns[0] = "id";
            columns[1] = "advertisetype";
            columns[2] = "username";
            columns[3] = "min";
            columns[4] = "max";
            columns[5] = "btcrate";
            columns[6] = "paymenttypename";
            columns[7] = "currencyname";
            columns[8] = "term";
            csvWriter.writeCsv(response, advertiseList, columns);
        } catch (IOException e) {

        }
    }


    /**
     * @param searchForm         Advertise search form
     * @param result
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value = {"/search", "/search/{page}", "/seaerch/{page}/{maxCount}"})
    public String advertiseSearch(@ModelAttribute("searchForm") SearchForm searchForm,
                                  @PathVariable Optional<Integer> page,
                                  @PathVariable Optional<Integer> maxCount,
                                  BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        try {

            Integer pageNo = 1;
            if (page.isPresent()) {
                pageNo = page.get();
            }
            Cookie cookie =new Cookie(ConstantProperties.COOKIE_DEFAULT_CURRENCY, currencyService.find(searchForm.getCurrencyId()).getCurrencyCode());
            cookie.setPath("/");

            response.addCookie(cookie);
            /*Integer recordPerPage=ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
            if (maxCount.isPresent()){
                recordPerPage = maxCount.get();
            }else {
                AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<>();
                AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new Advertise());
                recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
            }*/
            Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new Advertise()) : maxCount.get();

            String type = (searchForm.getType().contains("Buy") ? "SELL" : "BUY");
            List<Advertise> advertiseList = serviceUtil.getPaginatedAdvertiseByType(type, searchForm.getPaymentTypeId(), pageNo, recordPerPage, searchForm.getCurrencyId());
            List<Object> conditionParams = new ArrayList<>();
            conditionParams.add(type);
            Object[] params;
            if (searchForm.getPaymentTypeId() != 0) {
                conditionParams.add(searchForm.getPaymentTypeId());
            }
            if (searchForm.getCurrencyId() != 0) {
                conditionParams.add(searchForm.getCurrencyId());
            }

            params = conditionParams.toArray();
            String additionalCondition = (searchForm.getPaymentTypeId() != 0) ? " and payment_type_id=?" : "";
            additionalCondition += (searchForm.getCurrencyId() > 0) ? " and currency_id=? " : "";
            Long count = advertisementService.countQuery("select count(*) from Advertise where advertisement_type=?" + additionalCondition, params);

            /*Object[] params = new Object[4];
            params[0] = searchForm.getCurrencyId();
            params[1] = searchForm.getPaymentTypeId();
            params[2] = (searchForm.getType().contains("Buy") ? "SELL" : "BUY");
            params[3] = (searchForm.getType().contains("Buy") ? "SELL" : "BUY");
            *//*params[3] = (searchForm.getType().contains("Buy") ? "SELL_ONLINE" : "BUY_ONLINE");*//*
            String query = " from Advertise where currency_id=? and payment_type_id=? and (advertisement_type=? or advertisement_type=?) ";
            List<Advertise> advertiseList = advertisementService.queryWithParameter(query, params);*/
            List<PaymentType> paymentTypes = paymentTypeService.findAll();


            Currency currency = currencyService.find(searchForm.getCurrencyId());
            AdType adType = (type.equalsIgnoreCase("sell")) ? AdType.SELL : AdType.BUY;
            List<Advertise> advertiseListCount = advertisementService.countByPaymentType("paymentType", adType, "currency", currency);

            model.addAttribute("listType", searchForm.getType().toLowerCase());
            model.addAttribute("advertisementList", advertiseList);
            model.addAttribute("searchPaymentTypes", paymentTypeService.getPaymentTypes(7));
            model.addAttribute("paymentTypeList", paymentTypes);
            model.addAttribute("pagePath", searchForm.getType().toLowerCase());
            model.addAttribute("verify", new Verify());
            model.addAttribute("user", serviceUtil.getCurrentUser());
            model.addAttribute("ptypeId", searchForm.getPaymentTypeId());
            model.addAttribute("button", searchForm.getType());
            model.addAttribute("currencies", currencyService.findAll());
            model.addAttribute("searchCurrency", currency);

            model.addAttribute("advertiseListCount", advertiseListCount);
            if (searchForm.getPaymentTypeId() != 0) {
                String urlAry[] = {"/advertise/" + type + "/" + searchForm.getPaymentTypeId() + "/"};
                model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue(), urlAry));
            } else
                model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertModelAttribute(model);
            alerts.clearAlert();
            return "redirect:/home";
        }
        return "advertise/buysell";
    }


    /**
     * @param searchForm         Submitted advertise search form
     * @param result
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value = "/search/advertiser")
    public String searchAdvertiser(@ModelAttribute("searchForm") SearchForm searchForm, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
        User advertiser = serviceUtil.getUserFromUsernameOrEmail(searchForm.getAdvertiser());
        if (advertiser != null) {
            try {
                String query = " from Advertise where user_id=? ";
                Object[] params = new Object[1];
                params[0] = advertiser.getId();
                List<Advertise> advertiseList = advertisementService.queryWithParameter(query, params);

                model.addAttribute("advertisementList", advertiseList);
                model.addAttribute("user", serviceUtil.getCurrentUser());
                model.addAttribute("verify", new Verify());
                model.addAttribute("advertiser", advertiser);
                model.addAttribute("currencies", currencyService.findAll());
                model.addAttribute("paymentTypeList", paymentTypeService.findAll());

                return "advertise/searchuser";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            alerts.setError("Advertiser not found");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/home";
        }
        return null;
    }
}