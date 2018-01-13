package main.com.netomation.core;

import main.com.netomation.api.SocialNetwork;
import main.com.netomation.data.Globals;
import main.com.netomation.data.Messages;

public class Worker extends Thread {

    private SocialNetwork socialNetwork;

    public Worker(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void run() {
        while(true) {
            socialNetwork.updateActiveUsersList();
            for(SocialNetwork.SocialNetworkUser user : socialNetwork.activeUsersList) {
                socialNetwork.sendPrivateMessage(user.id, Messages.generateMessage());
                delay(Globals.DELAY_BEFORE_INTERACTING_WITH_NEXT_USER);
            }
        }
    }

    public void delay(int seconds) {
        try{Thread.sleep(1000 * seconds);} catch (Exception ignore){}
    }
}