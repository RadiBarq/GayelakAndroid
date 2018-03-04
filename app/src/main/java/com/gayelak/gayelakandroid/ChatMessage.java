package com.gayelak.gayelakandroid;

import java.util.Date;


public class ChatMessage {

    public String messageText;
    public String messageFrom;
    public long messageTime;

    public ChatMessage(String messageText, String messageFrom)
    {
        this.messageText = messageText;
        this.messageFrom = messageFrom;
        Long tsLong = System.currentTimeMillis() / 1000;
        String stringLong = tsLong.toString();
        messageTime = Integer.parseInt(stringLong);
    }
}
