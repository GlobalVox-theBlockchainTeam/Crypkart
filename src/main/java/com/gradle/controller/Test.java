/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller;

import com.google.common.util.concurrent.ListenableFuture;
import com.gradle.components.wallet.kit.WKit;
import com.gradle.components.wallet.transactions.Transactions;
import com.gradle.controller.base.AbstractBaseController;
import com.gradle.entity.Mail;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import com.gradle.util.Alerts;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.PeerDataEventListener;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.utils.BlockFileLoader;
import org.bitcoinj.wallet.Wallet;

import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bitcoinj.wallet.listeners.ScriptsChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;



@RequestMapping(value = "/admin/test")
public class Test extends AbstractBaseController{

    protected static final NetworkParameters netParams = TestNet3Params.get();
    protected static final NetworkParameters regNetParams = RegTestParams.get();
    protected static final NetworkParameters prodNetParams = MainNetParams.get();

    @Autowired
    private WKit wKit;

    @Autowired
    Transactions transactions;

    @Autowired
    Alerts alerts;

    private static final String templateBase = "wallet/";

    /**
     *  From home controller unused methods for reference
     *
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(@Valid @ModelAttribute("user") User user, ModelMap model, RedirectAttributes redirectAttributes) {

        alerts.setError("Error saving user. Try again later");

        model.addAttribute("msg", "Welcome to Coinmart");
        model.addAttribute("msgtype", "warning");
        return "login";
    }

    @RequestMapping(value = "/captcha/check", method = RequestMethod.GET)
    public String requestSectionById(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        String a = "A";
        return null;
    }

    @RequestMapping(value = "/g/home")
    public String testGuest(ModelMap model) {
        model.addAttribute("page", "Guest");
        return "test";
    }

    @RequestMapping(value = "/admin/home")
    public String testAdmin(ModelMap model) {
        model.addAttribute("page", "Admin");
        return "test";
    }

    @RequestMapping(value = "/u/home")
    public String testUser(ModelMap model) {
        model.addAttribute("page", "User");
        return "test";
    }

    @RequestMapping(value = "/u/sendmail", method = RequestMethod.GET)
    public String sendMail(User user, ModelMap model, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response)
            throws MessagingException {
        model.addAttribute("name", "Anand Panchal");
        model.addAttribute("subscriptionDate", new Date());
        model.addAttribute("hobbies", Arrays.asList("Music", "Sports", "Games"));

        org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
        ctx.setVariable("name", "Anand Panchal");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Music", "Sports", "Games"));
        List<User> userList = userService.findAll();
        ctx.setVariable("users", userList);


        Mail mail = new Mail();
        mail.setFrom("no-reply@memorynotfound.com");
        mail.setTo("info@memorynotfound.com");
        mail.setSubject("Sending Email with Thymeleaf HTML Template Example");


        model.put("name", "Memorynotfound.com");
        model.put("location", "Belgium");
        model.put("signature", "https://memorynotfound.com");
        mail.setContent("te");


//        final String htmlContent = templateEngine.process("landing",ctx);
        //emailService.sendEditableMail("Anand Pancahl", "anand4686@gmail.com","landing", Locale.ENGLISH);
        //emailService.sendTextMail("Anand Panchal", "anand4686@gmail.com", Locale.ENGLISH);


        return "landing";
    }


    @RequestMapping(value = "/simple.html", method = RequestMethod.GET)
    public String simple() {
        return "simple";
    }

    @RequestMapping(value = "/sendMailSimple", method = POST)
    public String sendSimpleMail(
            @RequestParam("recipientName") final String recipientName,
            @RequestParam("recipientEmail") final String recipientEmail,
            final Locale locale)
            throws MessagingException {

        this.emailService.sendSimpleMail(recipientName, recipientEmail, locale);
        return "redirect:sent.html";

    }

    @RequestMapping(value = "/sendEditableMail", method = POST)
    public String sendMailWithInline(
            @RequestParam("recipientName") final String recipientName,
            @RequestParam("recipientEmail") final String recipientEmail,
            @RequestParam("body") final String body,
            final Locale locale)
            throws MessagingException, IOException {

        this.emailService.sendEditableMail(
                recipientName, recipientEmail, body, locale);
        return "editable";

    }


    @RequestMapping(value = "/editable", method = RequestMethod.GET)
    public String editable(ModelMap model) {
        return "editable";
    }



    @RequestMapping(value = "/delete/user", method = RequestMethod.POST)
    public String delete(@ModelAttribute("user") User user, ModelMap model, BindingResult result) {

        user = userService.find(user.getId());
        userService.delete(user);
        List<User> users = userService.findAll();
        model.addAttribute("title", "Bootstrap worked");
        model.addAttribute("users", users);
        model.addAttribute("msg", "User deleted");
        model.addAttribute("msgtype", "success");
        return "landing";
    }

    @RequestMapping(value = "/update/user", method = RequestMethod.POST)
    public String update(@ModelAttribute("user") User user, ModelMap model, BindingResult result) {

        List<User> users = userService.findAll();
        User updateUser = userService.find(user.getId());
        model.addAttribute("user", updateUser);
        model.addAttribute("title", "Bootstrap worked");
        model.addAttribute("users", users);
        if (result.hasErrors()) {
            model.addAttribute("msg", "User saved");
            model.addAttribute("msgtype", "danger");
            return "landing";
        }
        return "landing";
    }


    /**
     * Unused methods from Wallet controller for reference
     *
     *
     */

