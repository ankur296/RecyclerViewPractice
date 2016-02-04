package seedling.corp.recyclerviewpractice.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.activity.MainActivity;
import seedling.corp.recyclerviewpractice.adapter.MyAdapter;
import seedling.corp.recyclerviewpractice.model.LocReminder;
import seedling.corp.recyclerviewpractice.model.TimeReminder;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.Logger;
import seedling.corp.recyclerviewpractice.utils.OffsetDecoration;

public class MainFragment extends Fragment {

    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    FloatingActionButton fabLoc, fabTime;
    FloatingActionsMenu menuMultipleActions;
    MainFragInterface mCallback;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        findViewsByIds(view);

        mRecyclerView.setHasFixedSize(true);
        final int spacing = getActivity().getResources()
                .getDimensionPixelSize(R.dimen.spacing_nano);
        mRecyclerView.addItemDecoration(new OffsetDecoration(spacing));

        mAdapter = new MyAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        handleGridClicks();
        handleFabClicks();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementReturnTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(R.transition.shared_move));
            setExitTransition(TransitionInflater.from(
                    getActivity()).inflateTransition(android.R.transition.fade));
        }

        setCorrectTheme();
        return view;
    }

    private void handleGridClicks() {
        mAdapter.setOnItemClickListener(

                new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {
                        ImageView heroView;
                        heroView = (ImageView) v.findViewById(R.id.category_icon);
                        Gson gson = new Gson();
                        Bundle bundle = new Bundle();
                        String json = getActivity()
                                .getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, getActivity().MODE_PRIVATE)
                                .getString(mAdapter.getItem(position), null); //TODO: check null

                        Logger.e("Clicked reminder json = " + json);

                        if (json.contains("\"type\":\"Time\"")) {
                            Logger.e("time rem clicked");
                            TimeReminder timeReminder = gson.fromJson(json, TimeReminder.class);
                            bundle.putString(Constants.TITLE_BUNDLE_KEY, timeReminder.getTitle());
                            bundle.putString(Constants.DATE_BUNDLE_KEY, timeReminder.getDate());
                            bundle.putString(Constants.TIME_BUNDLE_KEY, timeReminder.getTime());
                            bundle.putString(Constants.REPEAT_BUNDLE_KEY, timeReminder.getRepeat());
                            bundle.putString(Constants.PATH_BUNDLE_KEY, timeReminder.getImgPath());
                            bundle.putBoolean(Constants.ENABLED_BUNDLE_KEY, timeReminder.isEnabled());
                            bundle.putInt(Constants.SYSTEM_TIME_BUNDLE_KEY, timeReminder.getSystemTime());
                            mCallback.onCreatedTimeRemClicked(bundle, heroView);
                        } else {
                            Logger.e("loc rem clicked");
                            LocReminder locReminder = gson.fromJson(json, LocReminder.class);
                            bundle.putString(Constants.TITLE_BUNDLE_KEY, locReminder.getTitle());
                            bundle.putDouble(Constants.LAT_BUNDLE_KEY, locReminder.getLatitude());
                            bundle.putDouble(Constants.LONG_BUNDLE_KEY, locReminder.getLongitude());
                            bundle.putInt(Constants.RADIUS_BUNDLE_KEY, locReminder.getRadius());
                            bundle.putString(Constants.REPEAT_BUNDLE_KEY, locReminder.getRepeat());
                            bundle.putString(Constants.PATH_BUNDLE_KEY, locReminder.getImgPath());
                            bundle.putBoolean(Constants.ENABLED_BUNDLE_KEY, locReminder.isEnabled());
                            mCallback.onCreatedLocRemClicked(bundle, heroView);
                        }
                    }
                }
        );
    }

    private void findViewsByIds(View view) {
        menuMultipleActions = (FloatingActionsMenu)view.findViewById(R.id.fab_menu);
        fabTime = (FloatingActionButton)view.findViewById(R.id.fab_time);
        fabLoc = (FloatingActionButton)view.findViewById(R.id.fab_loc);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
    }

    private void handleFabClicks() {
        fabTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onNewTimeRemFabClicked(fabTime);
            }
        });

        fabLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onNewLocRemFabClicked(fabLoc);
            }
        });
    }

    @Override
    public void onAttach(Context activity) {
        Logger.e("Main frag ATTACHED !!");
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (MainFragInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    // Container Activity must implement this interface
    public interface MainFragInterface {
        void onNewTimeRemFabClicked(FloatingActionButton fab);
        void onNewLocRemFabClicked(FloatingActionButton fab);
        void onCreatedTimeRemClicked(Bundle bundle, ImageView imageView);
        void onCreatedLocRemClicked(Bundle bundle, ImageView imageView);
    }

    private void setCorrectTheme(){
        String currThemeSet = getActivity()
                .getSharedPreferences("theme", getActivity().MODE_PRIVATE)
                .getString("color", "default");

        if(currThemeSet.equalsIgnoreCase("red")){
            fabLoc.setColorNormalResId(R.color.colorPrimaryDark_RED);
            fabLoc.setColorPressedResId(R.color.colorPrimary_RED);
            fabTime.setColorNormalResId(R.color.colorPrimaryDark_RED);
            fabTime.setColorPressedResId(R.color.colorPrimary_RED);
            menuMultipleActions.setColorNormalResId(R.color.colorPrimaryDark_RED);
            menuMultipleActions.setColorPressedResId(R.color.colorPrimaryDark_RED);
//                mDrawerList.setBackgroundResource(R.color.colorPrimaryDark_RED);
        }
        else if (currThemeSet.equalsIgnoreCase("green")) {
            fabLoc.setColorNormalResId(R.color.colorPrimaryDark_GREEN);
            fabLoc.setColorPressedResId(R.color.colorPrimary_GREEN);
            fabTime.setColorNormalResId(R.color.colorPrimaryDark_GREEN);
            fabTime.setColorPressedResId(R.color.colorPrimary_GREEN);
            menuMultipleActions.setColorNormalResId(R.color.colorPrimaryDark_GREEN);
            menuMultipleActions.setColorPressedResId(R.color.colorPrimaryDark_GREEN);
//                mDrawerList.setBackgroundResource(R.color.colorPrimaryDark_GREEN);
        }
        else {
            fabLoc.setColorNormalResId(R.color.colorPrimaryDark_GREEN);
            fabLoc.setColorPressedResId(R.color.colorPrimary_GREEN);
            fabTime.setColorNormalResId(R.color.colorPrimaryDark_GREEN);
            fabTime.setColorPressedResId(R.color.colorPrimary_GREEN);
            menuMultipleActions.setColorNormalResId(R.color.colorPrimaryDark_GREEN);
            menuMultipleActions.setColorPressedResId(R.color.colorPrimaryDark_GREEN);
//                mDrawerList.setBackgroundResource(R.color.colorPrimaryDark_GREEN);
        }
    }
}
