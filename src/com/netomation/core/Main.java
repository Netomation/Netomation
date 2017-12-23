package com.netomation.core;

import com.netomation.api.SocialNetwork;
import com.netomation.api.SocialNetworkFactory;
import com.netomation.api.TwitterWrapper;
import com.netomation.data.Filter;
import com.netomation.data.Preferences;

public class Main {

    public static void main(String[] args) {
        initProgram();

        System.exit(0);

        // Dynamic creation, can be at run time!
        SocialNetwork twitter1 = SocialNetworkFactory.getSocialNetwork("twitter");

        // Static creation
        SocialNetwork twitter2 = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);

        // The factory class keep the latest social network instantiated
        SocialNetwork latest = SocialNetworkFactory.getLatest(); // latest equals now to "twitter2"

    }

    private static void initProgram() {
        Preferences.initPreferences();
        Filter.initFilter();
    }

}