    /*try {
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            System.out.println("coins sent. transaction hash: " + result.tx.getHashAsString());
            // you can use a block explorer like https://www.biteasy.com/ to inspect the transaction with the printed transaction hash.
        } catch (InsufficientMoneyException e) {
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
            System.out.println("Send money to: " + kit.wallet().currentReceiveAddress().toString());

            // Bitcoinj allows you to define a BalanceFuture to execute a callback once your wallet has a certain balance.
            // Here we wait until the we have enough balance and display a notice.
            // Bitcoinj is using the ListenableFutures of the Guava library. Have a look here for more information: https://github.com/google/guava/wiki/ListenableFutureExplained
            ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, Wallet.BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                @Override
                public void onSuccess(@Nullable Coin result) {
                    System.out.println("coins arrived and the wallet now has enough balance");
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("something went wrong");
                }
            };
            *//*ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                @Override
                public void onSuccess(Coin balance) {
                    System.out.println("coins arrived and the wallet now has enough balance");
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("something went wrong");
                }
            };
            Futures.addCallback(balanceFuture, callback);*//*
        }*/



    @RequestMapping(value = "/key")
    public String createEckey(ModelMap model) {

        ECKey key = new ECKey();
        model.addAttribute("key", "We created key:\n" + key);
        System.out.println("We created key:\n" + key);
        Address addressFromKey = key.toAddress(netParams);

        System.out.println("On the test network, we can use this address:\n" + addressFromKey);
        System.out.println("Private key : " + key.getPrivKey());
        System.out.println("Private key hash : " + key.getPrivateKeyAsHex());
        System.out.println("Public key : " + key.getPubKey());
        System.out.println("Public key hash: " + key.getPubKeyHash());


        System.out.println("On the test network, we can use this address:\n" + addressFromKey);
        System.out.println("Private key : " + key.getPrivKey());
        System.out.println("Private key hash : " + key.getPrivateKeyAsHex());
        System.out.println("Public key : " + key.getPubKey());
        System.out.println("Public key hash: " + key.getPubKeyHash());

        return templateBase + "key";
    }


