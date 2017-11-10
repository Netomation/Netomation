package com.netomation.api;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterWrapper extends SocialNetwork<Twitter> {

    private Configuration configuration = null;
    private long ownID;

    public static TwitterWrapper getInstance() {
        if(instance == null)
            instance = new TwitterWrapper();
        return (TwitterWrapper)instance;
    }

    private TwitterWrapper() {
        authenticate();
        socialNetwork = new TwitterFactory(configuration).getInstance();
        while (ownID <= 0) {
            try {ownID = socialNetwork.getId();}
            catch (TwitterException exp) { }
        }
    }

    @Override
    public void authenticate() {
        configuration =
                new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey("setOAuthConsumerKey")
                .setOAuthConsumerSecret("setOAuthConsumerSecret")
                .setOAuthAccessToken("setOAuthAccessToken")
                .setOAuthAccessTokenSecret("setOAuthAccessTokenSecret")
                .build();
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
