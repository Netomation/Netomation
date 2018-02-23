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

    // MongoDB Related
    public static String MONGO_DB_ENTRY_CREATION_TIME_KEY = "entry_creation_time";
    public static String MONGO_DB_USER_ID_KEY = "user_id";
    public static String MONGO_DB_FIRST_NAME_KEY = "first_name";
    public static String MONGO_DB_LAST_NAME_KEY = "last_name";
    public static String MONGO_DB_USER_PARENT_ID_KEY = "parent_id";
    public static String MONGO_DB_USER_CONNECTION_TYPE_KEY = "connection_type_to_user";
    public static String MONGO_DB_USER_DESCRIPTION_KEY = "user_description";
    public static String MONGO_DB_USER_GEO_LOCATION_KEY = "user_geographical_location";
    public static String MONGO_DB_USER_LANGUAGE_KEY = "user_language";
    public static String MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY = "first_meet_timestamp";
    public static String MONGO_DB_DATABASE_NAME = "netomation";
    public static String MONGO_DB_USERS_COLLECTION_NAME = "users";
    public static String MONGO_DB_MESSAGES_COLLECTION_NAME = "messages";
    public static String MONGO_DB_ADDRESS = "localhost";
    public static int MONGO_DB_PORT = 27017;

    // Filter Related
    public static int MIN_AGE_FILTER = -1;
    public static int MAX_AGE_FILTER = -1;
    public static String[] FILTER_KEYWORDS = {};
    public static String[] FILTER_COUNTRIES = {};
    public static String[] FILTER_LANGUAGES = {
            "Egyptian", "egy",
            "Judeo-Arabic", "jrb",
            "Judeo-Persian", "jpr",
            "Persian", "fa", "per", "fas",
            "Arabic",
            "ar", "ara", "arq", "aao",
            "bbz", "abv", "shu", "acy",
            "adf", "avl", "arz", "afb",
            "ayh", "acw", "ayl", "acm",
            "ary", "ars", "apc", "ayp",
            "acx", "aec", "ayn", "ssh",
            "ajp", "arb", "apd", "pga",
            "acq", "abh", "aeb", "auz"

    };

    // Execution Related
    public static Object[] START_GROUP_IDS = {"*Enter IDs Here*"};

    // Delays
    public static int DELAY_BEFORE_INTERACTING_WITH_NEXT_USER = 60 * 1000;
    public static int DELAY_BEFORE_TRYING_OPERATION_AGAIN = 2 * 60 * 1000;

    // Messages
    public static String[] MESSAGES = {
            "Test_Message_1",
            "Test_Message_2",
            "Test_Message_3"
    };

    // SocialNetworkUserObject Related
    public static final String UNKNOWN_PARENT = "UNKNOWN_PARENT";
    public enum ConnectionType {NONE, FROM_ME, FROM_HIM, BOTH}
    public enum WaitingTypes {
        DONT_WAIT_JUST_TRY_AGAIN,
        WAIT_FIXED_TIME_AND_TRY_AGAIN ,
        WAIT_SPECIFIC_TIME_AND_TRY_AGAIN,
        WAIT_UNTIL_ORDERD_TO_TRY_AGAIN,
        DONT_TRY_AGAIN
    }
}
