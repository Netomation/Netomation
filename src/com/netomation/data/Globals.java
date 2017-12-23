package com.netomation.data;

import javax.swing.*;
import java.io.File;

public abstract class Globals {
    // File names
    public static final String PREFERENCES_FILE_NAME = "netomation_data.ini";
    public static final String CACHE_FILE_NAME = "netomation_cache.data";
    public static final String JSON_FILE_NAME = "netomation_cache.json";

    // File paths
    public static final String BASE_FILES_LOCATION = (new JFileChooser()).getFileSystemView().getDefaultDirectory().toString();
    public static final String PREFERENCES_FILE_PATH = BASE_FILES_LOCATION + File.separator + PREFERENCES_FILE_NAME;
    public static final String CACHE_FILE_PATH = BASE_FILES_LOCATION + File.separator + CACHE_FILE_NAME;
    public static final String JSON_FILE_PATH = BASE_FILES_LOCATION + File.separator + JSON_FILE_NAME;

    // Misc
    public static final String CACHE_ENTRY_CREATION_TIME_KEY = "entry_creation_time";
}
