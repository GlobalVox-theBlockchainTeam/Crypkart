/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller;

import com.gradle.components.jms.MessageSender;
import com.gradle.components.wallet.kit.WKit;
import com.gradle.components.wallet.transactions.Transactions;
import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.bitcoin.DirectTransaction;
import com.gradle.entity.bitcoin.InternalTransfer;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forms.BitcoinDataList;
import com.gradle.entity.forms.SendCoinForm;
import com.gradle.entity.forms.bitcoin.ExternalTransferForm;
import com.gradle.entity.forms.bitcoin.InternalTransferForm;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import com.gradle.services.iface.bitcoin.DirectTransactionService;
import com.gradle.services.iface.bitcoin.InternalTransferService;
import com.gradle.util.Common;
import com.gradle.util.Paging;
import com.gradle.util.adminConfig.AdminConfigUtil;
import com.gradle.util.constants.ConstantProperties;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/wallet")
public class WalletController extends AbstractBaseController {

    @Autowired
    private WKit wKit;

    @Autowired
    private Transactions transactions;


    @Autowired
    private InternalTransferService internalTransferService;

    @Autowired
    private DirectTransactionService directTransactionService;

    @Autowired
    private MessageSender messageSender;

    /**
     * Wallet home page
     *
     * @param form    : Sendcoin form contains address and coin properties
     * @param result
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String homeWallet(SendCoinForm form, BindingResult result, ModelMap model, HttpServletRequest request) throws Exception {
        try {


            Boolean test = serviceUtil.isBlockedUser(38);
            Context.propagate(new Context(netParams));
            User user = serviceUtil.getCurrentUser();


            //Load user's wallet
            /*Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH + user.getEmail() + ".wallet"));

            //Get all transaction for wallet
            List<BitcoinDataList> bitcoinDataLists = transactions.getTransactionFromWallet(wallet, netParams, ConstantProperties.WALLET_HOME_MAX_DISPLAY_TRANSACTIONS);
            List<BitcoinDataList> pendingTransactions = transactions.getPendingTransactionFromWallet(wallet, netParams, ConstantProperties.WALLET_HOME_MAX_DISPLAY_TRANSACTIONS);

            //Get value of pending transactions
            Coin value = transactions.getPendingTransactionValue(wallet, netParams);


            // Get wallet Balance
            Coin balance = wallet.getBalance();*/

            //Set all attributes to model so we can access it in front end
            /*model.addAttribute("waddress", wallet.getIssuedReceiveAddresses());
            model.addAttribute("wbalance", balance.toFriendlyString());
            model.addAttribute("wpendingbalance", value.toFriendlyString());
            model.addAttribute("currentWaddress", wallet.currentReceiveAddress());
            model.addAttribute("transactionList", bitcoinDataLists);
            model.addAttribute("pendingTransactionList", pendingTransactions);*/
            model.addAttribute("waddress", null);
            model.addAttribute("wbalance", null);
            model.addAttribute("wpendingbalance", null);
            model.addAttribute("currentWaddress", null);
            model.addAttribute("transactionList", null);
            model.addAttribute("pendingTransactionList", null);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("internalTransferForm", new InternalTransferForm());
            model.addAttribute("user", user);
            UserWallet userWallet = userWalletService.getCurrentUserWallet(user);
            if(userWallet.getWalletAddress().equals("Pending")) {
                try {
                    userWallet.setUid(user.getId());
                    messageSender.sendMessage(userWallet);
                } catch (Exception e) {
                    logger.error(e.getMessage() + e.getStackTrace());
                }
            }
            model.addAttribute("userWallet", userWallet);

            // Set page title (in case you do not set it here it will be loaded from properties file
            model.addAttribute("title", "Wallet home");

            //wKit.synchWallet(user, netParams);


