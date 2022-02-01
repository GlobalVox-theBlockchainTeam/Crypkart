/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.msg;

public class OutputMessage {

    private String from;
    private String message;
    private String time;
    private boolean myMsg;
    private String url;
    private String tradeId;

    public OutputMessage(String from, String message, String time, boolean myMsg) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.myMsg = myMsg;
    }
    public OutputMessage(String from, String message, String time, boolean myMsg, String tradeId) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.myMsg = myMsg;
        this.tradeId=tradeId;
    }

    public OutputMessage() {
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public boolean isMyMsg() {
        return myMsg;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getTradeId() {
        return tradeId;
    }
}
