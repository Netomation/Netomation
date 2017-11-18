package com.netomation.api;

import facebook4j.Facebook;
import twitter4j.Twitter;

public class FacebookWrapper implements SocialNetwork {

    private static FacebookWrapper instance;

    public static FacebookWrapper getInstance() {
        if(instance == null)
            instance = new FacebookWrapper();
        return instance;
    }

    private FacebookWrapper() {

    }

    @Override
    public Object expose() {
        return null;
    }

    @Override
    public long getOwnID() {
        return 0;
    }

    @Override
    public Object getUser(long id) {
        return null;
    }

    @Override
    public void blockUser(long id) {

    }

    @Override
    public void unblockUser(long id) {

    }

    @Override
    public void sendPrivateMessage(long id, String msg) {

    }
}
