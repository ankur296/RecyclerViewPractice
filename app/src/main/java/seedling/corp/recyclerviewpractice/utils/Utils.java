package seedling.corp.recyclerviewpractice.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import java.util.Random;
import java.util.UUID;

import seedling.corp.recyclerviewpractice.R;

/**
 * Created by Ankur Nigam on 24-12-2015.
 */
public final class Utils {


    public static final int SDK_VERSION = Build.VERSION.SDK_INT;

    public static final String SEVEN_LETTERS_URL_HTTP = "https://play.google.com/store/apps/details?id=corp.seedling.seven.letters";
    public static final String SEVEN_LETTERS_URL = "market://details?id=corp.seedling.seven.letters";
    public static final String PUBLISHER_URL = "market://search?q=pub:Seedling Corp";

    public static final int[] COLOR_RES = {
            R.color.theme_blue_background,//10
            R.color.theme_purple_background,//0
            R.color.theme_red_background,//0
            R.color.theme_green_background//0
    };

    static int rand = -1;
    public static int getRandomColor(){

        Random r = new Random();
        int randInt = r.nextInt(4 - 0) + 0;

        while (rand == randInt){
            randInt = r.nextInt(4 - 0) + 0;
        }

        rand = randInt;
        return COLOR_RES[rand] ;
    }


    public static String getUniqueImageFilename(){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return Constants.APP_NAME + "_" + uuid + ".jpg";
    }

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px){
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }


    public static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_GREEN = 1;
    public final static int THEME_BLUE = 2;
    public final static int THEME_RED = 3;
    public final static int THEME_PINK = 4;

    /** * Set the theme of the Activity, and restart it by creating a new Activity of the same type. */
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity) {

        switch (sTheme) {

            default:

            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;

            case THEME_RED: activity.setTheme(R.style.AppTheme_Red);
                break;

            case THEME_GREEN: activity.setTheme(R.style.AppTheme_Green);
                break;
        }
    }


}
