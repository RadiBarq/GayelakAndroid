package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 3/13/18.
 */

public class PushNotification
{
    String from;
    String to;
    String messageType;
    String itemId;

    PushNotification(String from, String to, String messageType, String itemId)
    {
        this.from = from;
        this.to = to;
        this.messageType = messageType;
        this.itemId = itemId;

    }
}
