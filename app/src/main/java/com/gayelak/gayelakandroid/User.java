package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 1/17/18.
 */

public class User {



    // sorry for the confusion about the names, I know it's sucks

    public String Email;
    public String UserId;
    public String UserName;
    public String instanceId;




    public User()
    {

        // Default constructor required for calls to DataSnapshot.getValue(User.class)


    }



    public User(String email, String userId, String userName, String instanceId)
    {

        this.Email = email;
        this.UserId = userId;
        this.UserName = userName;
        this.instanceId = instanceId;

    }



}
