package main.com.netomation.data;

import java.util.Random;

public abstract class Messages {

    public static String generateMessage(Object userID) {
        return Globals.MESSAGES[new Random().nextInt(Globals.MESSAGES.length)]
                + Globals.DDNS + ":" + Globals.DDNS_PORT + "/" + userID.toString();
    }

}
