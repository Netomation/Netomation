package main.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.cache.MongoCache;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Messages;

import java.util.Random;

import static main.com.netomation.core.Main.delay;

public class Worker extends Thread {

    private SocialNetwork socialNetwork;

    public Worker(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void run() {
        if(socialNetwork == null) return;
        int arrayIndex = MongoCache.getInstance().getArrayIndexQueueFromLastRunning();
        int updateFromIndex = MongoCache.getInstance().getUpdateFromIndexQueueFromLastRunning();
        System.out.println("Initial update of users list.");
        socialNetwork.updateActiveUsersList(-1);
        while(true) {
            for(; arrayIndex < socialNetwork.getActiveUsersList().size() ; arrayIndex++) {
                freezeProgramIfNeeded();
                SocialNetwork.SocialNetworkUser user = socialNetwork.getActiveUsersList().get(arrayIndex);
                System.out.println("Worker delaying...");
                delay(Globals.DELAY_BEFORE_INTERACTING_WITH_NEXT_USER + new Random().nextInt(Globals.DELAY_BEFORE_INTERACTING_WITH_NEXT_USER_RANDOM_OFFSET));
                System.out.println("Worker done delaying, going on.");
                freezeProgramIfNeeded();
                if(socialNetwork.shouldContactUser(user) && !userIsFromInitGroup(user) && socialNetwork.canSendPrivateMessage(user)) {
                    MongoCache.getInstance().putToUsersTable(user);
                    String message = Messages.generateMessage(user.getId());
                    SocialNetwork.SocialNetworkPrivateMessage privateMessage = Main.generatePrivateMessageObject(user, message);
                    System.out.println("Trying to send message to user: " + user.getFirstName() + " " + user.getLastName());
                    socialNetwork.sendPrivateMessage(user.getId(), message);
                    System.out.println("successfully sent a private message.");
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
            MongoCache.getInstance().updateActiveUsers(socialNetwork.getActiveUsersList(), arrayIndex, updateFromIndex);
        }
    }

    private boolean userIsFromInitGroup(SocialNetwork.SocialNetworkUser user) {
        for(int i = 0 ; i < Globals.START_GROUP_IDS.length ; i++)
            if(user.getId().toString().equals(Globals.START_GROUP_IDS[i].toString()))
                return true;
        return false;
    }

    private void freezeProgramIfNeeded() {
        while(!Globals.WORKER_SHOULD_WORK) {
            try{Thread.sleep(2000);}catch (Exception ignore){}
        }
    }

}