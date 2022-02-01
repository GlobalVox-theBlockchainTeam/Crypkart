/*
 * Copyright (c) 30/4/18 2:44 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class SendSMS {

    public static void main1(String[] args)
    {
        //Your authentication key
        String authkey = "112278Auw1fQ4YH3g572c36e2";
        //Multiple mobiles numbers separated by comma
        String mobiles = "9687048668";
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "COINMT";
        //Your message to send, Add URL encoding here.
        String message = "This is your OTP";
        //define route
        String route="4";
        String countryCode = "91";

        //Prepare Url
        URLConnection myURLConnection=null;
        URL myURL=null;
        BufferedReader reader=null;

        //encoding message
        String encoded_message=URLEncoder.encode(message);

        //Send SMS API
        String mainUrl="http://api.msg91.com/api/sendhttp.php?";

        //Prepare parameter string
        StringBuilder sbPostData= new StringBuilder(mainUrl);
        sbPostData.append("authkey="+authkey);
        sbPostData.append("&mobiles="+mobiles);
        sbPostData.append("&message="+encoded_message);
        sbPostData.append("&route="+route);
        sbPostData.append("&country="+countryCode);
        sbPostData.append("&encrypt="+"");
        sbPostData.append("&sender="+senderId);

        //final string
        mainUrl = sbPostData.toString();
        try
        {
            //prepare connection
            myURL = new URL(mainUrl);

            System.out.println(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            //reading response
            String response;
            while ((response = reader.readLine()) != null)
                //print response
                System.out.println(response);

            //finally close connection
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static char[] OTP(int len)
    {
        System.out.println("Generating OTP using random() : ");
        System.out.print("You OTP is : ");

        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return otp;
    }
    public static void main(String[] args)
    {
        int length = 6;
        System.out.println(OTP(length));
    }
}
