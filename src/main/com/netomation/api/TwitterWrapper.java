package main.com.netomation.api;

import main.com.netomation.core.Main;
import main.com.netomation.data.Filter;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Preferences;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.net.UnknownHostException;
import java.util.ArrayList;


public class TwitterWrapper extends SocialNetwork {

    private static TwitterWrapper instance;
    private Configuration configuration;
    private Twitter twitter = null;
    private long ownID;
    private String OAuthConsumerKey;
    private String OAuthConsumerSecret;
    private String OAuthAccessToken;
    private String OAuthAccessTokenSecret;

    public static TwitterWrapper getInstance() {
        if(instance == null)
            instance = new TwitterWrapper();
        return instance;
    }

    private TwitterWrapper() {
        configuration = getConfiguration();
        if(configuration != null)
            initTwitterObject();
    }

    private void initTwitterObject() {
        boolean execute = true;
        while(execute) {
            execute = false;
            twitter = new TwitterFactory(configuration).getInstance();
            try {ownID = twitter.getId();}
            catch (TwitterException exp) {execute = handleException(exp);}
        }
    }

    @Override
    public Configuration getConfiguration() {
        if(configuration != null) {
            return configuration;
        }
        if(OAuthConsumerKey == null || OAuthConsumerSecret == null || OAuthAccessToken == null || OAuthAccessTokenSecret == null)
            return null;
        return new ConfigurationBuilder()
                .setOAuthConsumerKey(OAuthConsumerKey)
                .setOAuthConsumerSecret(OAuthConsumerSecret)
                .setOAuthAccessToken(OAuthAccessToken)
                .setOAuthAccessTokenSecret(OAuthAccessTokenSecret).build();
    }

    @Override
    public boolean canSendPrivateMessage(SocialNetworkUser user) {
        Relationship relationship = null;
        boolean execute = true;
        while (execute) {
            execute = false;
            try {
                relationship = twitter.showFriendship(Long.parseLong(getOwnID().toString()), Long.parseLong(user.getId().toString()));
            } catch (Exception exp) {
                execute = handleException(exp);
            }
        }
        return relationship != null && relationship.canSourceDm();
    }

    @Override
    public SocialNetworkPrivateMessage mapPrivateMessage(Object privateMessage) {
        SocialNetworkPrivateMessage toReturn = new SocialNetworkPrivateMessage();
        DirectMessage message = (DirectMessage) privateMessage;
        toReturn.setContent(message.getText());
        toReturn.setTimestamp(message.getCreatedAt());
        toReturn.setFromUserId(message.getSenderId());
        toReturn.setToUserId(message.getRecipientId());
        return toReturn;
    }

    @Override
    public boolean createFriendship(Object id) {
        boolean execute = true;
        while(execute) {
            execute = false;
            try {
                twitter.createFriendship(Long.parseLong(id.toString()));
                return true;
            } catch (Exception exp) {execute = handleException(exp);}
        }
        return false;
    }

    public Object getListenerStream() {
        return new TwitterStreamFactory(getConfiguration()).getInstance();
    }

    @Override
    public Twitter expose() {
        return twitter;
    }

    @Override
    public Object getOwnID() {
        return ownID;
    }

    @Override
    public SocialNetworkUser getUser(Object id) {
        boolean execute = true;
        while(execute) {
            execute = false;
            try {
                return new TwitterUser(twitter.showUser(Long.parseLong(id.toString())));
            } catch (TwitterException exp) { execute = handleException(exp); }
        }
        return null;
    }

    @Override
    public ArrayList<SocialNetworkUser> getExpansionGroupByUser(Object id) {
        boolean execute = true;
        while(execute) {
            execute = false;
            try {
                SocialNetworkUser user = getUser(id);
                return user.getExpansionGroup();
            } catch (Exception exp){execute = handleException(exp);}
        }
        return null;
    }

    @Override
    public boolean sendPrivateMessage(Object id, String msg) {
//        return true;
        boolean execute = true;
        while(execute) {
            execute = false;
            try {
                twitter.sendDirectMessage(Long.parseLong(id.toString()), msg);
                return true;
            } catch (Exception exp) {execute = handleException(exp);}
        }
        return false;
    }

    @Override
    public void setCredentials(String... credentials) {
        this.OAuthConsumerKey = credentials[0];
        this.OAuthConsumerSecret = credentials[1];
        this.OAuthAccessToken = credentials[2];
        this.OAuthAccessTokenSecret = credentials[3];
        configuration = getConfiguration();
        if(configuration != null)
            initTwitterObject();
    }

    @Override
    public boolean shouldContactUser(SocialNetworkUser user) {
        return Filter.languagePassFilter(user.getLanguage().toString(), false);
    }

    private class TwitterUser extends SocialNetworkUser<twitter4j.User> {

        public TwitterUser(User originalUserObject) {
            super(originalUserObject);
        }

        @Override
        protected String mapFirstName() {
            return _user.getName().split(" ")[0];
        }

