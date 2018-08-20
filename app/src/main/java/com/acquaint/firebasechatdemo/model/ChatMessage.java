package com.acquaint.firebasechatdemo.model;

import java.net.URL;
import java.util.Date;

/**
 * Created by acquaint on 10/5/18.
 */

public class ChatMessage {

    private String messageText;
    private URL url;


    public ChatMessage(URL url, String messageUser) {
        this.url = url;
        this.messageUser = messageUser;
    }

    public ChatMessage(String messageText, URL url, String messageUser) {
        this.messageText = messageText;
        this.url = url;
        this.messageUser = messageUser;
    }



    public URL getUrl() {

        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public ChatMessage() {
    }



    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;

        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }

    private String messageUser;
    private long messageTime;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
