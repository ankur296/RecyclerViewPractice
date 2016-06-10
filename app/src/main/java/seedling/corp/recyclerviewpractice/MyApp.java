package seedling.corp.recyclerviewpractice;

import android.app.Application;
import android.content.SharedPreferences;

import seedling.corp.recyclerviewpractice.utils.Constants;

/**
 * Created by Ankur Nigam on 12-01-2016.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//chk git--ankur
        //test3
        SharedPreferences preferences = this.getSharedPreferences("misc",MODE_PRIVATE);

        boolean firstLaunch = preferences.getBoolean("first_launch", true);

        if (firstLaunch){
            preferences.edit().putBoolean("first_launch", false).apply();
            //load preset reminders
            SharedPreferences.Editor editor = this.getSharedPreferences(
                    Constants.REMINDER_PREF_FILE_NAME,MODE_PRIVATE).edit();

            editor.putString("Quit Smoking",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/smoking.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Quit Smoking\",\"type\":\"Time\",\"enabled\":\"false\"}");

            editor.putString("Meditate Daily",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/meditation.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Meditate Daily\",\"type\":\"Time\"}");

            editor.putString("Drink 8 glasses of water daily",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/drink_water.png\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Drink 8 glasses of water daily\",\"type\":\"Time\"}");

            editor.putString("Wake up early",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/early_morn.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Wake up early\",\"type\":\"Time\"}");

            editor.putString("Lose Weight",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/lose_weight.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Lose Weight\",\"type\":\"Time\"}");

            editor.putString("Read a book",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/read_book.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Read a book\",\"type\":\"Time\"}");

            editor.putString("Exercise Daily",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/exercise.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Exercise Daily\",\"type\":\"Time\"}");

            editor.putString("Sleep Early",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/sleep_early.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Sleep Early\",\"type\":\"Time\"}");

            editor.putString("Practice Yoga",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/yoga.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Practice Yoga\",\"type\":\"Time\"}");

            editor.putString("Quit Alcohol",
                    "{\"date\":\"13/1/2016\",\"imgPath\":\"file:///android_asset/img/quit_alcohol.jpg\"," +
                            "\"repeat\":\"Disabled\",\"time\":\"18:37\",\"title\":\"Quit Alcohol\",\"type\":\"Time\"}");

            editor.apply();
        }
    }
}
