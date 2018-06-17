package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.core.Listener;
import main.com.netomation.data.Filter;
import main.com.netomation.data.Preferences;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Preview {

    public static void main(String[] args) {
        startup();
        System.out.println("Creating SocialNetwork instance.");
        SocialNetwork socialNetwork = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        socialNetwork.setCredentials(getCredentials());
        new Listener(socialNetwork).start();
    }

    public static void startup() {
        printNetomationIcon();
        initProgram();
        letUserReviewProperties();
    }

    private static void letUserReviewProperties() {
        System.out.println("Please review the current execution properties.\n" +
                "After reviewing and editing the properties as required\n" +
                "press enter key to continue...");
        try{System.in.read();}catch (Exception exp){exp.printStackTrace();}
        initProgram();
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

    private static void printNetomationIcon() {
        System.out.println("" +
                "  _   _   _____   _____    ___    __  __      _      _____   ___    ___    _   _ \n" +
                " | \\ | | | ____| |_   _|  / _ \\  |  \\/  |    / \\    |_   _| |_ _|  / _ \\  | \\ | |\n" +
                " |  \\| | |  _|     | |   | | | | | |\\/| |   / _ \\     | |    | |  | | | | |  \\| |\n" +
                " | |\\  | | |___    | |   | |_| | | |  | |  / ___ \\    | |    | |  | |_| | | |\\  |\n" +
                " |_| \\_| |_____|   |_|    \\___/  |_|  |_| /_/   \\_\\   |_|   |___|  \\___/  |_| \\_|\n" +
                "                                                                                 " +
                "");
        try { Thread.sleep(700); }catch (Exception ignore) { }
    }

    private static String[] getCredentials() {
        return new String[] {
                "9xjQZs17bpUCHvCG9pokGdJzi",
                "N7VKHt22ItDJhXqW4sjuS2Ea7CFdOc1Oa8Bu2NOTGIVHpzs7XW",
                "892388780753211392-JfFbaWlpfQb03rxQV6hZEBu12SDr3Sl",
                "SosL4XiNEBAtVGX7J7ElU9ptgqNWS0leK2vbO8nWN28dv"
        };
    }
}
