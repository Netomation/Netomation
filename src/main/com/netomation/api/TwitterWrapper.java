package main.com.netomation.api;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;

public class TwitterWrapper extends SocialNetwork {

    private static TwitterWrapper instance;
    private Configuration configuration;
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
        if(configuration != null)
            initTwitterObject();
    }

    private void initTwitterObject() {
        twitter = new TwitterFactory(configuration).getInstance();
        try {ownID = twitter.getId();}
        catch (TwitterException exp) {exp.printStackTrace();}
    }

    @Override
    public Configuration getConfiguration() {
        if(OAuthConsumerKey == null || OAuthConsumerSecret == null || OAuthAccessToken == null || OAuthAccessTokenSecret == null)
            return null;
        return new ConfigurationBuilder()
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
    public SocialNetworkUser getUser(String id) {
        return null;
    }

    @Override
    public void blockUser(String id) {

    }

    @Override
    public void unblockUser(String id) {

    }

    @Override
    public ArrayList<SocialNetworkUser> getExpansionGroupByUser(String id) {
        return null;
    }

    @Override
    public void sendPrivateMessage(String id, String msg) {

    }

    @Override
    public void setCredentials(String... credentials) {
        this.OAuthConsumerKey = credentials[0];
        this.OAuthConsumerSecret = credentials[1];
        this.OAuthAccessToken = credentials[2];
        this.OAuthAccessTokenSecret = credentials[3];
        configuration = getConfiguration();
        if(configuration != null)
            initTwitterObject();
    }


}