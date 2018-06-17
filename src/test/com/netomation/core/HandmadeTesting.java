package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.core.WebsiteListener;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Messages;
import twitter4j.Twitter;

import java.util.ArrayList;
import java.util.Date;

public class HandmadeTesting {

    public static void main(String[] args) {

        MongoTestingUtils.addRandomUserToDb();


        System.exit(0);
        WebsiteListener listener = new WebsiteListener();
        listener.start();
        try{listener.join();}catch (Exception exp){exp.printStackTrace();}
        System.exit(0);

        System.out.println(getTwitterID("IaakovExman"));
        System.exit(0);
        MongoCache.getInstance().deleteAllDataFromDatabase();

        for(long i = 25000000 ; i < (25000000 + 100000) ; i++) {
            UserImpl user = new UserImpl(null);
            user.setId(i);
            user.setFirstName(MongoTestingUtils.getRandomString());
            user.setLastName(MongoTestingUtils.getRandomString());
            user.setGeoLocation(MongoTestingUtils.getRandomString());
            user.setDescription(MongoTestingUtils.getRandomString());
            user.setLanguage(MongoTestingUtils.getRandomString());
            MongoCache.getInstance().putToUsersTable(user);
        }

        System.exit(0);
        UserImpl user1 = new UserImpl(null);
        user1.setId(1);
        user1.setFirstName("Avihu");
        user1.setLastName("Harush");
        user1.setGeoLocation("Israel");
        MongoCache.getInstance().putToUsersTable(user1);

        UserImpl user2 = new UserImpl(null);
        user2.setId(1);
        user2.setFirstName("Avihu");
        user2.setLastName("Harush");
        user2.setGeoLocation("Jordan");
        user2.setFirstMeetTimestamp(new Date());
        MongoCache.getInstance().putToUsersTable(user2);

        SocialNetwork.SocialNetworkPrivateMessage message = new SocialNetwork.SocialNetworkPrivateMessage();
        message.setFromUserId(1);
        message.setToUserId(13);
        message.setContent("Testing content");
        MongoCache.getInstance().addMessageToUser(1, message);


        SocialNetwork.SocialNetworkPrivateMessage message2 = new SocialNetwork.SocialNetworkPrivateMessage();
        message2.setFromUserId(13);
        message2.setToUserId(1);
        message2.setContent("Response Testing content");
        MongoCache.getInstance().addMessageToUser(1, message2);

        MongoCache.getInstance().userFollowUs(user2);
        MongoCache.getInstance().followingUser(user2);
        MongoCache.getInstance().userStoppedFollowUs(user2);

        System.out.println(Messages.generateMessage(4544365));

        System.exit(0);

        SocialNetwork twitter = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        twitter.setCredentials(Globals.CREDENTIALS[0], Globals.CREDENTIALS[1], Globals.CREDENTIALS[2], Globals.CREDENTIALS[3]);
        try {
            SocialNetwork.SocialNetworkUser user = twitter.getUser(twitter.getOwnID());
            MongoCache.getInstance().putToUsersTable(user);
        } catch (Exception exp){exp.printStackTrace();}
    }

    private static long getTwitterID(String name) {
        SocialNetwork socialNetwork = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        socialNetwork.setCredentials(Globals.CREDENTIALS);
        try {
            return ((Twitter)socialNetwork.expose()).showUser(name).getId();
        } catch (Exception exp) {exp.printStackTrace();}
        return -1;
    }

    private static class UserImpl extends SocialNetwork.SocialNetworkUser {

        public UserImpl(Object originalUserObject) {
            super(originalUserObject);
        }

        @Override
        protected String mapFirstName() {
            return null;
        }

        @Override
        protected String mapLastName() {
            return null;
        }

        @Override
        protected Object mapId() {
            return null;
        }

        @Override
        protected Object mapParentId() {
            return null;
        }

        @Override
        protected String mapDescription() {
            return null;
        }

        @Override
        protected Object mapGeoLocation() {
            return null;
        }

        @Override
        protected Object mapLanguage() {
            return null;
        }

        @Override
        public ArrayList<SocialNetwork.SocialNetworkUser> getExpansionGroup() {
            return null;
        }
    }


    public int show_numbers(int wanted) {
        return show_numbers(wanted, wanted, 0, 0);
    }

    private int show_numbers(int wanted, int leftover, int digit, int counter) {
        if(leftover == 0)
            if(digit < 9)
                return Math.max(counter, show_numbers(wanted, wanted, digit + 1, 0));
            else
                return counter;
        if(leftover % 10 == digit)
            return show_numbers(wanted, leftover / 10, digit, ++counter);
        else
            return show_numbers(wanted, leftover / 10, digit, counter);
    }
}
