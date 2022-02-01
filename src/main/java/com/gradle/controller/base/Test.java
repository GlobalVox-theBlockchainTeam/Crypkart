/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.base;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws Exception {
        byte[] b;
        NetworkParameters np = TestNet3Params.get();
        Context.propagate(new Context(np));
        b = Files.readAllBytes(new File("genesis.bin").toPath());
        Transaction tx1 = new Transaction(np, b);
        tx1.getInput(0).getScriptSig().isSentToMultiSig();
        System.out.println(tx1);
        byte[] pk = tx1.getOutput(0).getScriptPubKey().getPubKey();
        System.out.println(bytesToHex(pk));
        System.out.println(bytesToHex(hash160(pk)));
        Address a = new Address(np, hash160(pk));
        System.out.println(a);
    }
    static byte[] hash160(byte[] in) {
        MessageDigest d1;
        try {
            d1 = MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        d1.update(in);
        byte[] digest = d1.digest();
        RIPEMD160Digest d2 = new RIPEMD160Digest();
        d2.update(digest, 0, 32);
        byte[] ret = new byte[20];
        d2.doFinal(ret, 0);
        return ret;
    }
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
