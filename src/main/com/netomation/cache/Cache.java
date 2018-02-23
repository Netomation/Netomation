package main.com.netomation.cache;

import main.com.netomation.data.Globals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* replaced with MongoDB
* */

@Deprecated
public abstract class Cache {

    private static File file = new File(Globals.CACHE_FILE_PATH);
    private static File jsonFile = new File(Globals.JSON_FILE_PATH);
    private static int cacheSize = -1;

    private static void commitToFile(CacheRow row) {
        try {
            if(!file.exists() && !file.createNewFile()) {
                throw new IOException("The file: " + file.getAbsolutePath() + " could not be found nor created.");
            }
            updateCacheSizeVariable();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            String[] keys = row.data.keySet().toArray(new String[row.data.keySet().size()]);
            bw.append('"').append(String.valueOf(cacheSize++)).append('"').append(':').append('{');
            for(int i = 0 ; i < keys.length ; i++) {
                bw
                        .append('"').append(keys[i]).append('"')
                        .append(':')
                        .append('"').append(String.valueOf(row.data.get(keys[i]))).append('"');
                if(i != keys.length - 1) {
                    bw.append(',');
                }
            }
            bw.append("},");
            bw.newLine();
            bw.close();
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

    private static void updateCacheSizeVariable() {
        if(cacheSize > -1) {
            return;
        }
        try {
            cacheSize = 1;
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher;
            Scanner scanner = new Scanner(file);
            String line;
            while(scanner.hasNextLine()) {
                line = scanner.nextLine();
                matcher = pattern.matcher(line);
                if(matcher.find())
                    cacheSize = Integer.parseInt(matcher.group());
            }
        }
        catch (Exception exp) { exp.printStackTrace(); }
        if(cacheSize > 1)
            cacheSize++;
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
            data.put(Globals.MONGO_DB_ENTRY_CREATION_TIME_KEY, Calendar.getInstance().getTimeInMillis());
            Cache.commitToFile(this);
            return data;
        }
    }

    public static void cacheFileToJSON() {
        try {
            if((jsonFile.exists() && !jsonFile.delete()) || !jsonFile.createNewFile()) {
                throw new IOException("The file: " + jsonFile.getAbsolutePath() + " could not be created (missing permissions?)");
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
            Scanner scanner = new Scanner(file);
            String line;
            bw.append('{');
            if(scanner.hasNextLine()) {
                line = scanner.nextLine();
                bw.append(line.substring(0, line.length() - 1));
            }
            while(scanner.hasNextLine()) {
                line = scanner.nextLine();
                bw.append(',');
                bw.append(line.substring(0, line.length() - 1));
            }
            bw.append('}');
            bw.close();
            scanner.close();
        }
        catch (Exception exp) { exp.printStackTrace(); }
    }

}