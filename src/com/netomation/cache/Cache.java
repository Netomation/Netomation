package com.netomation.cache;

import com.netomation.data.Globals;
import org.ini4j.Wini;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public abstract class Cache {

    private static File file = new File(Globals.CACHE_FILE_PATH);

    private static void commitToFile(CacheRow row) {
        try {
            if(!file.exists() && !file.createNewFile()) {
                throw new Exception("The file: " + file.getAbsolutePath() + " could not be found nor created.");
            }
            // TODO append row to cache file
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

    public static CacheRow createRow() {
        return new CacheRow();
    }


    public static class CacheRow {
        private HashMap<String, Object> data = new HashMap<>();

        public CacheRow addData(String key, Object val) {
            data.put(key, val);
            return this;
        }
        public HashMap commit() {
            data.put(Globals.CACHE_ENTRY_CREATION_TIME_KEY, Calendar.getInstance().getTimeInMillis());
            Cache.commitToFile(this);
            return data;
        }
    }

}