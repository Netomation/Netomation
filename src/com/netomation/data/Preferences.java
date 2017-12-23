package com.netomation.data;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class Preferences {

    private static File file = new File(Globals.PREFERENCES_FILE_NAME);
    private static String GLOBAL_SECTION = "globals";
    private static Preferences instance;
    private static Wini ini;


    public static Preferences getInstance() {
        if(instance == null)
            instance = new Preferences();
        return instance;
    }

    private Preferences() {
        try {
            fileExistAndReady();
            ini = new Wini(file);
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

    public static void initProgramPreferences() {
        Preferences pref = Preferences.getInstance();
        Field[] fields = Globals.class.getFields();
        boolean initFile = (file.length() == 0);
        for(Field field : fields) {
            try {
                if(!isFinal(field))
                    if(initFile)
                        pref.store(GLOBAL_SECTION, field.getName(), field.get(Globals.class));
                    else
                        field.set(Globals.class, pref.get(GLOBAL_SECTION, field.getName()));
            }
            catch(Exception ignore){}
        }
    }

    public void store(String section, String option, Object value) {
        ini.put(section,option,value);
        commit();
    }

    public String get(String section, String option) {
        return ini.get(section, option);
    }



    private void commit() {
        try {
            ini.store();
        } catch (Exception exp) {exp.printStackTrace();}
    }

    private void fileExistAndReady() throws IOException {
        if(!file.exists() && !file.createNewFile()) {
            throw new IOException("The file: " + file.getAbsolutePath() + " could not be found nor created.");
        }
    }

    public static boolean isFinal(Field field) {
        return isFinal(field.getModifiers());
    }

    public static boolean isFinal(int modifiers) {
        return (modifiers & java.lang.reflect.Modifier.FINAL) == java.lang.reflect.Modifier.FINAL;
    }




}