        @Override
        protected String mapLastName() {
            try{return _user.getName().split(" ")[1];}
            catch (Exception ignore){return _user.getName();}
        }

        @Override
        protected Object mapId() {
            return _user.getId();
        }

        @Override
        protected Object mapParentId() {
            return Globals.UNKNOWN_PARENT;
        }

        @Override
        protected String mapDescription() {
            return _user.getDescription();
        }

        @Override
        protected Object mapGeoLocation() {
            return _user.getLocation();
        }

        @Override
        protected Object mapLanguage() {
            return _user.getLang();
        }

        @Override
        public ArrayList<SocialNetworkUser> getExpansionGroup() {
            long cursor = -1;
            IDs ids = null;
            ArrayList<SocialNetworkUser> users = new ArrayList<>();
            do {
                boolean execute = true;
                while(execute) {
                    execute = false;
                    try {
                        ids = twitter.getFollowersIDs(Long.parseLong(this.getId().toString()), cursor);
                    } catch (Exception exp){execute = handleException(exp);}
                }
                for (long id : ids.getIDs()) {
                    users.add(getUser(id));
                    if(users.size() >= 1500)
                        return users;
                }
            } while ((cursor = ids.getNextCursor()) != 0 && users.size() < 1500);
            return users;
        }
    }

    public synchronized boolean handleException(Exception exp) {
        Globals.WaitingTypes type = getOperationFromStatusCode(exp);
        if(type == null) {
            return false;
        }
        TwitterException twitterException = (TwitterException)exp;
        System.out.println("A TwitterException has been thrown. Operation: " + type.toString() + "\n(Message: " + exp.getMessage() + ")");
        if(type == Globals.WaitingTypes.DONT_WAIT_JUST_TRY_AGAIN) {
            return true;
        } else if(type == Globals.WaitingTypes.WAIT_FIXED_TIME_AND_TRY_AGAIN) {
            Main.delay(Globals.DELAY_BEFORE_TRYING_OPERATION_AGAIN);
            return true;
        } else if(type == Globals.WaitingTypes.WAIT_SPECIFIC_TIME_AND_TRY_AGAIN) {
            int time = twitterException.getRateLimitStatus().getSecondsUntilReset();
            System.out.println("Waiting for " + time + " seconds...");
            Main.delay(1000* (time + 5));
            return true;
        } else if(type == Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN) {
            System.out.println("Waiting for new credentials...");
            while(!credentialsChanged())
                Main.delay(Globals.DELAY_BEFORE_TRYING_OPERATION_AGAIN);
            configuration = null;
            setCredentials(Globals.CREDENTIALS);
            initTwitterObject();
            return true;
        } else if(type == Globals.WaitingTypes.DONT_TRY_AGAIN) {
            return false;
        }
        return false;
    }

    private boolean credentialsChanged() {
        Preferences.initPreferences();
        if(!Globals.CREDENTIALS[0].equals(this.OAuthConsumerKey)) {
            return true;
        }
        if(!Globals.CREDENTIALS[1].equals(this.OAuthConsumerSecret)) {
            return true;
        }
        if(!Globals.CREDENTIALS[2].equals(this.OAuthAccessToken)) {
            return true;
        }
        if(!Globals.CREDENTIALS[3].equals(this.OAuthAccessTokenSecret)) {
            return true;
        }
        return false;
    }

    private static Globals.WaitingTypes getOperationFromStatusCode(Exception exp) {
        if(!(exp instanceof TwitterException))
            return null;
        int errorCode = ((TwitterException) exp).getErrorCode();
        if (exp.getCause() != null && exp.getCause().getClass().equals(UnknownHostException.class)) {
            return Globals.WaitingTypes.WAIT_FIXED_TIME_AND_TRY_AGAIN;
        }
        switch (errorCode) {
            case 3: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 13: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 17: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 32: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 34: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 36: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 44: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 50: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 63: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 64: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 68: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 87: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 88: return Globals.WaitingTypes.WAIT_SPECIFIC_TIME_AND_TRY_AGAIN;
            case 89: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 92: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 93: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 99: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 120: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 130: return Globals.WaitingTypes.WAIT_FIXED_TIME_AND_TRY_AGAIN ;
            case 131: return Globals.WaitingTypes.WAIT_FIXED_TIME_AND_TRY_AGAIN;
            case 135: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 144: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 150: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 151: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 160: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 161: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 179: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 185: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 186: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 187: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 205: return Globals.WaitingTypes.WAIT_FIXED_TIME_AND_TRY_AGAIN;
            case 215: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 220: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 226: return Globals.WaitingTypes.WAIT_FIXED_TIME_AND_TRY_AGAIN;
            case 231: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 251: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 261: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 271: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 272: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 323: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 324: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 325: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 326: return Globals.WaitingTypes.WAIT_UNTIL_ORDERD_TO_TRY_AGAIN;
            case 327: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 354: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 385: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            case 386: return Globals.WaitingTypes.DONT_TRY_AGAIN;
            default: return Globals.WaitingTypes.DONT_TRY_AGAIN;
        }
    }

}