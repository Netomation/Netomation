package main.com.netomation.core;

import com.mongodb.Mongo;
import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Filter;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Preferences;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static Worker worker;
    private static Listener listener;
    private static SocialNetwork socialNetwork;
    private static WebsiteListener websiteListener;


    public static void main(String[] args) {
        initProgram();
        letUserReviewProperties();
        System.out.println("Creating SocialNetwork instance.");
        socialNetwork = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        socialNetwork.setCredentials(Globals.CREDENTIALS);
        listener = new Listener(socialNetwork);
        worker = new Worker(socialNetwork);
        websiteListener = new WebsiteListener();
        websiteListener.start();
        startSocialNetworkListener();
        try{listener.join();}catch (Exception exp){exp.printStackTrace();}
        try{worker.join();}catch (Exception exp){exp.printStackTrace();}
        try{websiteListener.join();}catch (Exception exp){exp.printStackTrace();}
    }

    public static void startWorker() {
        System.out.println("Starting Worker");
        if(worker == null) {
            worker = new Worker(socialNetwork);
            worker.start();
        } else if(!worker.isInterrupted()) {
            worker.start();
        }
    }

    public static void startSocialNetworkListener() {
        System.out.println("Starting Listener");
        if(listener == null) {
            listener = new Listener(socialNetwork);
            listener.start();
        } else if(!listener.isInterrupted()) {
            listener.start();
        }
    }

    public static void restartSocialNetworkListener() {
        delay(Globals.DELAY_BEFORE_TRYING_OPERATION_AGAIN + new Random().nextInt(Globals.DELAY_BEFORE_TRYING_OPERATION_AGAIN_RANDOM_OFFSET));
        listener = null;
        startSocialNetworkListener();
    }

    private static void initProgram() {
        System.out.println("Setting up program.");
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
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
        System.out.println("Starting delay(time = " + time + ")");
        try{Thread.sleep(time);} catch (Exception ignore){}
        System.out.println("After delay(time = " + time + ")");
    }

    public static SocialNetwork.SocialNetworkPrivateMessage generatePrivateMessageObject(SocialNetwork.SocialNetworkUser user, String message) {
        SocialNetwork.SocialNetworkPrivateMessage toReturn = new SocialNetwork.SocialNetworkPrivateMessage();
        toReturn.setContent(message);
        toReturn.setToUserId(user.getId());
        toReturn.setFromUserId(socialNetwork.getOwnID());
        return toReturn;
    }

}