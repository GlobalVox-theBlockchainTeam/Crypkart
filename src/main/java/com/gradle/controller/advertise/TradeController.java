/*
 * Copyright (c) 30/4/18 12:34 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.advertise;

import com.gradle.components.encrypter.PathVariableEncrypt;
import com.gradle.components.jms.MessageSender;
import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.PaymentType;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.advertisement.TradeStatus;
import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.msg.ChatFiles;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.entity.msg.ChatMessage;
import com.gradle.entity.msg.OutputMessage;
import com.gradle.entity.user.User;
import com.gradle.enums.advertisement.AdType;
import com.gradle.exception.handler.chat.ChatFileNotFoundException;
import com.gradle.services.iface.bitcoin.*;
import com.gradle.services.iface.chat.ChatFileService;
import com.gradle.services.iface.user.CountriesService;
import com.gradle.util.ActiveSessionManager;
import com.gradle.util.Common;
import com.gradle.util.Paging;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.advertise.TradeProcess;
import com.gradle.util.advertise.Verify;
import com.gradle.util.constants.ConstantProperties;
import com.gradle.util.export.WriteCsvToResponse;
import com.gradle.util.websocket.WebsocketHelper;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * This class is a controller to control advertise related actions
 */
@Controller
@RequestMapping(value = "/trade")
public class TradeController extends AbstractBaseController {
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

    private static Logger logger = Logger.getLogger(TradeController.class);


    /**
     * @param id                 : Advertisement type id to display specific advertisement
     * @param model
     * @param redirectAttributes
     * @param request
     * @return
     */
    @RequestMapping(value = {"/sell", "/buy", "/sellonline", "/buyonline", "/sell/{id}", "/buy/{id}", "/sellonline/{id}", "/buyonline/{id}"}, method = RequestMethod.GET)
    public String sellCoinsByType(@PathVariable Optional<Integer> id, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

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


        List<Advertise> advertiseList = serviceUtil.getAdvertiseByType(pathMap.get(paths[2]), paymentId);
        model.addAttribute("listType", paths[2]);
        model.addAttribute("advertisementList", advertiseList);
        model.addAttribute("searchPaymentTypes", paymentTypes);
        model.addAttribute("paymentTypeList", paymentTypes);
        model.addAttribute("pagePath", paths[2]);
        model.addAttribute("verify", new Verify());
        model.addAttribute("user", serviceUtil.getCurrentUser());
        model.addAttribute("ptypeId", paymentId);
        model.addAttribute("button", (pathMap.get(paths[2]).contains("BUY")) ? "Sell" : "Buy");
        model.addAttribute("currencies", currencyService.findAll());

        return "advertise/buysell";
    }


    /**
     * @param id                 advertisement id under which we are going to create trade
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String tradeHome(@PathVariable String id, ModelMap model, RedirectAttributes redirectAttributes) {

        User user = serviceUtil.getCurrentUser();
        /*Verify verify = new Verify();*/

        int advertiseId = 0;
        advertiseId = Integer.parseInt(pathVariableEncrypt.decrypt(id));
        Advertise advertise = advertisementService.find(advertiseId);

        if (advertise != null) {
            if (!verify.canTrade(advertise, user) && advertise.getUser().getId() != user.getId()) {
                alerts.setError("trade.not.allowed");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/home";
            }
        }

        User tradeUser = userService.find(advertise.getUser().getId());


        Map<String, String> pathMap = new HashMap<>();
        pathMap.put("buy", AdType.SELL.toString());
        pathMap.put("sell", AdType.BUY.toString());
        /*pathMap.put("buyonline", AdType.SELL_ONLINE.toString());
        pathMap.put("sellonline", AdType.BUY_ONLINE.toString());*/

