package test.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.api.SocialNetworkFactory;
import main.com.netomation.api.TwitterWrapper;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Globals;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {


        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        for(int i = 0 ; i < list.size() ; i++) {
            int s = list.get(i);
            if(s == 1)
                list.add(12);
            if(s == 2)
                list.add(122);
            if(s == 3)
                list.add(1222);
            System.out.println(s);
        }
        System.exit(0);
        MongoCache.getInstance().deleteAllDataFromDatabase();

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put(Globals.MONGO_DB_USER_ID_KEY, 1);
        map1.put("first_name", "Avihu");
        map1.put("last_name", "Harush");
        MongoCache.getInstance().putToUsersTable(map1);

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put(Globals.MONGO_DB_USER_ID_KEY, 2);
        map2.put("first_name", "Radik");
        map2.put("last_name", "Lianski");
        MongoCache.getInstance().putToUsersTable(map2);

        HashMap<String, Object> editMap = new HashMap<>();
        editMap.put(Globals.MONGO_DB_USER_ID_KEY, 1);
        editMap.put("first_name", "Gabi");
        editMap.put("last_name", "Simba");
        MongoCache.getInstance().putToUsersTable(editMap);

        MongoCache.getInstance().putToUsersTable(new UserImpl(null));



        System.exit(0);

        SocialNetwork twitter = SocialNetworkFactory.getSocialNetwork(TwitterWrapper.class);
        twitter.setCredentials(
                "9xjQZs17bpUCHvCG9pokGdJzi",
                "N7VKHt22ItDJhXqW4sjuS2Ea7CFdOc1Oa8Bu2NOTGIVHpzs7XW",
                "892388780753211392-JfFbaWlpfQb03rxQV6hZEBu12SDr3Sl",
                "SosL4XiNEBAtVGX7J7ElU9ptgqNWS0leK2vbO8nWN28dv"
        );
        try {
            SocialNetwork.SocialNetworkUser user = twitter.getUser(twitter.getOwnID());
            MongoCache.getInstance().putToUsersTable(user);
        } catch (Exception exp){exp.printStackTrace();}
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
