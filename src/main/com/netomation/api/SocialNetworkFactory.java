package main.com.netomation.api;

import java.lang.reflect.Method;

public class SocialNetworkFactory {

    private static SocialNetwork latestSocialNetwork;

    public static<S extends SocialNetwork> SocialNetwork getSocialNetwork(Class<S> socialNetwork) {
        Method toInvoke = null;
        try {
            toInvoke = socialNetwork.getMethod("getInstance");
        } catch(NoSuchMethodException e) { }

        try {
            latestSocialNetwork = toInvoke != null ?
                    (SocialNetwork) toInvoke.invoke(null) :
                    socialNetwork.newInstance();

            return latestSocialNetwork;
        } catch (Exception failed1) {
            System.err.println(failed1.getMessage() + ".\nTrying to call: " + socialNetwork.getName() + ".getInstance()");
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