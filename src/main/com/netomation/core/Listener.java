package main.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Messages;
import twitter4j.*;

public class Listener extends Thread {

    private SocialNetwork socialNetwork;
    private boolean killThread = false;

    public Listener(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void run() {
        if(!socialNetwork.isConnected()) {
            return;
        }
        TwitterStream socialNetworkStream = (TwitterStream)socialNetwork.getListenerStream();
        socialNetworkStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {
            public void onConnect() {
                //Main.startWorker(); TODO this method should be uncommented!
            }
            public void onDisconnect() {
                killThread = true;
                Main.restartSocialNetworkListener();
            }
            public void onCleanUp() {}
        });
        socialNetworkStream.addListener(new UserStreamListener() {
            @Override
            public void onDirectMessage(DirectMessage directMessage) {
                receivedMessage(directMessage);
            }
            @Override
            public void onException(Exception e) { }
            @Override
            public void onDeletionNotice(long l, long l1) { }
            @Override
            public void onFriendList(long[] longs) { }
            @Override
            public void onFavorite(User user, User user1, Status status) { }
            @Override
            public void onUnfavorite(User user, User user1, Status status) { }
            @Override
            public void onFollow(User user, User user1) {
                freezeProgramIfNeeded();
                if(!String.valueOf(user.getId()).equals(socialNetwork.getOwnID().toString()) && String.valueOf(user1.getId()).equals(socialNetwork.getOwnID().toString())) {
                    System.out.println("User: " + user.getName() + " started following us.");
                    MongoCache.getInstance().userFollowUs(socialNetwork.getUser(user.getId()));
                }
            }
            @Override
            public void onUnfollow(User user, User user1) {
                freezeProgramIfNeeded();
                if(!String.valueOf(user.getId()).equals(socialNetwork.getOwnID().toString()) && String.valueOf(user1.getId()).equals(socialNetwork.getOwnID().toString())) {
                    System.out.println("User: " + user.getName() + " stopped following us.");
                    MongoCache.getInstance().userStoppedFollowUs(socialNetwork.getUser(user.getId()));
                }
            }
            @Override
            public void onUserListMemberAddition(User user, User user1, UserList userList) { }
            @Override
            public void onUserListMemberDeletion(User user, User user1, UserList userList) { }
            @Override
            public void onUserListSubscription(User user, User user1, UserList userList) { }
            @Override
            public void onUserListUnsubscription(User user, User user1, UserList userList) { }
            @Override
            public void onUserListCreation(User user, UserList userList) { }
            @Override
            public void onUserListUpdate(User user, UserList userList) { }
            @Override
            public void onUserListDeletion(User user, UserList userList) { }
            @Override
            public void onUserProfileUpdate(User user) { }
            @Override
            public void onUserSuspension(long l) { }
            @Override
            public void onUserDeletion(long l) { }
            @Override
            public void onBlock(User user, User user1) { }
            @Override
            public void onUnblock(User user, User user1) { }
            @Override
            public void onRetweetedRetweet(User user, User user1, Status status) { }
            @Override
            public void onFavoritedRetweet(User user, User user1, Status status) { }
            @Override
            public void onQuotedTweet(User user, User user1, Status status) { }
            @Override
            public void onStatus(Status status) { }
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }
            @Override
            public void onTrackLimitationNotice(int i) { }
            @Override
            public void onScrubGeo(long l, long l1) { }
            @Override
            public void onStallWarning(StallWarning stallWarning) { }
        });
        socialNetworkStream.user();
        while(!killThread){try{sleep(5000);}catch(Exception ignore){}}
    }

    private void receivedMessage(DirectMessage directMessage) {
        freezeProgramIfNeeded();
        if(String.valueOf(directMessage.getSender().getId()).equals(socialNetwork.getOwnID().toString())) {
            return;
        }
        System.out.println("User: " + directMessage.getSender().getName() + " sent private message.");
        SocialNetwork.SocialNetworkPrivateMessage message = socialNetwork.mapPrivateMessage(directMessage);
        SocialNetwork.SocialNetworkUser messageFrom = socialNetwork.getUser(directMessage.getSenderId());
        socialNetwork.uploadStatus("Just got a message from: " + messageFrom.getFirstName()); // TODO DELETE THIS!
        MongoCache.getInstance().putToUsersTable(messageFrom);
        MongoCache.getInstance().addMessageToUser(message.getFromUserId(), message);
        System.out.println("Trying to send a response.");
        sendResponseMessage(messageFrom);
    }

    private void sendResponseMessage(SocialNetwork.SocialNetworkUser messageFrom) {
        freezeProgramIfNeeded();
        if(socialNetwork.canSendPrivateMessage(messageFrom)) {
            String rawMessage = Messages.generateMessage(messageFrom.getId());
            SocialNetwork.SocialNetworkPrivateMessage message = Main.generatePrivateMessageObject(messageFrom, rawMessage);
            socialNetwork.sendPrivateMessage(messageFrom.getId(), rawMessage);
            MongoCache.getInstance().addMessageToUser(messageFrom.getId(), message);
            System.out.println("successfully sent a private message.");
        } else {
            System.out.println("Could not sent a private message.");
        }
    }

    private void freezeProgramIfNeeded() {
        while(!Globals.LISTENER_SHOULD_WORK) {
            try{Thread.sleep(2000);}catch (Exception ignore){}
        }
    }

}