            /*Generate Qr code for String*/
            /*QrCodeUtility qrCode = new QrCodeUtility();
            qrCode.generateQRCodeImage(wallet.currentReceiveAddress().toString(),200,200,
                    "/home/anand/projects/java/gradle/src/main/webapp/resources/qrimages/"+user.getEmail()+".png");*/


        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
        }
        return "wallet/home";
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String transactionList(ModelMap model, HttpServletRequest request) throws Exception {
        User user = serviceUtil.getCurrentUser();

        //Load user's wallet
        Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH + user.getEmail() + ".wallet"));

        //Get all transaction for wallet
        List<BitcoinDataList> bitcoinDataLists = transactions.getTransactionFromWallet(wallet, netParams, ConstantProperties.WALLET_HOME_ALL_TRANSACTION);


        // set attributes to model so we can access it in front end
        model.addAttribute("transactionList", bitcoinDataLists);
        return "wallet/list";
    }


    @PostMapping(value = "/internal/transfer")
    public String internalTransfer(@Valid @ModelAttribute("internalTransferForm") InternalTransferForm form, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {

        String ret = "wallet/internal/list";
        try {

            User user = serviceUtil.getCurrentUser();
            if (result.hasErrors()) {
                alerts.setError("Wallet.internal.transfer.error");
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
                ret = "wallet/internal/internaltransferform";
            } else {
                if (serviceUtil.canDoInternalTransfer(user, form)) {
                    InternalTransfer internalTransfer = new InternalTransfer();
                    User buyer = serviceUtil.getUserFromUsernameOrEmail(form.getUsername());
                    internalTransfer.setBuyer(buyer);
                    internalTransfer.setSeller(user);
                    internalTransfer.setBitcoinAmount(Common.plainStringPrice(form.getBtcAmount()));
                    internalTransfer.setRemarks(form.getRemarks());
                    internalTransferService.saveOrUpdate(internalTransfer);
                    alerts.setSuccess("Wallet.internal.transfer.success");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                    return "redirect:/wallet/internal/transfer/list/seller";
                } else {
                    alerts.setError("Wallet.sufficient.balance.error");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                    ret = "wallet/internal/internaltransferform";
                }
            }
            model.addAttribute("waddress", null);
            model.addAttribute("wbalance", null);
            model.addAttribute("wpendingbalance", null);
            model.addAttribute("currentWaddress", null);
            model.addAttribute("transactionList", null);
            model.addAttribute("pendingTransactionList", null);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("internalTransferForm", form);

            // Set page title (in case you do not set it here it will be loaded from properties file
            model.addAttribute("title", "Sending bitcoins");
        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            ret = "redirect:/wallet/internal/transfer";
        }

        return ret;
    }

    @GetMapping(value = "/internal/transfer")
    public String internalTransferForm(ModelMap model, RedirectAttributes redirectAttributes) {

        try {

            User user = serviceUtil.getCurrentUser();
            model.addAttribute("email", user.getEmail());
            model.addAttribute("internalTransferForm", new InternalTransferForm());
            // Set page title (in case you do not set it here it will be loaded from properties file
            model.addAttribute("title", "Sending bitcoins");
        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/wallet/home";
        }

        return "wallet/internal/internaltransferform";
    }


    @PostMapping(value = "/external/transfer")
    public String externalTransfer(@Valid @ModelAttribute("externalTransferForm") ExternalTransferForm form, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String ret;
        String pass = request.getParameter("secret");
        GoogleAuthenticator ga = new GoogleAuthenticator();
        //Boolean v = ga.authorize(serviceUtil.getCurrentUser().getGoogleAuthenticatorKey(), Integer.parseInt(pass));
        try {

            User user = serviceUtil.getCurrentUser();
            if (result.hasErrors()) {
                alerts.setError("Wallet.required.fields.missing");
                alerts.setAlertModelAttribute(model);
                alerts.clearAlert();
                ret = "wallet/external/externalTransferForm";
            } else {
                if (serviceUtil.canDoExternalTransfer(user, form)) {
                    DirectTransaction directTransaction = new DirectTransaction();
                    directTransaction.setOutgoing(true);
                    directTransaction.setOutgoingWalletAddress(form.getWalletAddress());
                    directTransaction.setBitcoinAmount(form.getBtcAmount());
                    directTransaction.setEmail(form.getEmail());
                    directTransaction.setUser(user);
                    directTransaction.setRemarks(form.getRemarks());
                    directTransactionService.saveOrUpdate(directTransaction);

                    UserWallet userWallet = new UserWallet();
                    userWallet.setUid(user.getId());
                    userWallet.setAmount(form.getBtcAmount());
                    userWallet.setWalletAddress(form.getWalletAddress());
                    userWallet.setCurrentTransactionId(directTransaction.getId());
                    messageSender.sendCoinMessage(userWallet);

                    messageSender.sendMessage(userWallet);

                    alerts.setSuccess("Wallet.internal.transfer.success");
                    alerts.setAlertRedirectAttribute(redirectAttributes);
                    alerts.clearAlert();
                    return "redirect:/wallet/external/transfer/list/out";
                } else {
                    alerts.setError("Wallet.sufficient.balance.error");
                    alerts.setAlertModelAttribute(model);
                    alerts.clearAlert();
                    ret = "wallet/external/externalTransferForm";
                }
            }
            model.addAttribute("waddress", null);
            model.addAttribute("wbalance", null);
            model.addAttribute("wpendingbalance", null);
            model.addAttribute("currentWaddress", null);
            model.addAttribute("transactionList", null);
            model.addAttribute("pendingTransactionList", null);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("internalTransferForm", form);

            // Set page title (in case you do not set it here it will be loaded from properties file
            model.addAttribute("title", "Sending bitcoins");
        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            ret = "redirect:/wallet/external/transfer";
        }
        UserWallet currentWallet = userWalletService.getCurrentUserWallet(serviceUtil.getCurrentUser());
        String walletAddress = currentWallet.getWalletAddress();
        model.addAttribute("walletAddress", walletAddress);
        return ret;
    }

    @GetMapping(value = "/external/transfer")
    public String externalTransferForm(ModelMap model, RedirectAttributes redirectAttributes) {

        try {

            User user = serviceUtil.getCurrentUser();
            model.addAttribute("user", user);
            model.addAttribute("externalTransferForm", new ExternalTransferForm());
            // Set page title (in case you do not set it here it will be loaded from properties file
            model.addAttribute("title", "Sending Bitcoins");
        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/wallet/home";
        }
        UserWallet currentWallet = userWalletService.getCurrentUserWallet(serviceUtil.getCurrentUser());
        String walletAddress = currentWallet.getWalletAddress();
        model.addAttribute("walletAddress", walletAddress);
        return "wallet/external/externalTransferForm";
    }

    @GetMapping(value = {
            "/external/transfer/list",
            "/external/transfer/list/{type}",
            "/external/transfer/list/{type}/{page}",
            "/external/transfer/list/{type}/{page}/{maxCount}",
            "/external/transfer/list/{page}",
            "/external/transfer/list/{page}/{maxCount}"})
    public String externalTransferList(
            @PathVariable Optional<String> type,
            @PathVariable Optional<Integer> page,
            @PathVariable Optional<Integer> maxCount,
            ModelMap model, RedirectAttributes redirectAttributes) {

        try {
            Integer pageNo = 1;
            String transactionType = "out";
            if (page.isPresent()) {
                pageNo = page.get();
            }
            if (type.isPresent()) {
                transactionType = type.get();
            }

            User user = serviceUtil.getCurrentUser();
            Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new DirectTransaction()) : maxCount.get();
            /*Integer recordPerPage;
            if (!maxCount.isPresent()) {
                AdminConfigUtil<DirectTransaction> adminConfigUtil = new AdminConfigUtil<DirectTransaction>();
                AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new DirectTransaction());
                recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
            } else {
                recordPerPage = maxCount.get();
            }*/

            List<DirectTransaction> directTransactions = directTransactionService.getByType(pageNo, recordPerPage, new DirectTransaction(), user, "user_id", "outgoing_transaction", (transactionType.equalsIgnoreCase("out")));
            Double count = directTransactionService.getByProjection(user, "user", Projections.count("id"), transactionType.equalsIgnoreCase("out"));
            model.addAttribute("title", "Sending bitcoins");
            model.addAttribute("list", directTransactions);
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count.longValue(), recordPerPage.longValue(), "/wallet/external/transfer/list/" + transactionType + "/"));
            model.addAttribute("type", transactionType);
            model.addAttribute("user", user);


        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/wallet/home";
        }

        return "wallet/external/list";
    }


    @GetMapping(value = "/external/transfer/view/{type}")
    public String viewExternalTransfer(@PathVariable String type, ModelMap model, RedirectAttributes redirectAttributes) {

        try {
            User user = serviceUtil.getCurrentUser();
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            DirectTransaction directTransaction = directTransactionService.find(id);
            if (directTransaction != null && (directTransaction.getUser().getId() == user.getId())) {
                model.addAttribute("directTransaction", directTransaction);
                model.addAttribute("user", user);
            } else {
                alerts.setError("Genera.unauthorised.access");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/wallet/internal/transfer/list";
            }
        } catch (Exception e) {
            alerts.setError("Genera.unauthorised.access");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/wallet/external/transfer/list";
        }
        return "wallet/external/view";
    }


    @GetMapping(value = {
            "/internal/transfer/list",
            "/internal/transfer/list/{type}",
            "/internal/transfer/list/{type}/{page}",
            "/internal/transfer/list/{type}/{page}/{maxCount}",

    })
    public String internalTransferList(
            @PathVariable Optional<String> type,
            @PathVariable Optional<Integer> page,
            @PathVariable Optional<Integer> maxCount,
            ModelMap model, RedirectAttributes redirectAttributes) {

        try {
            Integer pageNo = 1;
            String transactionType = "buyer";
            if (page.isPresent()) {
                pageNo = page.get();
            }
            if (type.isPresent()) {
                transactionType = type.get();
            }
            User user = serviceUtil.getCurrentUser();

            /*Integer recordPerPage;
            if (!maxCount.isPresent()) {
                AdminConfigUtil<InternalTransfer> adminConfigUtil = new AdminConfigUtil<InternalTransfer>();
                AdminConfig adminConfig = adminConfigUtil.getAdminConfig(serviceUtil, new InternalTransfer());
                recordPerPage = (adminConfig != null && adminConfig.getRecordPerPage() > 0) ? adminConfig.getRecordPerPage() : ConstantProperties.PAGING_MAX_PER_PAGE.intValue();
            } else {
                recordPerPage = maxCount.get();
            }*/
            Integer recordPerPage = (!maxCount.isPresent()) ? serviceUtil.getRecordPerPage(new InternalTransfer()) : maxCount.get();


            List<InternalTransfer> internalTransfersAsBuyer = internalTransferService.findPaginatedByUserWithOrder(pageNo, recordPerPage, new InternalTransfer(), user, transactionType + "_id", "id", "DESC");
            Long count = internalTransferService.countByUserByProperty(user, transactionType);
            model.addAttribute("title", "Sending bitcoins");
            model.addAttribute("list", internalTransfersAsBuyer);
            model.addAttribute("pagging", new Paging(pageNo.longValue(), count, recordPerPage.longValue(), "/wallet/internal/transfer/list/" + transactionType + "/"));
            model.addAttribute("type", transactionType);
            model.addAttribute("user", user);


        } catch (Exception e) {
            alerts.setError("General.error.msg");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/wallet/home";
        }

        return "wallet/internal/list";
    }


    @GetMapping(value = "/internal/transfer/view/{type}")
    public String viewInternalTransfer(@PathVariable String type, ModelMap model, RedirectAttributes redirectAttributes) {

        try {
            User user = serviceUtil.getCurrentUser();
            Integer id = Integer.parseInt(pathVariableEncrypt.decrypt(type));
            InternalTransfer internalTransfer = internalTransferService.find(id);
            if (internalTransfer != null &&
                    (internalTransfer.getBuyer().getId() == user.getId() || internalTransfer.getSeller().getId() == user.getId())
                    ) {
                model.addAttribute("internalTransfer", internalTransfer);
                model.addAttribute("user", user);
            } else {
                alerts.setError("Genera.unauthorised.access");
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/wallet/internal/transfer/list";
            }
        } catch (Exception e) {
            alerts.setError("Genera.unauthorised.access");
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();
            return "redirect:/wallet/internal/transfer/list";
        }
        return "wallet/internal/view";
    }

    /*@RequestMapping(value = "/send", method = RequestMethod.POST)
    public String sendBtc(@Valid @ModelAttribute("sendCoinForm") SendCoinForm form, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) throws Exception {
        WalletAppKit kit = null;
        Context.propagate(new Context(netParams));
        try {
            // validate inputs if not valid redirect back with error message
            if (result.hasErrors()) {
                alerts.setError("Please enter valid bitcoin and wallet address");
                alerts.setAlertModelAttribute(model);
                alerts.setAlertRedirectAttribute(redirectAttributes);
                alerts.clearAlert();
                return "redirect:/wallet/home";
            }


            // get current user
            User user = serviceUtil.getCurrentUser();
            Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH + user.getEmail() + ".wallet"));
            Coin balance = wallet.getBalance();
            Coin estimatedBalance = wallet.getBalance(Wallet.BalanceType.ESTIMATED);

            model.addAttribute("waddress", wallet.getIssuedReceiveAddresses());
            model.addAttribute("wbalance", balance.toFriendlyString());
            model.addAttribute("wpendingbalance", estimatedBalance.toFriendlyString());
            model.addAttribute("currentWaddress", wallet.currentReceiveAddress());

            //Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH+user.getEmail()+".wallet"));

            // Sending coins using wallet kit
            wKit.sendCoinsTo(user, form, netParams);


            alerts.setAlertModelAttribute(model);
            alerts.setAlertRedirectAttribute(redirectAttributes);
            alerts.clearAlert();


        } finally {

        }

        return "redirect:/wallet/sent";
    }*/


    /**
     * Refresh wallet by syncing wallet with block chain
     *
     * @param model
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public String refreshWallet(ModelMap model, RedirectAttributes redirectAttributes) throws Exception {
        User user = serviceUtil.getCurrentUser();
        //Wallet wallet = Wallet.loadFromFile(new File(ConstantProperties.USER_WALLET_FILE_PATH+user.getEmail()+".wallet"));
        File file = new File(ConstantProperties.USER_WALLET_FILE_PATH + user.getEmail() + ".wallet");
        Wallet wallet = Wallet.loadFromFile(file);
        logger.error(wallet.toString());

        // Set up the components and link them together.
        final NetworkParameters params = TestNet3Params.get();
        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, wallet, blockStore);

        final PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.startAsync();
        // Now download and process the block chain.
        peerGroup.downloadBlockChain();
        peerGroup.stopAsync();
        wallet.saveToFile(file);
        logger.error("\nDone!\n");
        logger.error(wallet.toString());

        return "redirect:/wallet/home";
    }

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }
}
