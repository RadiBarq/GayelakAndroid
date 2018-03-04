package com.gayelak.gayelakandroid;

import java.sql.Timestamp;

/**
 * Created by radibarq on 2/15/18.
 */

public class Notification {

    String itemId;
    String recent;
    double timestamp;
    String type;
    String userId;
    String userName;
    String notificationId;

    Notification()
    {

    }

    Notification(String itemId, String recent, double timestamp, String type, String userId, String userName, String notificationId)
    {
        this.itemId = itemId;
        this.recent = recent;
        this.timestamp = timestamp;
        this.type = type;
        this.userId = userId;
        this.userName = userName;
        this.notificationId = notificationId;

    }

}
