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
        System.out.println("Initial update of users list.");
        socialNetwork.updateActiveUsersList(-1);
        int arrayIndex = 0, updateFromIndex = 0;
        while(true) {
            for(; arrayIndex < socialNetwork.getActiveUsersList().size() ; arrayIndex++) {
                SocialNetwork.SocialNetworkUser user = socialNetwork.getActiveUsersList().get(arrayIndex);
                if(socialNetwork.shouldContactUser(user) && !userIsFromInitGroup(user) && socialNetwork.canSendPrivateMessage(user)) {
                    delay(Globals.DELAY_BEFORE_INTERACTING_WITH_NEXT_USER);
                    String message = Messages.generateMessage(user.getId());
                    SocialNetwork.SocialNetworkPrivateMessage privateMessage = Main.generatePrivateMessageObject(user, message);
                    System.out.println("Trying to send message to user: " + user.getFirstName() + " " + user.getLastName());
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
                }
            }
            System.out.println("Updating users list.");
            while(true) {
                int amount = socialNetwork.updateActiveUsersList(updateFromIndex++);
                if(amount == -1) {
                    return;
                } else if(amount > 0) {
                    break;
                }
            }
        }
    }

    private boolean userIsFromInitGroup(SocialNetwork.SocialNetworkUser user) {
        for(int i = 0 ; i < Globals.START_GROUP_IDS.length ; i++)
            if(user.getId().toString().equals(Globals.START_GROUP_IDS[i].toString()))
                return true;
        return false;
    }

}