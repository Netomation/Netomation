package com.netomation.data;

import org.ini4j.Wini;

import java.io.File;

public class Preferences {

    private static File file = new File(Globals.PREFERENCES_FILE_PATH);
    private static Preferences instance;
    private static Wini ini;


    public static Preferences getInstance() {
        if(instance == null)
            instance = new Preferences();
        return instance;
    }

    private Preferences() {
        try {
            if(!file.exists() && !file.createNewFile()) {
                throw new Exception("The file: " + file.getAbsolutePath() + " could not be found nor created.");
            }
            ini = new Wini(file);
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

    private void store(String section, String option, Object value) {
        ini.put(section,option,value);
        commit();
    }

    private void commit() {
        try {
            ini.store();
        } catch (Exception exp) {exp.printStackTrace();}
    }




}
