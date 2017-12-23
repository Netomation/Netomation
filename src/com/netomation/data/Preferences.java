package com.netomation.data;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Preferences {

    private static File file = new File(Globals.PREFERENCES_FILE_NAME);
    private static Preferences instance;
    private static Ini ini;


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

    public static void initPreferences() {
        Preferences pref = Preferences.getInstance();
        Field[] fields = Globals.class.getFields();
        boolean initFile = (file.length() == 0);
        for(Field field : fields) {
            try {
                if(!isFinal(field))
                    if(initFile) {
                        if(field.get(Globals.class) instanceof String[])
                            pref.storeArray(field.getName(), (String[])field.get(Globals.class));
                        else
                            pref.store(Globals.GLOBAL_SECTION, field.getName(), field.get(Globals.class));
                    }
                    else {
                        if (field.get(Globals.class) instanceof String[])
                            field.set(Globals.class, pref.getArray(field.getName()));
                        else
                            field.set(Globals.class, pref.get(Globals.GLOBAL_SECTION, field.getName()));
                    }
            }
            catch(Exception ignore){}
        }
    }

    public void storeArray(String section, Object[] array) {
        Profile.Section sectionObj = ini.add(section);
        for(int i = 0 ; i < array.length ; i++)
            sectionObj.add("" + (i + 1), array[i]);
        commit();
    }

    public void store(String section, String option, Object value) {
        ini.put(section,option,value);
        commit();
    }

    public String get(String section, String option) {
        return ini.get(section, option);
    }

    public String[] getArray(String section) {
        ArrayList<String> toReturn = new ArrayList<>();
        Profile.Section sectionObj = ini.get(section);
        int iter = 1;
        String temp = sectionObj.get("" + iter);
        while(temp != null) {
            toReturn.add(temp);
            iter++;
            temp = sectionObj.get("" + iter);
        }
        return toReturn.toArray(new String[toReturn.size()]);
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
