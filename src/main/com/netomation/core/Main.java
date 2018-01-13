package main.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.data.Filter;
import main.com.netomation.data.Preferences;

public class Main {

    public static void main(String[] args) {
        initProgram();
        letUserReviewProperties();
        Preferences.initPreferences();
        SocialNetwork twitter = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        Worker worker = new Worker(twitter);
        worker.start();
    }

    private static void initProgram() {
        Preferences.initPreferences();
        Filter.initFilter();
    }

    private static void letUserReviewProperties() {
        System.out.println("Please review the current execution properties.\n" +
                "After reviewing and editing the properties as required\n" +
                "press enter key to continue...");
        try{System.in.read();}catch (Exception exp){exp.printStackTrace();}
    }

}