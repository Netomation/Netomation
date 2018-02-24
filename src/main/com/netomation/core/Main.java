package main.com.netomation.core;

import com.mongodb.Mongo;
import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Filter;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Preferences;

public class Main {

    private static Worker worker;
    private static Listener listener;
    private static SocialNetwork socialNetwork;


    public static void main(String[] args) {
        initProgram();
        letUserReviewProperties();
        System.out.println("Creating SocialNetwork instance.");
        socialNetwork = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        socialNetwork.setCredentials(Globals.CREDENTIALS);
        System.out.println("Starting Listener");
        startListener();
    }

    public static void startWorker() {
        if(worker == null) {
            worker = new Worker(socialNetwork);
            worker.start();
        }
    }

    public static void startListener() {
        if(listener == null) {
            listener = new Listener(socialNetwork);
            listener.start();
        }
    }

    public static void restartListener() {
        delay(Globals.DELAY_BEFORE_TRYING_OPERATION_AGAIN);
        listener = null;
        startListener();
    }

    private static void initProgram() {
        System.out.println("Setting up program.");
        Preferences.initPreferences();
        Filter.initFilter();
        while(!MongoCache.validConnection()) {
            System.out.println("Error while connecting to MongoDB (Press any key to try again)...");
            try{System.in.read();}catch (Exception exp){exp.printStackTrace();}
            Preferences.initPreferences();
            Filter.initFilter();
        }
    }

    private static void letUserReviewProperties() {
        System.out.println("Please review the current execution properties.\n" +
                "After reviewing and editing the properties as required\n" +
                "press enter key to continue...");
        try{System.in.read();}catch (Exception exp){exp.printStackTrace();}
        initProgram();
    }

    public static void delay(int time) {
        try{Thread.sleep(time);} catch (Exception ignore){}
    }

    public static SocialNetwork.SocialNetworkPrivateMessage generatePrivateMessageObject(SocialNetwork.SocialNetworkUser user, String message) {
        SocialNetwork.SocialNetworkPrivateMessage toReturn = new SocialNetwork.SocialNetworkPrivateMessage();
        toReturn.setContent(message);
        toReturn.setToUserId(user.getId());
        toReturn.setFromUserId(socialNetwork.getOwnID());
        return toReturn;
    }

}