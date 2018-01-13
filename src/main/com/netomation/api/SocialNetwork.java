package main.com.netomation.api;

import java.util.ArrayList;

public abstract class SocialNetwork {

    public ArrayList<SocialNetworkUser> activeUsersList = new ArrayList<>();

    public abstract Object expose();

    public abstract String getOwnID();

    public abstract SocialNetworkUser getUser(String id);

    public abstract void blockUser(String id);

    public abstract void unblockUser(String id);

    public abstract ArrayList<SocialNetworkUser> getExpansionGroupByUser(String id);

    public abstract void sendPrivateMessage(String id, String msg);

    public abstract void setCredentials(String... credentials);

    public boolean updateActiveUsersList() {
        boolean toReturn = false;
        ArrayList<SocialNetworkUser> toCombine = new ArrayList<>();
        ArrayList<SocialNetworkUser> temp;
        for(SocialNetworkUser user : activeUsersList) {
            temp = getExpansionGroupByUser(user.id);
            if(temp == null)
                continue;
            if(temp.size() > 0)
                toReturn = true;
            toCombine.addAll(temp);
        }
        activeUsersList.addAll(toCombine);
        return toReturn;
    }

    public abstract Object getConfiguration();

    public abstract class SocialNetworkUser {
        public String firstName = null;
        public String lastName = null;
        public String id = null;
    }

}