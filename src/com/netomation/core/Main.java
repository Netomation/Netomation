package com.netomation.core;

import com.netomation.api.SocialNetwork;
import com.netomation.api.SocialNetworkFactory;
import com.netomation.api.TwitterWrapper;
import com.netomation.cache.Cache;

public class Main {

    public static void main(String[] args) {

        Cache.createRow()
                .addData("Name", "gabi")
                .addData("Location", "Israel")
                .addData("ID", 154265)
                .addData("Input_Messages", 12)
                .addData("Output_Messages", 12)
                .commit();

        // Dynamic creation, can be at run time!
        SocialNetwork twitter1 = SocialNetworkFactory.getSocialNetwork("twitter");

        // Static creation
        SocialNetwork twitter2 = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);

        // The factory class keep the latest social network instantiated
        SocialNetwork latest = SocialNetworkFactory.getLatest(); // latest equals now to "twitter2"

    }
}