    public void test() throws Exception {
        NetworkParameters mParams = RegTestParams.get();

// define how to connect based on network params
        if (mParams == RegTestParams.get()) {
            try {
                PeerAddress pa = new PeerAddress(mParams, InetAddress.getByName("127.0.0.1"), 19000);

                //mKit.setPeerNodes(pa);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }


    }


    public void test1() throws Exception {
        WalletAppKit kit = new WalletAppKit(regNetParams, new File("/home/anand/projects/java/gradle/"), "anand");
        PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);
        kit.setPeerNodes(pa);
        kit.startAsync();

        kit.awaitRunning();

        Coin value = Coin.parseCoin("0.01");
        Address to = Address.fromBase58(regNetParams, "mrfpsLGufNr6JguVjqEqYKqGoNU7DQWgJg");


        System.out.println(kit.wallet().getIssuedReceiveAddresses());
        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("-----> coins resceived: " + tx.getHashAsString());
                System.out.println("received: " + tx.getValue(wallet));
            }
        });

        kit.wallet().addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("coins sent");
            }
        });

        kit.wallet().addKeyChainEventListener(new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
            }
        });

        kit.wallet().addScriptsChangeEventListener(new ScriptsChangeEventListener() {
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }
        });

        kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                System.out.println("-----> confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
            }
        });


    }

    public void tet() throws Exception {

        Context.getOrCreate(regNetParams);
        WalletAppKit kit = new WalletAppKit(regNetParams, new File("/home/anand/projects/java/gradle/"), "anand");
        Wallet wallet = new Wallet(regNetParams);
        SPVBlockStore spvBlockStore = new SPVBlockStore(regNetParams, new File("/home/anand/projects/java/gradle/anand.spvchain"));
        BlockChain chain = new BlockChain(regNetParams, spvBlockStore);
        PeerGroup peer = new PeerGroup(regNetParams, chain);
        PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);
        kit.setPeerNodes(pa);
        chain.addWallet(wallet);
        peer.addWallet(wallet);

        PeerDataEventListener blistner = new PeerDataEventListener() {
            @Override
            public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
                System.out.println("Download done");
            }

            @Override
            public void onChainDownloadStarted(Peer peer, int blocksLeft) {
                System.out.println("Download done");
            }

            @Nullable
            @Override
            public List<Message> getData(Peer peer, GetDataMessage m) {
                System.out.println("Message downloaded");
                return null;
            }

            @Override
            public Message onPreMessageReceived(Peer peer, Message m) {
                System.out.println("Message received");
                return null;
            }
        };

        peer.start();
        peer.startBlockChainDownload(blistner);
        blistner.wait();


    }


    @RequestMapping(value = "/store")
    public String storeChain(ModelMap model) throws Exception {

        tet();

        /*BlockStore blockStore = new MemoryBlockStore(netParams);
        BlockChain chain;

        try {
            chain = new BlockChain(netParams, blockStore);
            byte[] b = new byte[64];






            PeerGroup peerGroup = new PeerGroup(netParams, chain);
            PeerAddress addr = new PeerAddress(netParams, InetAddress.getLocalHost());
            peerGroup.addAddress(addr);



            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                            peerGroup.startAsync();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                }
            }).start();

            peerGroup.waitForPeers(1).get();

            Peer peer = peerGroup.getConnectedPeers().get(0);
            Sha256Hash blockHash = new Sha256Hash("00000007199508e34a9ff81e6ec0c477a4cccff2a4767a8eee39c11db367b008");

            // ask the node to which we're connected for the block
            // and wait for a response
            Future<Block> future = peer.getBlock(blockHash);
            System.out.println("Waiting for node to send us the requested block: " + blockHash);

            // get and use the Block's toString() to output the genesis block
            Block block = future.get();
            System.out.println("Here is the genesis block:\n" + block);

            *//*Sha256Hash blockHash = Sha256Hash.wrap();
            Future<Block> future = peer.getBlock(blockHash);
            System.out.println("Waiting for node to send us the requested block: " + blockHash);
            Block block = future.get();
            System.out.println(block);
            peerGroup.stopAsync();*//*

        } catch (Exception e) {

        }*/
        return "wallet/store";
    }


    @RequestMapping(value = "/wallet/create", method = RequestMethod.GET)
    public String createWallet(ModelMap model, Authentication authentication) throws Exception {


        Wallet wallet = null;
        final File walletFile = new File("/home/anand/projects/java/gradle/wallet/test.wallet");
        /*
        BlockStore blockStore = new MemoryBlockStore(netParams);



        BlockChain chain = new BlockChain(netParams, wallet, blockStore);
        PeerGroup peerGroup = new PeerGroup(netParams, chain);
        peerGroup.addWallet(wallet);
        peerGroup.start();
        Address a = wallet.currentReceiveAddress();
        ECKey b = wallet.currentReceiveKey();
        Address c = wallet.freshReceiveAddress();*/
        try {
            //wallet.loadFromFile(walletFile);
            wallet = new Wallet(netParams);
            // 5 timesl
            for (int i = 0; i < 1; i++) {

                // create a key and save it to the wallet
                wallet.importKey(new ECKey());
                //wallet.addKey(new ECKey());
            }
            // save wallet contents to disk
            wallet.saveToFile(walletFile);
            Object[] params = new Object[1];
            params[0] = authentication.getName();
            User user = userService.first("from User where username=?", params);
            UserWallet userWallet = new UserWallet();
            userWallet.setPrivateKey(wallet.currentReceiveKey().toString());
           /* userWallet.setPublicKey(wallet.currentReceiveKey().toString());
            userWallet.setUser(user);
            userWallet.setWalletId(wallet.currentReceiveAddress().toString());*/
            userWalletService.saveOrUpdate(userWallet);


        } catch (IOException e) {
            System.out.println("Unable to create wallet file.");
        }

        System.out.println("Complete content of the wallet:\n" + wallet);


        model.addAttribute("wallet", wallet);
        model.addAttribute("walletString", wallet.toString().replaceAll("(\r\n|\n)", "<br />"));


        Address a = wallet.currentReceiveAddress();
        ECKey b = wallet.currentReceiveKey();
        Address c = wallet.freshReceiveAddress();
        model.addAttribute("walletString", wallet.toString().replaceAll("(\r\n|\n)", "<br />"));
        model.addAttribute("walletAddressa", a.toString());
        model.addAttribute("walletAddressb", b.toAddress(netParams));
        model.addAttribute("walletAddressc", c.toString());
        model.addAttribute("msg", "Wallet Created");
        model.addAttribute("msgtype", "success");
        /*wallet.getWatchingKey();
        wallet.getBalance();
        wallet.getWatchedAddresses();*/
        return "wallet/create";
    }


    @RequestMapping(value = "/wallet/send", method = RequestMethod.GET)
    public String sendRequest(ModelMap model) throws Exception {

        WalletAppKit kit = new WalletAppKit(regNetParams, new File("."), "sendrequest-example");
        PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);
        kit.setPeerNodes(pa);
        kit.startAsync();

        kit.awaitRunning();

        Coin value = Coin.parseCoin("0.01");
        Address to = Address.fromBase58(regNetParams, "2Mx4DP31BJCH9EECdxthSz5MZVpGRCQT6B3");

        Wallet wallet = new Wallet(regNetParams);
        wallet.addWatchedAddress(to);
        System.out.println(wallet.getBalance());


        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("-----> coins resceived: " + tx.getHashAsString());
                System.out.println("received: " + tx.getValue(wallet));
            }
        });

        kit.wallet().addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("coins sent");
            }
        });

        kit.wallet().addKeyChainEventListener(new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
            }
        });

        kit.wallet().addScriptsChangeEventListener(new ScriptsChangeEventListener() {
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }
        });

        kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                System.out.println("-----> confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
            }
        });







        model.addAttribute("walletString", "Test failed");

        return "wallet/create";
    }








































    public static void main2(String[] args){
        Context.getOrCreate(regNetParams);
        NetworkParameters np = regNetParams;
        List<File> blockChainFiles = new ArrayList<>();
        //blockChainFiles.add(new File("/home/anand/Downloads/bitcoin-testnet-box/1/regtest/blocks/blk00000.dat"));
        blockChainFiles.add(new File("/home/anand/projects/java/gradle/anand.spvchain"));
        BlockFileLoader bfl = new BlockFileLoader(np, blockChainFiles);

// Data structures to keep the statistics.
        Map<String, Integer> monthlyTxCount = new HashMap<>();
        Map<String, Integer> monthlyBlockCount = new HashMap<>();

// Iterate over the blocks in the dataset.
        for (Block block : bfl) {

            // Extract the month keyword.
            String month = new SimpleDateFormat("yyyy-MM").format(block.getTime());

            // Make sure there exists an entry for the extracted month.
            if (!monthlyBlockCount.containsKey(month)) {
                monthlyBlockCount.put(month, 0);
                monthlyTxCount.put(month, 0);
            }

            // Update the statistics.
            monthlyBlockCount.put(month, 1 + monthlyBlockCount.get(month));
            monthlyTxCount.put(month, block.getTransactions().size() + monthlyTxCount.get(month));

        }

// Compute the average number of transactions per block per month.
        Map<String, Float> monthlyAvgTxCountPerBlock = new HashMap<>();
        for (String month : monthlyBlockCount.keySet())
            monthlyAvgTxCountPerBlock.put(
                    month, (float) monthlyTxCount.get(month) / monthlyBlockCount.get(month));
    }

    public static void main4(String[] args){
        try {
            /*WalletAppKit walletAppKit = new WalletAppKit(regNetParams, new File("/home/anand/projects/java/gradle/wallet/test.wallet"), "");
            PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);

            walletAppKit.setPeerNodes(pa);
            Wallet wallet = new Wallet(regNetParams);

            wallet.importKey(new ECKey());
            SPVBlockStore spvBlockStore = new SPVBlockStore(regNetParams, new File("/home/anand/projects/java/gradle/wallet/test.wallet"));
            BlockChain chain = new BlockChain(regNetParams, spvBlockStore);
            PeerGroup peer = new PeerGroup(regNetParams, chain);
            System.out.println(peer);

            peer.addWallet(wallet);*/


            //testnet();
            WalletAppKit kit = new WalletAppKit(regNetParams, new java.io.File("."), "anand");

            PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("192.168.31.217"), 19000);
            kit.setAutoSave(true);
            kit.setPeerNodes(pa);
            kit.startAsync();
            kit.awaitRunning();
            BlockChain chain = kit.chain();
            PeerGroup peerGroup = new PeerGroup(regNetParams, chain);
            BlockStore bs = chain.getBlockStore();
            Peer peer = kit.peerGroup().getDownloadPeer();
            Block b = peer.getBlock(bs.getChainHead().getHeader().getHash()).get();
            Address to = Address.fromBase58(regNetParams, "mgL6wQ57i3fLPavd1LAJCubcmrk7QFTzoY");
            Coin value = Coin.parseCoin("0.01");
            b.createNextBlock(to, value);
            System.out.println(b.getTransactions().toString());
            System.out.println(b);
            System.out.println("Anand file read");



            peerGroup.startBlockChainDownload(new PeerDataEventListener() {
                public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int i) {

                    List<Transaction> transactionsList = block.getTransactions();
                    int transactions = transactionsList == null ? 0 : transactionsList.size();

                    long height = peer.getBestHeight() - i;
                    //If the block contains transactions, it is likely to be complete.
                    System.out.println( "Downloaded block " + height + " with " + transactions + " transactions");
                    //blockUpdate(block);
                }

                private void blockUpdate(Block fBlock) throws IOException {
                    //TODO: Update blockchain database with the full block.
                }

                public void onChainDownloadStarted(Peer peer, int i) {
                    //Log.i(TAG, "Started to download chain on peer " + peer);
                }

                @Nullable
                public List<Message> getData(Peer peer, GetDataMessage getDataMessage) {
                    //Log.i(TAG, "getData from " + peer);
                    return null;
                }

                public Message onPreMessageReceived(Peer peer, Message message) {
                    //Log.i(TAG, "onPreMessageReceived (" + message.getClass().getSimpleName() + ") from " + peer);
                    return message;
                }
            });




            Wallet wallet = kit.wallet();
            System.out.println(wallet.getIssuedReceiveAddresses());
            chain.addWallet(wallet);
            peerGroup.addWallet(wallet);
            PeerDataEventListener blistner = new PeerDataEventListener() {
                @Override
                public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {
                    System.out.println("Download done");
                }

                @Override
                public void onChainDownloadStarted(Peer peer, int blocksLeft) {
                    System.out.println("Download done");
                }

                @Nullable
                @Override
                public List<Message> getData(Peer peer, GetDataMessage m) {
                    System.out.println("Message downloaded");
                    return null;
                }

                @Override
                public Message onPreMessageReceived(Peer peer, Message m) {
                    System.out.println("Message received");
                    return null;
                }
            };

            peerGroup.start();
            peerGroup.startBlockChainDownload(blistner);
            blistner.wait();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void testnet() throws Exception{
        WalletAppKit kit = new WalletAppKit(regNetParams, new File("."), "anand");
        PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);

        kit.setPeerNodes(pa);
        kit.startAsync();

        kit.awaitRunning();

        Coin value = Coin.parseCoin("0.01");
        Address to = Address.fromBase58(netParams, "mgL6wQ57i3fLPavd1LAJCubcmrk7QFTzoY");


        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("-----> coins resceived: " + tx.getHashAsString());
                System.out.println("received: " + tx.getValue(wallet));
            }
        });

        kit.wallet().addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("coins sent");
            }
        });

        kit.wallet().addKeyChainEventListener(new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
            }
        });

        kit.wallet().addScriptsChangeEventListener(new ScriptsChangeEventListener() {
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
            }
        });

        kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                System.out.println("-----> confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
            }
        });



    }

    public static void main(String[] args) throws Exception{


        /*WalletAppKit wk = new WalletAppKit(netParams, new File("."), "sendrequest-example-anand");
        System.out.println(wk);
        wk.setAutoSave(true);
        wk.startAsync();
        wk.awaitRunning();
        exit(123);*/

       /* Wallet wallet1 = Wallet.loadFromFile(new File("sendrequest-example-anand.wallet"));

        System.out.println(wallet1.toString());*/


        Wallet wallet = Wallet.loadFromFile(new File("sendrequest-example-anand.wallet"));

        System.out.println(wallet.toString());
        //exit(563);

        NetworkParameters params = TestNet3Params.get();
        WalletAppKit kit = new WalletAppKit(params, new File("."), "sendrequest-example-anand");



        kit.setAutoSave(true);
        kit.startAsync();

        kit.awaitRunning();
        Coin value = Coin.parseCoin("0.09");






        System.out.println(kit.wallet().getIssuedReceiveKeys());



        kit.wallet().getBalance();
        System.out.println(kit.wallet().getBalance());

        System.out.println("Send money to: " + kit.wallet().currentReceiveAddress().toString());

        // How much coins do we want to send?
        // The Coin class represents a monetary Bitcoin value.
        // We use the parseCoin function to simply get a Coin instance from a simple String.


        // To which address you want to send the coins?
        // The Address class represents a Bitcoin address.
        Address to = Address.fromBase58(params, "mupBAFeT63hXfeeT4rnAUcpKHDkz1n4fdw");

        // There are different ways to create and publish a SendRequest. This is probably the easiest one.
        // Have a look at the code of the SendRequest class to see what's happening and what other options you have: https://bitcoinj.github.io/javadoc/0.11/com/google/bitcoin/core/Wallet.SendRequest.html
        //
        // Please note that this might raise a InsufficientMoneyException if your wallet has not enough coins to spend.
        // When using the testnet you can use a faucet (like the http://faucet.xeno-genesis.com/) to get testnet coins.
        // In this example we catch the InsufficientMoneyException and register a BalanceFuture callback that runs once the wallet has enough balance.
        try {
            Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            System.out.println("coins sent. transaction hash: " + result.tx.getHashAsString());
            // you can use a block explorer like https://www.biteasy.com/ to inspect the transaction with the printed transaction hash.
        } catch (InsufficientMoneyException e) {
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
            System.out.println("Send money to: " + kit.wallet().currentReceiveAddress().toString());

            // Bitcoinj allows you to define a BalanceFuture to execute a callback once your wallet has a certain balance.
            // Here we wait until the we have enough balance and display a notice.
            // Bitcoinj is using the ListenableFutures of the Guava library. Have a look here for more information: https://github.com/google/guava/wiki/ListenableFutureExplained
            ListenableFuture<Coin> balanceFuture = kit.wallet().getBalanceFuture(value, Wallet.BalanceType.AVAILABLE);
           /* FutureCallback<Coin> callback = new FutureCallback<Coin>() {
                @Override
                public void onSuccess(Coin balance) {
                    System.out.println("coins arrived and the wallet now has enough balance");
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("something went wrong");
                }
            };
            Futures.addCallback(balanceFuture, callback);*/
        }

    }

    @Override
    public AdminConfig getAdminConfig() {
        return null;
    }
}


//mrigrj7kzwE3Gki567xZk32pJCzdAP9qGh - anand
// msRErs4qrpXpYXN2792HkLpZ6LSu3LffZr - test

