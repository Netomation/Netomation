package com.netomation.api;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper implements SocialNetwork {

    private static TwitterWrapper instance;

    public static TwitterWrapper getInstance() {
        if(instance == null)
            instance = new TwitterWrapper();
        return instance;
    }

    private TwitterWrapper() {

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