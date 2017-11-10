package com.netomation.api;

import facebook4j.Facebook;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class FacebookWrapper extends SocialNetwork<Facebook> {

    public static FacebookWrapper getInstance() {
        if(instance == null)
            instance = new FacebookWrapper();
        return (FacebookWrapper)instance;
    }

    private FacebookWrapper() {

    }

    @Override
    public void authenticate() {

    }

    @Override
    public void blockUser() {

    }

    @Override
    public void unblockUser() {

    }

    @Override
    public void sendPrivateMessage() {

    }
}