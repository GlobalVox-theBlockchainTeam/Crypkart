/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller;

import com.google.common.util.concurrent.ListenableFuture;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.PeerDataEventListener;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.*;

import javax.annotation.Nullable;
import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class App {


    protected static final NetworkParameters netParams = TestNet3Params.get();
    protected static final NetworkParameters regNetParams = RegTestParams.get();
    protected static final NetworkParameters prodNetParams = MainNetParams.get();

    public static void main1(String[] args) throws BlockStoreException, UnknownHostException, Exception {

        NetworkParameters params = netParams;
        Wallet wallet = new Wallet(params);
        ECKey key = new ECKey();
        System.out.println("Public address: " +
                key.toAddress(params).toString());
        System.out.println("Private key: " +
                key.getPrivateKeyEncoded(params).toString());
        wallet.addKey(key);
        File file = new File("my-blockchain");
        SPVBlockStore store = new SPVBlockStore(params, file);
        BlockChain chain = new BlockChain(params, wallet, store);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.addWallet(wallet);
        peerGroup.start();
        peerGroup.downloadBlockChain();
        wallet.addEventListener(new AbstractWalletEventListener() {
            public void onCoinsReceived(Wallet wallet,
                                        Transaction tx, BigInteger prevBalance,
                                        BigInteger newBalance) {
                System.out.println("Hello Money! Balance: "
                        + newBalance + " satoshis");
            }
        });
        while (true) {
        }
    }


    public static void main2(String[] args) throws BlockStoreException, AddressFormatException,
            InterruptedException, ExecutionException, Exception {
        NetworkParameters params = regNetParams;
        PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);
        File file = new File("my-blockchain");
        SPVBlockStore store = new SPVBlockStore(params, file);

        Wallet wallet = new Wallet(params);
        DumpedPrivateKey key = DumpedPrivateKey.fromBase58(params, "cVoJibjkotckqY3VJAzaxzwRW7uYNUuzSk5PmUALFuqHW7DPrn7i");
        wallet.importKey(key.getKey());
        BlockChain chain = new BlockChain(params, wallet,
                store);


        PeerGroup peerGroup = new PeerGroup(params, chain);
        //peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.addAddress(pa);
        peerGroup.addWallet(wallet);
        peerGroup.start();
        peerGroup.downloadBlockChain();
        Coin balance = wallet.getBalance();
        System.out.println("Wallet balance: " + balance);
       /* Address destinationAddress = new Address(params,
                "1BTCorgHwCg6u2YSAWKgS17qUad6kHmtQW");
        BigInteger fee=BigInteger.valueOf(10000);*/
        /*Wallet.SendRequest req = Wallet.SendRequest.to(destinationAddress,balance.subtract(fee));
        req.fee = fee;
        Wallet.SendResult result = wallet.sendCoins(peerGroup, req);
        if(result != null)
        {
            result.broadcastComplete.get();
            System.out.println("The money was sent!");
        }
        else
        {
            System.out.println("Something went wrong sending the money.");
        }*/
    }


    public static void main3(String[] args) throws Exception {
        /*Context.getOrCreate(netParams);
        NetworkParameters params = netParams;
        PeerAddress pa = new PeerAddress(regNetParams, InetAddress.getByName("127.0.0.1"), 19000);
        String filePrefix = "peer2-testnet";
        WalletAppKit kit = new WalletAppKit(params, new File("."), filePrefix);
        //kit.setPeerNodes(pa);
        // Download the block chain and wait until it's done.
        kit.startAsync();
        kit.awaitRunning();
        String ads = "2N9dNtXzPLbDWzte5AUcVb9LcTq5v3HjLRM ";
        Address address = new Address(params, ads);
        Wallet wallet = new Wallet(params);
        wallet.addWatchedAddress(address, 0);
        System.out.println("wallet.getWatchedAddresses()"+wallet.getWatchedAddresses());
        BlockChain chain;
        try {
            chain = new BlockChain(params, wallet,
                    new MemoryBlockStore(params));

            PeerGroup peerGroup = new PeerGroup(params, chain);
            peerGroup.addPeerDiscovery(new DnsDiscovery(params));
            peerGroup.addWallet(wallet);
            peerGroup.start();
            peerGroup.downloadBlockChain();
            Coin balance = wallet.getBalance();
            System.out.println("Wallet balance: " + balance);
        } catch (BlockStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/


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
    }


    public static void main4(String[] args) throws Exception {
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
        System.out.println(wallet.getBalance());
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


    public static void main5(String[] args) throws Exception {
        NetworkParameters params = netParams;
        Wallet wallet = new Wallet(params);
        ECKey key = new ECKey();
        System.out.println("Public address: " +
                key.toAddress(params).toString());
        System.out.println("Private key: " +
                key.getPrivateKeyEncoded(params).toString());
        wallet.addKey(key);
        File file = new File("my-blockchain");
        SPVBlockStore store = new SPVBlockStore(params, file);
        BlockChain chain = new BlockChain(params, wallet, store);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
        peerGroup.addWallet(wallet);
        peerGroup.start();
        peerGroup.downloadBlockChain();
        wallet.addEventListener(new AbstractWalletEventListener() {
            public void onCoinsReceived(Wallet wallet,
                                        Transaction tx, BigInteger prevBalance,
                                        BigInteger newBalance) {
                System.out.println("Hello Money! Balance: "
                        + newBalance + " satoshis");
            }
        });
        while (true) {
        }
    }


    public static void main(String[] args) throws Exception {
        BriefLogFormatter.init();
        System.out.println("Connecting to node");
        final NetworkParameters params = TestNet3Params.get();

        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, blockStore);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.start();
        PeerAddress addr = new PeerAddress(InetAddress.getLocalHost(), params.getPort());
        peerGroup.addAddress(addr);
        peerGroup.waitForPeers(1).get();
        Peer peer = peerGroup.getConnectedPeers().get(0);

        Sha256Hash blockHash = Sha256Hash.wrap("0000000000016b1e8785ca22b6c3531375bffa4cafc16fd822258a6a755edadc");
        Future<Block> future = peer.getBlock(blockHash);
        System.out.println("Waiting for node to send us the requested block: " + blockHash);
        Block block = future.get();
        System.out.println(block);


        List<Transaction> l = block.getTransactions();
        for (Transaction tx : l) {
            System.out.println(tx.toString());
            List<TransactionInput> in = tx.getInputs();
            for (TransactionInput i : in) {
                try {
                    System.out.println(i.getScriptSig().getFromAddress(netParams));
                } catch (Exception e) {
                    System.out.println("error");
                }

            }
        }

        peerGroup.stopAsync();
    }

// transaction id: 2962601632eecbbce8f58df89d15ac3d0860da3508fa661c1cba044b967ddc4e
}
//cVkay64AVQWje9JjhXRcEs1KTDP1idnsfs5fDs5PTAkpqrtT7DRR

//Public address: msU7mKxh4S63bSpHjnL2s2YNkF7iizkYkv
//Private key: cQ5wAiy8mdkhmdrzc3mH1EvBVD5EjATjHv5NpvgS615pjmkRJugG


//Public address: mptZqjWDLDbX2RUHimDG5YrojqoEQ4KGkD
//Private key: cN7mHzouQjkFs8AupuwVpkeuGGEwPGSWeDiQq9NghGXD5e7FFh3U