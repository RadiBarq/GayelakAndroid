package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 2/21/18.
 */

public class ChatRoom {


    ChatMessage chatMessage;
    String user;
    String currentUser;

    ChatRoom(ChatMessage chatMessage, String user, String currentUser)
    {
        this.chatMessage = chatMessage;
        this.user = user;
        this.currentUser = currentUser;
    }



}
