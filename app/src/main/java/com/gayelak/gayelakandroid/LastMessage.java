package com.gayelak.gayelakandroid;

import java.util.Date;

/**
 * Created by radibarq on 2/26/18.
 */

public class LastMessage {
    public String message;
    public String recent;
    public double time;

    public LastMessage(String message, String recent)
    {
        this.message = message;
        this.recent = recent;
        Long tsLong = System.currentTimeMillis() / 1000;
        String stringLong = tsLong.toString();
        time = Integer.parseInt(stringLong);
    }

    public LastMessage()
    {


    }
}
