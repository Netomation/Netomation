package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import twitter4j.Twitter;

public class Main {

    public static void main(String[] args) {
        SocialNetwork twitter = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        twitter.setCredentials(
                "OAuthConsumerKey",
                "OAuthConsumerSecret",
                "OAuthAccessToken",
                "OAuthAccessTokenSecret"
        );
        try {
            ((Twitter)twitter.expose()).updateStatus("Testing Status");
        } catch (Exception exp){exp.printStackTrace();}
    }

}
