/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.components.encrypter;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * This class has methods which encrypts path variables and decrypt it using specifig key and init vector
 */
public class PathVariableEncrypt implements Serializable {

    public static final Logger logger = Logger.getLogger(PathVariableEncrypt.class);
    private static final long serialVersionUID = 6714170988948899341L;


    /**
     *
     * @param value to be encrypted
     * @return encrypted value
     */
    public String encrypt(String value) {
        try {
            String encoded = EncryptionUtil.encode(value);

            /*IvParameterSpec iv = new IvParameterSpec(getInitVectorOrKey().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(getInitVectorOrKey().getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted));
            return URLEncoder.encode(Base64.encodeBase64String(encrypted), "UTF-8");*/
            return encoded;
            //return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            logger.error(ex);
            //ex.printStackTrace();
        }

        return null;
    }


    /**
     *
     * @param encrypted encrypted value which will be decrypted in this method
     * @return decrypted value
     */
    public String decrypt(String encrypted) {
        try {

            //encrypted = URLDecoder.decode(encrypted, "UTF-8");
            /*IvParameterSpec iv = new IvParameterSpec(getInitVectorOrKey().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(getInitVectorOrKey().getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);*/

            String decoded = EncryptionUtil.decode(encrypted);
            return decoded;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @return init vector and encryption key
     */
    public String getInitVectorOrKey(){
        return "rXlJe0tZZQqrz9C8";
    }








    private String keyString = "adkj@#$02#@adflkj)(*jlj@#$#@LKjasdjlkj<.,mo@#$@#kljlkdsu343";

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String getDecryptedUrlView(@RequestParam String d, @RequestParam String v) throws Exception {
        // The data goes out urlEncoded.. but comes in decoded (No Need to url decode on the request)
        Object obj = decryptObject(d, v);
        System.out.println("Object Decrypted: "+obj.toString());
        return "INDEX";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getEncryptedUrl(@PathVariable String id) {

        Map<String,String> map = new HashMap<String,String>();
        map.put("id",id);
        map.put("topSecret","Waffles are tasty");

        try {
            String[] encrypted = encryptObject(map);
            // url may differ.. based upon project initial context
            System.out.println("http://localhost:8080/view?d="+encrypted[0]+"&v="+encrypted[1]);
        }catch(Exception e) {
            //logger.debug("Unable to encrypt view id: "+id, e);
        }
        return "INDEX";
    }


    /**
     * Encrypts and encodes the Object and IV for url inclusion
     * @param obj
     * @return
     * @throws Exception
     */
    private String[] encryptObject(Object obj) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(stream);
        try {
            // Serialize the object
            out.writeObject(obj);
            byte[] serialized = stream.toByteArray();

            // Setup the cipher and Init Vector
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Hash the key with SHA-256 and trim the output to 128-bit for the key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(keyString.getBytes());
            byte[] key = new byte[16];
            System.arraycopy(digest.digest(), 0, key, 0, key.length);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            // encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // Encrypt & Encode the input
            byte[] encrypted = cipher.doFinal(serialized);
            byte[] base64Encoded = Base64.encodeBase64(encrypted);
            String base64String = new String(base64Encoded);
            String urlEncodedData = URLEncoder.encode(base64String,"UTF-8");

            // Encode the Init Vector
            byte[] base64IV = Base64.encodeBase64(iv);
            String base64IVString = new String(base64IV);
            String urlEncodedIV = URLEncoder.encode(base64IVString, "UTF-8");

            return new String[] {urlEncodedData, urlEncodedIV};
        }finally {
            stream.close();
            out.close();
        }
    }

    /**
     * Decrypts the String and serializes the object
     * @param base64Data
     * @param base64IV
     * @return
     * @throws Exception
     */
    public Object decryptObject(String base64Data, String base64IV) throws Exception {
        // Decode the data
        byte[] encryptedData = Base64.decodeBase64(base64Data.getBytes());

        // Decode the Init Vector
        byte[] rawIV = Base64.decodeBase64(base64IV.getBytes());

        // Configure the Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(rawIV);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(keyString.getBytes());
        byte[] key = new byte[16];
        System.arraycopy(digest.digest(), 0, key, 0, key.length);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // Decrypt the data..
        byte[] decrypted = cipher.doFinal(encryptedData);

        // Deserialize the object
        ByteArrayInputStream stream = new ByteArrayInputStream(decrypted);
        ObjectInput in = new ObjectInputStream(stream);
        Object obj = null;
        try {
            obj = in.readObject();
        }finally {
            stream.close();
            in.close();
        }
        return obj;
    }



}
