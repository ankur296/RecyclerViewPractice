package seedling.corp.recyclerviewpractice.utils;

import seedling.corp.recyclerviewpractice.R;

/**
 * Created by Ankur Nigam on 24-12-2015.
 */
public final class Constants {

    public static String APP_NAME = "Shadowy";
    public static String TYPE_LOC = "Loc";
    public static String TYPE_TIME = "Time";

    public static final String REMINDER_PREF_FILE_NAME = "reminders";
    public static final String TITLE_BUNDLE_KEY = "reminder_title";
    public static final String DATE_BUNDLE_KEY = "reminder_date";
    public static final String TIME_BUNDLE_KEY = "reminder_time";
    public static final String LAT_BUNDLE_KEY = "reminder_loc_lat";
    public static final String LONG_BUNDLE_KEY = "reminder_loc_long";
    public static final String RADIUS_BUNDLE_KEY = "reminder_radius";
    public static final String REPEAT_BUNDLE_KEY = "reminder_repeat";
    public static final String PATH_BUNDLE_KEY = "reminder_path";
    public static final String ENABLED_BUNDLE_KEY = "reminder_enabled";
    public static final String SYSTEM_TIME_BUNDLE_KEY = "reminder_sys_time";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int DEFAULT_RADIUS_IN_METERS = 100;
    public static final String REPEAT_NEXT_TIME = "Next Time";
    public static final String REPEAT_EVERY_TIME = "Every Time";
    public static final String IMAGE_PATH_FIELD = "imgPath";
    public static final String ENABLED_FIELD = "enabled";
    public static final String IMAGE_PATH_FIELD_DEF_VALUE = "mipmap://" + R.mipmap.ic_launcher;

    public static final String MAIN_FRAG_TAG = "main";
    public static final String SETT_FRAG_TAG = "setting";
    public static final String TIME_FRAG_TAG = "time";
    public static final String LOC_FRAG_TAG = "loc";

    // Used to size the images in the mobile app so they can animate cleanly from list to detail
    public static final int IMAGE_ANIM_MULTIPLIER = 2;

    public static final String PACKAGE_NAME = "ankur";
    //            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String GEO_RECEIVER = PACKAGE_NAME + ".GEO_RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 24;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public static final  int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
}
