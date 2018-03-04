package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 2/27/18.
 */

public class Report {

    String description;
    String reportedUser;
    String reportingUser;
    double tiemstamp;
    String type;

    public Report(String description, String reportedUser, String reportingUser, String type)
    {
        this.reportedUser =reportedUser;
        this.reportingUser = reportingUser;
        this.type = type;
        this.description = description;
        Long tsLong = System.currentTimeMillis() / 1000;
        String stringLong = tsLong.toString();
        tiemstamp = Integer.parseInt(stringLong);
    }

    public Report()
    {



    }

}
