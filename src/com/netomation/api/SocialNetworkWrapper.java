package com.netomation.api;

public class SocialNetworkWrapper extends SocialNetwork {

    private SocialNetwork concrete;
    private static SocialNetworkWrapper instance;

    public static<T extends SocialNetwork> SocialNetworkWrapper getInstance(Class<T> target) {
        if(instance == null)
            instance = new SocialNetworkWrapper(target);
        return instance;
    }

    public static SocialNetworkWrapper getInstance() {
        if(instance == null)
            throw new RuntimeException("SocialNetworkWrapper was not initialized using: 'SocialNetworkWrapper.getInstance(Class<T> target)'");
        return instance;
    }

    private<T extends SocialNetwork> SocialNetworkWrapper(Class<T> target) {
        try {
            concrete = target.newInstance();
        } catch (Exception exp) {exp.printStackTrace();}
    }

    @Override
    public Object expose() {
        return concrete.expose();
    }

    @Override
    public void authenticate() {
        concrete.authenticate();
    }

    @Override
    public void blockUser() {
        concrete.blockUser();
    }

    @Override
    public void unblockUser() {
        concrete.unblockUser();
    }

    @Override
    public void sendPrivateMessage() {
        concrete.sendPrivateMessage();
    }
}
