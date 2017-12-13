package com.netomation.data;

import java.io.File;

public abstract class Globals {
    public static final String BASE_FILES_LOCATION = "";
    public static final String PREFERENCES_FILE_NAME = "netomation_data.ini";
    public static final String CACHE_FILE_NAME = "netomation_cache.data";
    public static final String PREFERENCES_FILE_PATH = Globals.BASE_FILES_LOCATION + File.separator + Globals.PREFERENCES_FILE_NAME;
    public static final String CACHE_FILE_PATH = Globals.BASE_FILES_LOCATION + File.separator + Globals.CACHE_FILE_NAME;
    public static final String CACHE_ENTRY_CREATION_TIME_KEY = "entry_creation_time";
}
