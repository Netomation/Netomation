package main.com.netomation.data;

import javax.swing.*;
import java.io.File;

/*
 * Final fields will not show up in the ini file
 * */

public abstract class Globals {

    // File names
    public static final String PREFERENCES_FILE_NAME = "netomation_data.ini";
    public static final String CACHE_FILE_NAME = "netomation_cache.data";
    public static final String JSON_FILE_NAME = "netomation_cache.json";

    // File paths
    public static final String BASE_FILES_LOCATION = (new JFileChooser()).getFileSystemView().getDefaultDirectory().toString();
    public static String CACHE_FILE_PATH = BASE_FILES_LOCATION + File.separator + CACHE_FILE_NAME;
    public static String JSON_FILE_PATH = BASE_FILES_LOCATION + File.separator + JSON_FILE_NAME;

    // Preferences Related
    public static final String GLOBAL_SECTION = "PREFERENCES";

    // Cache Related
    public static String CACHE_ENTRY_CREATION_TIME_KEY = "entry_creation_time";

    // Filter Related
    public static int MIN_AGE_FILTER = 18;
    public static int MAX_AGE_FILTER = 80;
    public static String[] VALID_KEYWORDS = {
            "Computer",
            "Programming"
    };
    public static String[] VALID_LANGUAGES = {
            "English",
            "Hebrew"
    };
    public static String[] VALID_COUNTRIES = {
            "Israel",
            "USA"
    };

    // Execution Related
    public static String[] START_GROUP_IDS = {"*Enter IDs Here*"};
    public static String[] START_GROUP_NAMES = {"*Enter Names Here*"};

    // Delays
    public static int DELAY_BEFORE_INTERACTING_WITH_NEXT_USER = 60;

    // Messages
    public static String[] MESSAGES = {
            "Test_Message_1",
            "Test_Message_2",
            "Test_Message_3"
    };


}
