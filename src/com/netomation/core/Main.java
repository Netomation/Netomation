package com.netomation.core;

import com.netomation.api.FacebookWrapper;
import com.netomation.api.SocialNetworkWrapper;
import com.netomation.api.TwitterWrapper;

public class Main {

    public static void main(String[] args) {

        // Create Twitter instance
        SocialNetworkWrapper twitter = SocialNetworkWrapper.getInstance(TwitterWrapper.class);

        // Create Facebook instance
        SocialNetworkWrapper facebook = SocialNetworkWrapper.getInstance(FacebookWrapper.class);

    }
}