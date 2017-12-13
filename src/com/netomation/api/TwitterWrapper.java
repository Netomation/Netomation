package com.netomation.api;

import facebook4j.FacebookFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper implements SocialNetwork {

    private static TwitterWrapper instance;
    private Configuration configuration = null;
    private Twitter twitter = null;
    private long ownID;
    private String OAuthConsumerKey;
    private String OAuthConsumerSecret;
    private String OAuthAccessToken;
    private String OAuthAccessTokenSecret;

    public static TwitterWrapper getInstance() {
        if(instance == null)
            instance = new TwitterWrapper();
        return instance;
    }

    private TwitterWrapper() {
        configuration = getConfiguration();
        twitter = new TwitterFactory(configuration).getInstance();
        try {ownID = twitter.getId();}
        catch (TwitterException ignore) {}
    }

    @Override
    public Configuration getConfiguration() {
        return new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey(OAuthConsumerKey)
                .setOAuthConsumerSecret(OAuthConsumerSecret)
                .setOAuthAccessToken(OAuthAccessToken)
                .setOAuthAccessTokenSecret(OAuthAccessTokenSecret).build();
    }

    @Override
    public Twitter expose() {
        return twitter;
    }

    @Override
    public String getOwnID() {
        return ownID + "";
    }

    @Override
    public Object getUser(long id) {
        return null;
    }

    @Override
    public void blockUser(long id) {
        // TODO fill
    }

    @Override
    public void unblockUser(long id) {
        // TODO fill
    }

    @Override
    public void sendPrivateMessage(long id, String msg) {
        // TODO fill
    }

}