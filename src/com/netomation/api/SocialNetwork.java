package com.netomation.api;

public interface SocialNetwork {

    Object expose();

    long getOwnID();

    Object getUser(long id);

    void blockUser(long id);

    void unblockUser(long id);

    void sendPrivateMessage(long id, String msg);

}