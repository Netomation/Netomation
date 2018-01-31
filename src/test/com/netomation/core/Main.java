package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import twitter4j.Twitter;

public class Main {

    public static void main(String[] args) {
        final String OAuthConsumerKey = System.getenv("OAUTH_CONSUMER_KEY");
        final String OAuthConsumerSecret = System.getenv("OAUTH_CONSUMER_SECRET");
        final String OAuthAccessToken = System.getenv("OAUTH_ACCESS_TOKEN");
        final String OAuthAccessTokenSecret = System.getenv("OAUTH_ACCESS_TOKEN_SECRET");

        SocialNetwork twitter = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        twitter.setCredentials(
                OAuthConsumerKey,
                OAuthConsumerSecret,
                OAuthAccessToken,
                OAuthAccessTokenSecret
        );
        try {
            ((Twitter)twitter.expose()).updateStatus("Testing Status");
        } catch (Exception exp){exp.printStackTrace();}
    }

}
