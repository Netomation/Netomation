package main.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Messages;
import twitter4j.*;

public class Listener extends Thread {

    private SocialNetwork socialNetwork;

    public Listener(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void run() {
        TwitterStream socialNetworkStream = (TwitterStream)socialNetwork.getListenerStream();
        socialNetworkStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {
            public void onConnect() {
                Main.startWorker();
            }
            public void onDisconnect() {
                Main.restartListener();
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

                if(user.getId() != Long.parseLong(socialNetwork.getOwnID().toString()) && user1.getId() == Long.parseLong(socialNetwork.getOwnID().toString())) {
                    System.out.println("User: " + user.getScreenName() + " started following us.");
                    MongoCache.getInstance().userFollowUs(socialNetwork.getUser(user.getId()));
                }
            }
            @Override
            public void onUnfollow(User user, User user1) {
                if(user.getId() != Long.parseLong(socialNetwork.getOwnID().toString()) && user1.getId() == Long.parseLong(socialNetwork.getOwnID().toString())) {
                    System.out.println("User: " + user.getScreenName() + " stopped following us.");
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
    }

    private void receivedMessage(DirectMessage directMessage) {
        if(directMessage.getSender().equals(socialNetwork.getOwnID())) {
            return;
        }
        System.out.println("User: " + directMessage.getSender() + " sent private message.");
        SocialNetwork.SocialNetworkPrivateMessage message = socialNetwork.mapPrivateMessage(directMessage);
        SocialNetwork.SocialNetworkUser messageFrom = socialNetwork.getUser(directMessage.getSenderId());
        MongoCache.getInstance().putToUsersTable(messageFrom);
        MongoCache.getInstance().addMessageToUser(message.getFromUserId(), message);
        System.out.println("Trying to send a response.");
        sendResponseMessage(messageFrom);
    }

    private void sendResponseMessage(SocialNetwork.SocialNetworkUser messageFrom) {
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

}
