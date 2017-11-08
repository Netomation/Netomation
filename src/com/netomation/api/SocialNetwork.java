package com.netomation.api;

public interface SocialNetwork <S> {

    public void authenticate();

    public S expose();

    public void blockUser();

    public void unblockUser();

    public void sendPrivateMessage();

}