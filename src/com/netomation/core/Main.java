package com.netomation.core;

import com.netomation.api.FacebookWrapper;
import com.netomation.api.SocialNetwork;
import com.netomation.api.TwitterWrapper;
import com.netomation.data.Preferences;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import org.ini4j.Wini;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Twitter tw = TwitterFactory.getSingleton();
        Facebook fb = FacebookFactory.getSingleton();
        SocialNetwork social = TwitterWrapper.getInstance();




//        try {
//            File file = new File("D://pref.ini");
//            file.createNewFile();
//            Wini ini = new Wini(file);
//            ini.put("Main","Framerate",3);
//            ini.store();
//        } catch(Exception exp) { exp.printStackTrace(); }
    }
}
