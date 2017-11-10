package com.netomation.api;

import com.netomation.data.Preferences;

public abstract class SocialNetwork<S> {

    protected S socialNetwork;
    protected static SocialNetwork instance;
    private Preferences preferences;

    public S expose() {
        return socialNetwork;
    }

    public abstract void authenticate();

    public abstract void blockUser();

    public abstract void unblockUser();

    public abstract void sendPrivateMessage();

}