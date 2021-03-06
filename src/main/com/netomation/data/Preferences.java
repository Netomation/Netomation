package main.com.netomation.data;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Preferences {

    private static File file = new File(Globals.PREFERENCES_FILE_NAME);
    private static long fileLatestModified;
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
            createWiniAndAttachFile();
            fileLatestModified = file.lastModified();
            new FileChangeListener().start();
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

    private void createWiniAndAttachFile() {
        try {
            ini = new Wini(file);
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

    public static void initPreferences() {
        Preferences pref = Preferences.getInstance();
        pref.createWiniAndAttachFile();
        Field[] fields = Globals.class.getFields();
        boolean initFile = (file.length() == 0);
        for(Field field : fields) {
            try {
                if(!isFinal(field))
                    if(initFile) {
                        if(field.get(Globals.class) instanceof String[])
                            pref.storeArray(field.getName(), (String[])field.get(Globals.class));
                        else if(field.get(Globals.class) instanceof Object[])
                            pref.storeArray(field.getName(), (Object[])field.get(Globals.class));
                        else
                            pref.store(Globals.GLOBAL_SECTION, field.getName(), field.get(Globals.class));
                    }
                    else {
                        if (field.get(Globals.class) instanceof String[])
                            field.set(Globals.class, pref.getArray(field.getName()));
                        else if (field.get(Globals.class) instanceof Object[])
                            field.set(Globals.class, pref.getArray(field.getName()));
                        else if(field.get(Globals.class) instanceof Boolean)
                            field.set(Globals.class, Boolean.valueOf(pref.get(Globals.GLOBAL_SECTION, field.getName()).toString()));
                        else
                            field.set(Globals.class, pref.get(Globals.GLOBAL_SECTION, field.getName()));
                    }
            }
            catch(Exception exp){ exp.printStackTrace(); }
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

    public Object get(String section, String option) {
        String fetched = ini.get(section, option);
        if(isInt(fetched))
            return Integer.parseInt(fetched);
        else
            return fetched;
    }

    private boolean isInt(String test) {
        try {Integer.parseInt(test); return true;}
        catch (Exception ignore){return false;}
    }

    public String[] getArray(String section) {
        ArrayList<String> toReturn = new ArrayList<>();
        Profile.Section sectionObj = ini.get(section);
        int iter = 1;
        String temp = null;
        try{temp = sectionObj.get("" + iter);}catch (Exception ignore){}
        while(temp != null) {
            toReturn.add(temp);
            iter++;
            try{temp = sectionObj.get("" + iter);}catch (Exception ignore){}
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

    private class FileChangeListener extends Thread {
        public void run() {
            try{sleep(5000);} catch (Exception ignore) {} // letting the file enough time to write to disc
            while(true) {
                try{sleep(Globals.PREF_FILE_CHANGE_POOLING_TIMEOUT);} catch (Exception ignore) {}
                if(fileLatestModified != file.lastModified()) {
                    fileLatestModified = file.lastModified();
                    System.out.println("Modification found in preferences file, updating...");
                    initPreferences();
                }
            }
        }
    }


}
