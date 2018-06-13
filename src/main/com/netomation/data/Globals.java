package main.com.netomation.data;

import javax.swing.*;

/*
 * Final fields will not show up in the ini file
 * */

public abstract class Globals {

    // File names
    public static final String PREFERENCES_FILE_NAME = "netomation_data.ini";

    // File paths
    public static final String BASE_FILES_LOCATION = (new JFileChooser()).getFileSystemView().getDefaultDirectory().toString();

    // Preferences Related
    public static final String GLOBAL_SECTION = "PREFERENCES";

    // MongoDB Related
    public static String MONGO_DB_ENTRY_CREATION_TIME_KEY = "entry_creation_time";
    public static String MONGO_DB_USER_ID_KEY = "user_id";
    public static String MONGO_DB_FIRST_NAME_KEY = "first_name";
    public static String MONGO_DB_LAST_NAME_KEY = "last_name";
    public static String MONGO_DB_USER_PARENT_ID_KEY = "parent_id";
    public static String MONGO_DB_USER_CLICKED_KEY = "clicked";
    public static String MONGO_DB_USER_CONNECTION_TYPE_KEY = "connection_type_to_user";
    public static String MONGO_DB_USER_DESCRIPTION_KEY = "user_description";
    public static String MONGO_DB_USER_GEO_LOCATION_KEY = "user_geographical_location";
    public static String MONGO_DB_USER_LANGUAGE_KEY = "user_language";
    public static String MONGO_DB_USER_FIRST_MEET_TIMESTAMP_KEY = "first_meet_timestamp";
    public static String MONGO_DB_USER_PRIVATE_MESSAGES_KEY = "private_messages";
    public static String MONGO_DB_MESSAGE_TIMESTAMP_KEY = "timestamp";
    public static String MONGO_DB_MESSAGE_ID_KEY = "message_id";
    public static String MONGO_DB_FROM_USER_ID_KEY = "from_user";
    public static String MONGO_DB_TO_USER_ID_KEY = "to_user";
    public static String MONGO_DB_MESSAGE_CONTENT_KEY = "content";
    public static String MONGO_DB_DATABASE_NAME = "netomation";
    public static String MONGO_DB_USERS_COLLECTION_NAME = "users";
    public static String MONGO_DB_ACTIVE_USERS_COLLECTION_NAME = "active_path";
    public static String MONGO_DB_ACTIVE_USERS_QUEUE = "queue";
    public static String MONGO_DB_ACTIVE_USERS_ARRAY_INDEX = "array_index";
    public static String MONGO_DB_ACTIVE_USERS_UPDATE_FROM_INDEX = "update_from_index";
    public static String MONGO_DB_ADDRESS = "localhost";
    public static String MONGO_DB_CONNECTION_USERNAME = "netomation";
    public static String MONGO_DB_CONNECTION_PASSWORD = "netomation";
    public static int MONGO_DB_PORT = 27019;
    public static final int MONGO_DB_DEFAULT_PORT = 27017;

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

    public static String[] CREDENTIALS = {
            "9xjQZs17bpUCHvCG9pokGdJzi",
            "N7VKHt22ItDJhXqW4sjuS2Ea7CFdOc1Oa8Bu2NOTGIVHpzs7XW",
            "892388780753211392-JfFbaWlpfQb03rxQV6hZEBu12SDr3Sl",
            "SosL4XiNEBAtVGX7J7ElU9ptgqNWS0leK2vbO8nWN28dv"
    };

    // Execution Related
    public static Object[] START_GROUP_IDS = {1325385696};

    // Delays
    public static int DELAY_BEFORE_INTERACTING_WITH_NEXT_USER = 60 * 1000;
    public static int DELAY_BEFORE_INTERACTING_WITH_NEXT_USER_RANDOM_OFFSET = 2 * 60 * 1000;
    public static int DELAY_BEFORE_TRYING_OPERATION_AGAIN = 3 * 60 * 1000;
    public static int DELAY_BEFORE_TRYING_OPERATION_AGAIN_RANDOM_OFFSET = 2 * 60 * 1000;
    public static int DELAY_BEFORE_TRYING_OPERATION_AGAIN_FOR_AUTOMATION_ERROR = 25 * 60 * 1000;
    public static int DELAY_BEFORE_TRYING_OPERATION_AGAIN_FOR_AUTOMATION_ERROR_RANDOM_OFFSET = 15 * 60 * 1000;

    // Messages
    public static String CALL_FOR_PAPERS_URL = "http://ksiresearchorg.ipage.com/seke/seke18.html";
    public static String DDNS = "stud-twitter.jce.ac.il";
    public static int DDNS_PORT = 14534;
    public static String[] MESSAGES = {
            "SEKE'2018 = a surprising balance between Software and Knowledge. Call for papers: ",
            "LATTICE = non-obvious topic for a special session: Conceptual Lattices. Call for papers: ",
            "SEKE'2018 conference = medium size , high-quality, therefore fosters originality. Call for papers: ",
            "LATTICE special session = rigorous Software Theory for use in practice. Call for papers: "
    };

    // SocialNetworkUserObject Related
    public static final String UNKNOWN_PARENT = "UNKNOWN_PARENT";
    public final static class ConnectionType {
        final public static String NONE = "NONE";
        final public static String FROM_ME = "FROM_ME";
        final public static String FROM_HIM = "FROM_HIM";
        final public static String BOTH = "BOTH";
    }

    // Exception Handling Waiting Types
    public enum WaitingTypes {
        DONT_WAIT_JUST_TRY_AGAIN,
        WAIT_FIXED_TIME_AND_TRY_AGAIN ,
        WAIT_SPECIFIC_TIME_AND_TRY_AGAIN,
        WAIT_UNTIL_ORDERD_TO_TRY_AGAIN,
        DONT_TRY_AGAIN,
        BOT_DETECTED
    }

    // TCP Server
    public static int TCP_SERVER_PORT = 14535;
    public static String PAUSE_PROGRAM_COMMAND = "PAUSE_ALL";
    public static String PAUSE_LISTENER_COMMAND = "PAUSE_LISTENER";
    public static String PAUSE_WORKER_COMMAND = "PAUSE_WORKER";
    public static String CONTINUE_PROGRAM_COMMAND = "CONTINUE_ALL";
    public static String CONTINUE_LISTENER_COMMAND = "CONTINUE_LISTENER";
    public static String CONTINUE_WORKER_COMMAND = "CONTINUE_WORKER";
    public static boolean WORKER_SHOULD_WORK = true;
    public static boolean LISTENER_SHOULD_WORK = true;

}
