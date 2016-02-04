package seedling.corp.recyclerviewpractice.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.fragment.SettingsFragment;
import seedling.corp.recyclerviewpractice.utils.Utils;

/**
 * Created by Ankur Nigam on 19-01-2016.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCorrectTheme();
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sett);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame_sett,
                new SettingsFragment()).commit();
    }

    @Override
    public boolean onSupportNavigateUp () {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void setCorrectTheme(){

        String currThemeSet = getSharedPreferences("theme",MODE_PRIVATE).getString("color", "default");

        if (currThemeSet.equalsIgnoreCase("red")) {
            setTheme(R.style.AppTheme_Red);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_RED));
            }
            Utils.sTheme = Utils.THEME_RED;
        }
        else if (currThemeSet.equalsIgnoreCase("green")) {
            setTheme(R.style.AppTheme_Green);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark_GREEN));
            }
            Utils.sTheme = Utils.THEME_GREEN;
        }
        else {
            setTheme(R.style.AppTheme_Green);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            }
            Utils.sTheme = Utils.THEME_DEFAULT;
        }
    }

}
