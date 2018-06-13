package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.data.Globals;
import twitter4j.Twitter;
import twitter4j.User;

public class UsersIDs {

    public static SocialNetwork socialNetwork;
    public static Twitter twitter;

    public static void main(String[] args) {
        initProgram();
        printUsersIds("tchvu3", "yakirwin");
    }

    public static void initProgram() {
        socialNetwork = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        socialNetwork.setCredentials(Globals.CREDENTIALS);
        twitter = (Twitter)socialNetwork.expose();
    }

    public static void printUsersIds(String... ids) {
        for(int i = 0 ; i < ids.length ; i++) {
            try {
                User user = twitter.showUser(ids[i]);
                System.out.println("User: " + ids[i] + " id is: " + user.getId());
            } catch (Exception exp) {
                System.out.println("Could not find: " + ids[i]);
            }

        }
    }

}
