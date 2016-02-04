package seedling.corp.recyclerviewpractice.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.ads.AdView;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.adapter.NavAdapter;
import seedling.corp.recyclerviewpractice.fragment.CreateLocReminderFrag;
import seedling.corp.recyclerviewpractice.fragment.CreateTimeReminderFrag;
import seedling.corp.recyclerviewpractice.fragment.MainFragment;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.Logger;
import seedling.corp.recyclerviewpractice.utils.Utils;

public class MainActivity extends AppCompatActivity implements
        NavAdapter.OnItemClickListener,
        MainFragment.MainFragInterface,
        CreateLocReminderFrag.LocFragInterface,
        CreateTimeReminderFrag.TimeFragInterface{

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mNavOptions;
    private int[] navMenuIcons;
    Context mContext;
    Toolbar mToolbar;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.e("oncreate enter");
        super.onCreate(savedInstanceState);
        mContext = this;
        setCorrectTheme("layout");
        setContentView(R.layout.activity_main);

        findViewsByIds();
        handleActionBar();
        handleNavDrawer();

        //Add main frag dynamically
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.container, new MainFragment(), Constants.MAIN_FRAG_TAG)
                    .commit();
        }

        setCorrectTheme("nav");
    }

    private void handleActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void handleNavDrawer() {
        navMenuIcons = new int[]{
                R.mipmap.ic_action_settings,
                R.mipmap.ic_action_emo_cool,
                R.mipmap.ic_action_like,
                R.mipmap.ic_action_star_10,
                R.mipmap.ic_action_share,
                R.mipmap.ic_action_gmail
        };
        LinearLayoutManager mLinearLayoutmanager = new LinearLayoutManager(this);
        mDrawerList.setLayoutManager(mLinearLayoutmanager);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new NavAdapter(mNavOptions, navMenuIcons, this));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Navigation back icon listener
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void findViewsByIds() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavOptions = getResources().getStringArray(R.array.nav_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            default:
        }
    }

    @Override
    public void onNewTimeRemFabClicked(FloatingActionButton heroView) {

        CreateTimeReminderFrag timeReminderFrag = new CreateTimeReminderFrag();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            timeReminderFrag.setSharedElementEnterTransition(TransitionInflater.from(
                    this).inflateTransition(R.transition.shared_move));
            timeReminderFrag.setEnterTransition(TransitionInflater.from(
                    this).inflateTransition(android.R.transition.fade));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, timeReminderFrag, Constants.TIME_FRAG_TAG)
                .addToBackStack("null")
                .addSharedElement(heroView, "fab")
                .commit();

        //disable the toggle menu and show up carat
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onNewLocRemFabClicked(FloatingActionButton heroView) {
        CreateLocReminderFrag locReminderFrag = new CreateLocReminderFrag();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            locReminderFrag.setSharedElementEnterTransition(TransitionInflater.from(
                    this).inflateTransition(R.transition.shared_move));
            locReminderFrag.setEnterTransition(TransitionInflater.from(
                    this).inflateTransition(android.R.transition.fade));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, locReminderFrag, Constants.LOC_FRAG_TAG)
                .addToBackStack("null")
                .addSharedElement(heroView, "fab")
                .commit();

        //disable the toggle menu and show up carat
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onBackPressed() {
        Logger.e("onbackpressed enter");
        super.onBackPressed();
        // turn on the Navigation Drawer image;
        // this is called in the LowerLevelFragments
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }



    @Override
    public void onCreatedTimeRemClicked(Bundle bundle, ImageView heroView) {
        CreateTimeReminderFrag timeReminderFrag = new CreateTimeReminderFrag();
        timeReminderFrag.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            timeReminderFrag.setSharedElementEnterTransition(TransitionInflater.from(
                    this).inflateTransition(R.transition.shared_move));
            timeReminderFrag.setEnterTransition(TransitionInflater.from(
                    this).inflateTransition(android.R.transition.fade));
            Logger.e("heroView.getTransitionName() = " + heroView.getTransitionName());
            timeReminderFrag.setTransitionName(heroView.getTransitionName());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ft.replace(R.id.container, timeReminderFrag, Constants.TIME_FRAG_TAG)
                    .addSharedElement(heroView, heroView.getTransitionName())
                    .addToBackStack("null")
                    .commit();
        }else{
            ft.replace(R.id.container, timeReminderFrag, Constants.TIME_FRAG_TAG)
                    .addToBackStack("null")
                    .commit();
        }

        //disable the toggle menu and show up carat
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onCreatedLocRemClicked(Bundle bundle, ImageView heroView) {
        CreateLocReminderFrag locReminderFrag = new CreateLocReminderFrag();
        locReminderFrag.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            locReminderFrag.setSharedElementEnterTransition(TransitionInflater.from(
                    this).inflateTransition(R.transition.shared_move));
            locReminderFrag.setEnterTransition(TransitionInflater.from(
                    this).inflateTransition(android.R.transition.fade));
            Logger.e("heroView.getTransitionName() = " + heroView.getTransitionName());
            locReminderFrag.setTransitionName(heroView.getTransitionName());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ft.replace(R.id.container, locReminderFrag, Constants.LOC_FRAG_TAG)
                    .addSharedElement(heroView, heroView.getTransitionName())
                    .addToBackStack("null")
                    .commit();
        }else{
            ft.replace(R.id.container, locReminderFrag, Constants.LOC_FRAG_TAG)
                    .addToBackStack("null")
                    .commit();
        }

        //disable the toggle menu and show up carat
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void onDeleteTimeRemClicked() {
        getSupportFragmentManager().popBackStackImmediate();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onSaveTimeRemFabClicked() {
        getSupportFragmentManager().popBackStackImmediate();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onSaveLocRemFabClicked() {
        getSupportFragmentManager().popBackStackImmediate();
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void setCorrectTheme(String what){

        String currThemeSet = getSharedPreferences("theme",MODE_PRIVATE).getString("color", "default");

        if (what.equalsIgnoreCase("layout")) {

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
                    window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                }
                Utils.sTheme = Utils.THEME_DEFAULT;
            }
        }
        else {

            if(currThemeSet.equalsIgnoreCase("red")){
                mDrawerList.setBackgroundResource(R.color.colorPrimaryDark_RED);
            }
            else if (currThemeSet.equalsIgnoreCase("green")) {
                mDrawerList.setBackgroundResource(R.color.colorPrimaryDark_GREEN);
            }
            else {
                mDrawerList.setBackgroundResource(R.color.colorPrimaryDark_GREEN);
            }
        }
    }

    @Override
    protected void onPause() {
        Logger.e("onpause enter");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Logger.e("onstart enter");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Logger.e("onstop enter");
        super.onStop();
    }
}
