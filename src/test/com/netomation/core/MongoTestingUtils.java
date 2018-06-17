package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.cache.MongoCache;

import java.util.ArrayList;
import java.util.Random;

public class MongoTestingUtils {


    public static SocialNetwork.SocialNetworkUser addRandomUserToDb() {
        MongoTestingUtils.UserImpl user = new MongoTestingUtils.UserImpl(null);
        int randomId;
        do {
            randomId = new Random().nextInt(Integer.MAX_VALUE);
        } while(MongoCache.getInstance().userExistInDB(randomId));
        user.setId(randomId);
        user.setFirstName(getRandomString());
        user.setLastName(getRandomString());
        user.setGeoLocation(getRandomString());
        MongoCache.getInstance().putToUsersTable(user);
        return user;
    }

    public static String getRandomString() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        StringBuilder toReturn = new StringBuilder();
        for(int i = 0 ; i < 8 ; i++)
            toReturn.append(chars.charAt(rand.nextInt(chars.length())));
        return toReturn.toString();
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

}
