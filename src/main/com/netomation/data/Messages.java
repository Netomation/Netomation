package main.com.netomation.data;

import java.util.Random;

public abstract class Messages {

    public static String generateMessage() {
        return Globals.MESSAGES[new Random().nextInt(Globals.MESSAGES.length)];
    }

}
