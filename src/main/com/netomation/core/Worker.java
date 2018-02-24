package main.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Messages;

import static main.com.netomation.core.Main.delay;

public class Worker extends Thread {

    private SocialNetwork socialNetwork;

    public Worker(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void run() {
        while(true) {
            System.out.println("Initial update of users list.");
            socialNetwork.updateActiveUsersList();
            for(int i = 0 ; i < socialNetwork.getActiveUsersList().size() ; i++) {
                SocialNetwork.SocialNetworkUser user = socialNetwork.getActiveUsersList().get(i);
                if(socialNetwork.shouldContactUser(user) && !userIsFromInitGroup(user) && socialNetwork.canSendPrivateMessage(user)) {
                    String message = Messages.generateMessage(user.getId());
                    SocialNetwork.SocialNetworkPrivateMessage privateMessage = Main.generatePrivateMessageObject(user, message);
                    System.out.println("Trying to send message to user: " + user.getFirstName() + " " + user.getLastName());
                    if(socialNetwork.canSendPrivateMessage(user)) {
                        socialNetwork.sendPrivateMessage(user.getId(), message);
                        System.out.println("successfully sent a private message.");
                        MongoCache.getInstance().putToUsersTable(user);
                        MongoCache.getInstance().addMessageToUser(user.getId(), privateMessage);
                        System.out.println("Trying to follow: " + user.getFirstName() + " " + user.getLastName());
                        if(socialNetwork.createFriendship(user.getId())) {
                            System.out.println("successfully followed: " + user.getFirstName() + " " + user.getLastName());
                            MongoCache.getInstance().followingUser(user);
                        } else {
                            System.out.println("Could not follow: " + user.getFirstName() + " " + user.getLastName());
                        }
                    } else {
                        System.out.println("Could not sent a private message.");
                    }
                    delay(Globals.DELAY_BEFORE_INTERACTING_WITH_NEXT_USER);
                }
                System.out.println("Updating users list.");
                socialNetwork.updateActiveUsersList();
            }
        }
    }

    private boolean userIsFromInitGroup(SocialNetwork.SocialNetworkUser user) {
        for(int i = 0 ; i < Globals.START_GROUP_IDS.length ; i++)
            if(user.getId().equals(Globals.START_GROUP_IDS[i]))
                return true;
        return false;
    }

}