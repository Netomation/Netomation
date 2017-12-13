package com.netomation.api;

import java.lang.reflect.Method;

public class SocialNetworkFactory {

    private static SocialNetwork latestSocialNetwork;

    public static<S extends SocialNetwork> SocialNetwork getSocialNetwork(Class<S> socialNetwork) {
        try {
            latestSocialNetwork = socialNetwork.newInstance();
            return latestSocialNetwork;
        } catch (Exception failed1) {
            System.err.println(failed1.getMessage() + ".\nTrying to call: " + socialNetwork.getName() + ".getInstance()");
            try {
                Method toInvoke = socialNetwork.getMethod("getInstance");
                latestSocialNetwork = (SocialNetwork) toInvoke.invoke(null);
                return latestSocialNetwork;
            } catch (Exception failed2) {
                System.err.println(failed2.getMessage() + " Could not be found.");
            }
        }
        return null;
    }

    public static<S extends SocialNetwork> SocialNetwork getSocialNetwork(String name) {
        switch(name.trim().toLowerCase()) {
            case "twitter": latestSocialNetwork = TwitterWrapper.getInstance();
            break;
            default: return null;
        }
        return latestSocialNetwork;
    }

    public static SocialNetwork getLatest() {
        return latestSocialNetwork;
    }

}