        Trade trade = model.containsAttribute("trade") ? (Trade) model.get("trade") : new Trade();
        trade.setAdvertise(advertise);
        trade.setTrader(tradeUser);
        trade.setUser(user);
        model.addAttribute("trade", trade);
        String[] feedbacks = serviceUtil.getUserFeedbacks(tradeUser);
        model.addAttribute("feedbacks", feedbacks);
        Integer totalFeedbacks = Integer.parseInt(feedbacks[0]) + Integer.parseInt(feedbacks[1]) + Integer.parseInt(feedbacks[2]);
        model.addAttribute("totalFeedbacks", totalFeedbacks);
        //model.addAttribute("button" , )
        return "trade/sell";
    }


    /**
     * @param id                 User id
     * @param model
     * @param redirectAttributes
     * @return
     */

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String tradeUser(@PathVariable Optional<String> id, ModelMap model, RedirectAttributes redirectAttributes) {
        int userId = 0;
        if (id.isPresent()) {
            userId = Integer.parseInt(pathVariableEncrypt.decrypt(id.get()));
        }
        User user = userService.find(userId);

        String query = " from Advertise where user_id = ?";
        Object[] params = new Object[1];
        params[0] = user.getId();
        List<Advertise> advertiseList = advertisementService.queryWithParameter(query, params);


        model.addAttribute("user", user);
        model.addAttribute("advertiseList", advertiseList);

        return "trade/user";
    }


    /**
     * @param type               encrypted trade id
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/process/{type}", method = RequestMethod.GET)
    public String processTrade(@PathVariable Optional<String> type, ModelMap model, RedirectAttributes redirectAttributes) {


        int tradeId = 0;
        Trade trade;
        if (type.isPresent()) {
            tradeId = Integer.parseInt(pathVariableEncrypt.decrypt(type.get()));
            trade = tradeService.find(tradeId);
            if (trade != null && trade.getUser().getId() != serviceUtil.getCurrentUser().getId() && trade.getAdvertise().getUser().getId() != serviceUtil.getCurrentUser().getId()) {
                alerts.setError("Trade.not.authorized");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/trade/list";
            }

        } else {
            alerts.setError("Trade not available you are trying to view");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/trade/list";
        }

        User user = serviceUtil.getCurrentUser();


        int advertiseId = trade.getAdvertise().getId();
        String advertiseType = trade.getAdvertise().getAdType().getValue();
        advertiseType = (advertiseType.contains("Sell")) ? "Sell" : "Buy";

        int paymentReceiver = (advertiseType.contains("Sell")) ? trade.getAdvertise().getUser().getId() : trade.getUser().getId();
        int paymentSender = (advertiseType.contains("Buy")) ? trade.getAdvertise().getUser().getId() : trade.getUser().getId();
        int currentUserId = serviceUtil.getCurrentUser().getId();
        String userType = (paymentReceiver == currentUserId) ? "seller" : "buyer";

        User tradeUser;
        if (trade.getTrader().getId() == user.getId()) {
            tradeUser = userService.find(trade.getUser().getId());
        } else {
            tradeUser = userService.find(trade.getTrader().getId());
        }

        Set<String> activeUsers = activeSessionManager.getAllExceptCurrentUser(user.getUsername());
        Advertise advertise = advertisementService.find(trade.getAdvertise().getId());

        ChatHistory chatMessage = serviceUtil.getChatHistoryForTrade(user.getId(), tradeUser.getId(), trade.getId());
        List<ChatFiles> chatFilesList = chatFileService.getTradeUserFiles(trade.getId(), user.getId(), tradeUser.getId(), "id", "DESC");
        List<ChatMessage> chatList = null;
        if (chatMessage == null) {
            chatMessage = new ChatHistory();
        } else {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                TypeReference<List<ChatMessage>> mapType = new TypeReference<List<ChatMessage>>() {

                };
                chatList = objectMapper.readValue(chatMessage.getFullText(), mapType);
                if (chatList != null && chatList.size() > 0) {
                    chatList.sort(Comparator.comparing(ChatMessage::getTime).reversed());
                }
            } catch (IOException e) {
                logger.error(" Error reading chat history : " + e.getMessage());
            }
        }


        model.addAttribute("chatFilesList", chatFilesList);
        model.addAttribute("trade", trade);
        //model.addAttribute("advertise", advertise);
        model.addAttribute("user", user);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("tradeUser", tradeUser);
        model.addAttribute("online", (activeUsers.contains(tradeUser.getUsername())));
        model.addAttribute("chatMessage", chatMessage);
        model.addAttribute("chatList", chatList);
        model.addAttribute("paymentReceiver", paymentReceiver);
        model.addAttribute("paymentSender", paymentSender);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("userType", userType);

        return "trade/process";
    }


    /**
     * @param trade              Submitted trade object
     * @param result             Binding results
     * @param model
     * @param redirectAttributes
     * @param request
     * @return
     */
    @RequestMapping(value = "/create/{type}", method = RequestMethod.POST)
    public String createTrade(@ModelAttribute("trade") @Valid Trade trade, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {


        AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<Advertise>();
        /*AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new Advertise());
        List<AdminConfigValues> AdvertiseConfig = adminConfigUtil.getAdminConfigValues(serviceUtil, new Advertise());*/
        Advertise advertise = trade.getAdvertise();
        try {
            if (result.hasErrors()) {
                User tradeUser = userService.find(trade.getAdvertise().getUser().getId());
                User user = serviceUtil.getCurrentUser();
                trade.setUser(user);
                trade.setTrader(tradeUser);
                alerts.setError("Trade.saving.error");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
                model.addAttribute("trade", trade);
                model.addAttribute("feedbacks", serviceUtil.getUserFeedbacks(tradeUser));
                return "trade/sell";
            }

            User user = serviceUtil.getCurrentUser();
            User tradeUser = userService.find(advertise.getUser().getId());
            /*Verify verify = new Verify();*/
            if (serviceUtil.getAdvertiseMaxLimit(advertise) >= Double.valueOf(Common.plainStringPrice(trade.getAmount())).doubleValue() && verify.canTrade(trade.getAdvertise(), serviceUtil.getCurrentUser())
                    ) {

                if (verify.isTradeAmountAllowed(trade)) {
                    trade.setAdvertise(advertise);
                    trade.setUser(user);
                    trade.setTrader(tradeUser);
                    if (advertise.getAdType().getValue().contains("Sell")) {
                        trade.setBuyer(user);
                        trade.setSeller(tradeUser);
                    } else {
                        trade.setBuyer(tradeUser);
                        trade.setSeller(user);
                    }

                    trade.setRefereal(user);
                    trade.setBtcPrice(advertise.getAmount());
                    TradeStatus tradeStatus = tradeService.getTradeStatus(ConstantProperties.TRADE_STATUS_INPROCESS);
                    trade.setTradeStatus(tradeStatus);
                    trade.setTradeId(serviceUtil.getNewTradeId());
                    Integer nextTradeSequence = Integer.parseInt(serviceUtil.getLastTradeSequenceId()) + 1;
                    trade.setTradeSequenceId(nextTradeSequence.toString());
                    tradeService.save(trade);
                    alerts.setSuccess("Trade.saving.success");
                    try {
                        // send web notification to advertiser using websocket
                        if (advertise.getUser().isEnableWebNotification()) {
                            String msg = "New trade : " + trade.getAdvertise().getCurrency().getCurrencyCode() + " " + trade.getAmount() + "\nAdvertisement : #" + advertise.getAdvertisementId() + "\nFrom : " + user.getUsername();
                            OutputMessage outputMessage = new OutputMessage(serviceUtil.getCurrentUser().getUsername(), msg, "test", false);
                            outputMessage.setUrl(serviceUtil.getBaseUrl() + "/trade/process/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId())));
                            websocketHelper.sendWebNotification(advertise.getUser(), outputMessage);
                            List<User> childUsers = userService.getChildUsers(tradeUser);
                            for (User child : childUsers) {
                                OutputMessage outputMessage1 = new OutputMessage(user.getUsername(), msg, new LocalDateTime().toString(), false);
                                outputMessage.setUrl(serviceUtil.getBaseUrl() + "/trade/process/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId())));
                                websocketHelper.sendWebNotification(child, outputMessage1);
                            }
                            //webSocket.convertAndSendToUser(advertise.getUser().getUsername(), ConstantProperties.WEB_NOTIFICATION_DESTINATION, outputMessage);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage() + " error sending notification for new trade");
                    }
                }else{
                    alerts.setError("Trade.seller.insufficient.balance");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                    return "redirect:/trade/list";
                }
            } else {
                alerts.setError("General.error.msg");
                alerts.setError("<br/>");
                alerts.setError("<i class='fa fa-timesl'></i> Trade is disabled on this Advertise or Advertiser do not have enough balance");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/trade/list";
            }

        } catch (Exception e) {
            alerts.setError("Trade.saving.error");
            redirectAttributes.addFlashAttribute("trade", trade);
            logger.debug(e.getMessage());
        }
        alerts.setAlertModelAttribute(model);
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();

        return "redirect:/trade/process/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId()));
    }


    /**
     * @param type               List type (completed, all or in process)
     * @param page               Page no for paging
     * @param maxCount           max record per page
     * @param search             search string
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = {
            "/list",
            "/list/{type}",
            "/list/{type}/{page}",
            "/list/{type}/{page}/{maxCount}"
    }, method = RequestMethod.GET)
    public String tradeList(
            @PathVariable Optional<String> type,
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            ModelMap model, RedirectAttributes redirectAttributes) {
        User user = serviceUtil.getCurrentUser();
        /*List<Trade> incomingTradeList = serviceUtil.getIncomingTrades(null, null);
        List<Trade> outgoingTradeList = serviceUtil.getOutgoingTrades(null, null);*/
        Integer pageNo = 1;
        /*Integer recordPerPage;
        if (!maxCount.isPresent()) {
            AdminConfigUtil<Trade> adminConfigUtil = new AdminConfigUtil<Trade>();
            AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new Trade());
            recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
        } else {
            recordPerPage = maxCount.get();
        }*/
        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new Trade()) : maxCount.get();

        if (page.isPresent()) {
            pageNo = page.get();
        }

        String listType = "all";
        List<Trade> tradeList = new ArrayList<>();
        Long count = 0l;
        if (type.isPresent() && !type.get().equalsIgnoreCase("all")) {
            listType = type.get();
            tradeList = tradeService.getUsersTrades("id", "DESC", true, pageNo.longValue(), recordPerPage, listType);
            count = tradeService.countByUser(user, listType);
        } else {
            tradeList = tradeService.getUsersTrades("id", "DESC", true, pageNo.longValue(), recordPerPage);
            count = tradeService.countByUser(user);
        }


        model.addAttribute("tradeList", tradeList);
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue(), "/trade/list/" + listType + "/"));
        model.addAttribute("listType", listType);

        model.addAttribute("user", user);
        return "trade/list";
    }


    /**
     * @param id                 trade id
     * @param model
     * @param redirectAttributes
     * @param request
     * @return
     */
    @RequestMapping(value = {"/cancel/{id}", "/sendpayment/{id}", /*"/trade/paymentreceived/{id}",*/ "/bitcoinescrowed/{id}", "/bitcoinreleased/{id}", "/bitcoinreceived/{id}"}, method = RequestMethod.GET)
    public String tradeStatusChange(@PathVariable Optional<String> id, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        int tradeId = 0;
        String redirect = "";
        try {
            if (id.isPresent()) {
                tradeId = Integer.parseInt(pathVariableEncrypt.decrypt(id.get()));
            }

            Trade trade = tradeService.find(tradeId);
            if (trade.getTradeStatus().getStatusCode() == ConstantProperties.TRADE_STATUS_CANCELLED) {
                return "redirect:/trade/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId()));
            }

            User currentUser = serviceUtil.getCurrentUser();
            int currentUserId = currentUser.getId();
            int tradePartnerId = (currentUserId == trade.getBuyer().getId()) ? trade.getSeller().getId() : trade.getBuyer().getId();
            User tradePartner = userService.find(tradePartnerId);
            boolean ret = true;

            String[] paths = request.getServletPath().split("/");
            TradeProcess tradeProcess = new TradeProcess(serviceUtil);
            switch (paths[2]) {
                case "sendpayment":
                    if (!tradeProcess.processSendPayment(trade, currentUser))
                        ret = false;
                    break;
                /*case "paymentreceived":
                    if (!tradeProcess.processPaymentReceived(trade, currentUser))
                        ret = false;
                    break;*/
                case "cancel":
                    if (!tradeProcess.processCancel(trade, currentUser))
                        ret = false;
                    else {
                        //userService.saveOrUpdate(trade.getSeller());
                        serviceUtil.unEscrowBitcoins(trade);
                    }
                    break;
                case "bitcoinreleased":
                    if (!tradeProcess.processBitcoinReleased(trade, currentUser))
                        ret = false;
                    else {
                        serviceUtil.releaseBitcoins(trade);
                        Set<Escrow> e = trade.getEscrowSet();
                        ArrayList<Escrow> el = new ArrayList<Escrow>(e);
                        el.get(0).setReleased(true);
                        Set<Escrow> es = new HashSet<Escrow>(el);
                        trade.setEscrowSet(es);
                        redirect = "redirect:/user/feedback/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId()));
                        //userService.saveOrUpdate(trade.getBuyer());

                    }
                    break;
                case "bitcoinreceived":
                    if (!tradeProcess.processBitcoinReceived(trade, currentUser))
                        ret = false;
                    break;
                case "bitcoinescrowed":

                    if (!tradeProcess.processEscrow(trade, currentUser)) {
                        ret = false;
                        alerts.setError("Trade.bitcoin.balance.error");
                    } else {
                        if (serviceUtil.escrowBitcoins(trade))
                            advertisementService.saveOrUpdate(trade.getAdvertise());
                        else
                            ret = false;

                    }
                    break;
            }
            if (ret) {
                TradeStatus status = tradeService.getTradeStatus(trade.getTradeStatus().getStatusCode());
                trade.setTradeStatus(status);
                tradeService.saveOrUpdate(trade);
                if (tradePartner.isEnableWebNotification()) {
                    String msg = "Trade update : " + trade.getTradestatus() + "\n" + trade.getAdvertise().getCurrency().getCurrencyCode() + " " + trade.getAmount() + "\nTrade : #" + trade.getTradeId();
                    /*"Trade status changed\nTrade ID : #" + trade.getTradeId(), new LocalDateTime().toString(), false, pathVariableEncrypt.encrypt(Integer.toString(trade.getId()))*/
                    OutputMessage outputMessage = new OutputMessage(currentUser.getUsername(), msg, new LocalDateTime().toString(), false, pathVariableEncrypt.encrypt(Integer.toString(trade.getId())));
                    outputMessage.setUrl(serviceUtil.getBaseUrl() + "/trade/process/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId())));
                    websocketHelper.sendWebNotification(tradePartner, outputMessage);
                    List<User> childUsers = userService.getChildUsers(tradePartner);
                    for (User child : childUsers) {
                        OutputMessage outputMessage1 = new OutputMessage(currentUser.getUsername(),
                                "Trade status changed\nTrade ID : #" + trade.getTradeId(), new LocalDateTime().toString(), false);
                        outputMessage.setUrl(serviceUtil.getBaseUrl() + "/trade/process/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId())));
                        websocketHelper.sendWebNotification(child, outputMessage1);
                    }
                    //webSocket.convertAndSendToUser(tradePartner.getUsername(), ConstantProperties.WEB_NOTIFICATION_DESTINATION, outputMessage);
                }
                alerts.setSuccess("Trade.success.status.change");
            } else {
                if (alerts.getErrors().size() <= 0) {
                    alerts.setError("Trade.wrong.status.change");
                }
            }
        } catch (Exception e) {
            alerts.setError("Trade.error.status.change");
            logger.error("Error changin trade status : " + e.getMessage());
        }
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.setAlertModelAttribute(model);
        alerts.clearAlert();
        return (redirect.equalsIgnoreCase("")) ? "redirect:/trade/process/" + id.get() : redirect;
    }


    /**
     * @param file               file to be uploaded
     * @param request            httpservletrequest object
     * @param fromUser           user who is uploading file
     * @param toUser             other user in trade who will be able to access this file
     * @param tradeId            trade id
     * @param model
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/upload")
    public ResponseEntity<Object> fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request,
                                             @RequestParam("fromUser") String fromUser,
                                             @RequestParam("toUser") String toUser,
                                             @RequestParam("tradeId") String tradeId,
                                             ModelMap model,
                                             RedirectAttributes redirectAttributes
    ) throws IOException {

        String contentType = file.getContentType().toString().toLowerCase();
        if (!Common.isValidContentType(contentType, ALLOWED_FILE_TYPES)) {
            return new ResponseEntity<>("Invalid file. <br/>Allowed file types : " + Arrays.toString(ALLOWED_FILE_TYPES), HttpStatus.BAD_REQUEST);
        }

        User userFrom = serviceUtil.getUserFromUsernameOrEmail(fromUser);
        User userTo = serviceUtil.getUserFromUsernameOrEmail(toUser);
        Trade trade = tradeService.find(Integer.parseInt(pathVariableEncrypt.decrypt(tradeId)));
        LocalDateTime currentTime = new LocalDateTime();
        ChatFiles chatFile = new ChatFiles();
        if (userFrom != null && userTo != null && trade != null) {
            String newFileName = userFrom.getUsername() + userTo.getUsername() + tradeId + currentTime;
            if (!file.getOriginalFilename().isEmpty()) {
                BufferedOutputStream outputStream = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(ConstantProperties.CHAT_FILE_UPLOAD_LOCATION, newFileName)));


                outputStream.write(file.getBytes());
                outputStream.flush();
                outputStream.close();
                chatFile.setOriginalName(file.getOriginalFilename());
                chatFile.setPath(newFileName);
                chatFile.setTrade(trade);
                chatFile.setUserFrom(userFrom);
                chatFile.setUserTo(userTo);
                chatFile.setUploadTime(currentTime);
                chatFileService.saveOrUpdate(chatFile);
            } else {
                return new ResponseEntity<>("Invalid file.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Invalid file.", HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>("/trade/download/file/" + pathVariableEncrypt.encrypt(Integer.toString(chatFile.getId())) + "/" + pathVariableEncrypt.encrypt(tradeId) + "/" + chatFile.getOriginalName(), HttpStatus.OK);
    }

    /**
     * @param model
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/maxsizeexceeded")
    public ResponseEntity<Object> fileSizeExceeded(ModelMap model) throws IOException {
        return new ResponseEntity<>("Can no upload file > 10Mb", HttpStatus.OK);
    }


    /**
     * @param type               : File ID
     * @param tradeId            : Trade Id
     * @param fileName           : File name
     * @param request            : HttpRequest
     * @param response
     * @param redirectAttributes
     * @throws IOException
     */
    @GetMapping(value = "/download/file/{type}/{tradeId}/{fileName}")
    public void downloadFile(@PathVariable String type,
                             @PathVariable String tradeId,
                             @PathVariable String fileName,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             RedirectAttributes redirectAttributes) throws IOException {

        tradeId = (pathVariableEncrypt.decrypt(tradeId) != null ||
                !pathVariableEncrypt.decrypt(tradeId).equals(""))
                ? pathVariableEncrypt.decrypt(tradeId)
                : "0";
        this.tradeId = tradeId;
        String id = type;
        id = pathVariableEncrypt.decrypt(id);
        if (id != null && !id.equals("")) {
            ChatFiles chatFile = chatFileService.find(Integer.parseInt(id));
            if (chatFile != null &&
                    (
                            chatFile.getUserTo().getId() == serviceUtil.getCurrentUser().getId()
                                    ||
                                    chatFile.getUserFrom().getId() == serviceUtil.getCurrentUser().getId()
                    )
                    ) {
                Path file = Paths.get(ConstantProperties.CHAT_FILE_UPLOAD_LOCATION, chatFile.getPath());
                if (Files.exists(file)) {
                    //response.setContentType("application/pdf");
                    response.addHeader("Content-Disposition", "attachment; filename=" + chatFile.getOriginalName());
                    try {
                        Files.copy(file, response.getOutputStream());
                        response.getOutputStream().flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        throw new ChatFileNotFoundException(redirectAttributes);
                    }
                }
            } else {
                throw new ChatFileNotFoundException(redirectAttributes);
            }

        } else {
            throw new ChatFileNotFoundException(redirectAttributes);
        }
    }


    /**
     * @param req                : HttpServletRequest object
     * @param ex                 : Exception
     * @param redirectAttributes
     * @return
     * @see com.gradle.exception.handler.multipart.GlobalExceptionHandler
     */
    @ExceptionHandler(ChatFileNotFoundException.class)
    public String handleError(HttpServletRequest req, IOException ex, RedirectAttributes redirectAttributes) {
        logger.error("Request: " + req.getRequestURL() + " raised " + ex);
        alerts.setError("ChatFile.download.error");
        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/trade/process/" + pathVariableEncrypt.encrypt(tradeId);
    }


    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }


    /**
     * Download records as file controller actions
     *
     */

    /**
     * @param model              : ModelMap object
     * @param redirectAttributes : Redirect attributes object
     * @param response           : will generate csv file with trades records for logged in user
     */
    @GetMapping(value = "/download/csv", produces = "text/csv")
    @ResponseBody
    public void tradeDownload(ModelMap model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        List<Trade> tradeList = tradeService.getUsersTrades(null, null, false, 0l, 0);
        try {
            WriteCsvToResponse<Trade> csvWriter = new WriteCsvToResponse<Trade>();

            csvWriter.writeCsv(response, tradeList, null);
        } catch (IOException e) {

        }
    }


    /**
     * @param advertise           : Advertise Object
     * @param result              : Result
     * @param model               : ModelMap
     * @param redirectAttributes:
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/ad/list", "/ad/list/{page}", "/ad/list/{page}/{maxCount}"}, method = RequestMethod.GET)
    public String advertiseList(
            @PathVariable Optional<Integer> page, @PathVariable Optional<Integer> maxCount,
            @RequestParam Optional<String> search,
            Advertise advertise, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) throws Exception {

        Integer pageNo = 1;
        //messageSender.sendMessage(countriesService.findAll().get(0));
        /*AdminConfigUtil<Advertise> adminConfigUtil = new AdminConfigUtil<>();
        AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new Advertise());
        Integer recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();*/
        Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new Advertise()) : maxCount.get();
        if (page.isPresent()) {
            pageNo = page.get();
        }
        User user = serviceUtil.getCurrentUser();
        List<Advertise> advertiseList = advertisementService.findPaginatedByUser(pageNo.intValue(), recordPerPage, new Advertise(), null, user.getParent());
        //Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH+user.getEmail()+".wallet"));
        // Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH + user.getEmail() + ".wallet"));


        Long count = advertisementService.countByUser(user.getParent());
        User currentUser = serviceUtil.getCurrentUser();
        model.addAttribute("advertiseList", advertiseList);

        //model.addAttribute("currentWaddress", wallet.currentReceiveAddress());
        model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue()));
        model.addAttribute("user", currentUser);
        model.addAttribute("maxAllowed", verify.getMaxAllowedAdvertisement(currentUser));
        model.addAttribute("count", verify.getLiveAdvertisementCount(currentUser));

        return "trade/adlist";
    }

    /**
     * @param type               encrypted advertisement id
     * @param request
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value = "/inline/edit/{type}")
    public String inlineAdvertiseEdit(
            @PathVariable String type,
            HttpServletRequest request,
            ModelMap model,
            RedirectAttributes redirectAttributes) {
        try {
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            Advertise advertise = advertisementService.find(id);
            advertise.setMaxLimit(Common.formatPrice(Common.plainStringPrice(request.getParameter("maxRate"))));
            advertise.setMinLimit(Common.formatPrice(Common.plainStringPrice(request.getParameter("minRate"))));
            advertise.setBtcRate(Common.formatPrice(Common.plainStringPrice(request.getParameter("rate"))));
            Set<ConstraintViolation<Advertise>> violations = validator.validate(advertise);
            if (violations.size() <= 0 && serviceUtil.isAdvertiseAllowed(advertise)) {
                if (advertise.getUser().getId() == serviceUtil.getCurrentUser().getId()) {
                    advertisementService.saveOrUpdate(advertise);
                    alerts.setSuccess("Advertise.saving.success");
                } else {
                    alerts.setError("Genera.unauthorised.access");
                }
            } else {

                for (ConstraintViolation violation : violations) {
                    alerts.setError(violation.getMessage());
                }
                if (!serviceUtil.isAdvertiseAllowed(advertise)) {
                    alerts.setError("Advertise.bitcoin.not.available");
                }
                if (alerts.getErrors().size() <= 0) {
                    alerts.setError("General.error.msg");
                }
            }
        } catch (Exception e) {
            alerts.setError("General.error.msg");
            logger.error(e.getMessage());
        }

        alerts.setAlertRedirectAttribute(redirectAttributes);
        alerts.clearAlert();
        return "redirect:/home";
    }
}