package main.com.netomation.data;

import main.com.netomation.cache.MongoCache;

import java.util.Random;

public abstract class Messages {

    public static String generateMessage(Object userID) {
        return Globals.MESSAGES[new Random().nextInt(Globals.MESSAGES.length)] + getSite(userID);
    }

    private static String getSite(Object userID) {
        //return Globals.CALL_FOR_PAPERS_URL;
        return Globals.DDNS + ":" + Globals.DDNS_PORT + "/" + MongoCache.getInstance().getDbKeyByUserId(userID);
    }

}
