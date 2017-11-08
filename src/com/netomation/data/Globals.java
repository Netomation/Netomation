package com.netomation.data;

import java.io.File;

public abstract class Globals {
    public static String BASE_FILES_LOCATION = "";
    public static String PREFERENCES_FILE_NAME = "netomation_data.ini";
    public static String PREFERENCES_FILE_PATH = Globals.BASE_FILES_LOCATION + File.separator + Globals.PREFERENCES_FILE_NAME;

}
