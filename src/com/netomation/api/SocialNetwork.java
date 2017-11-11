package com.netomation.api;

import com.netomation.data.Preferences;

public abstract class SocialNetwork<S> {

    protected S socialNetwork;

    private Preferences preferences;

    public abstract S expose();

    public abstract void authenticate();

    public abstract void blockUser();

    public abstract void unblockUser();

    public abstract void sendPrivateMessage();

}