package seedling.corp.recyclerviewpractice.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.activity.MainActivity;
import seedling.corp.recyclerviewpractice.backend.AlarmReceiver;
import seedling.corp.recyclerviewpractice.model.TimeReminder;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.FontManager;
import seedling.corp.recyclerviewpractice.utils.Logger;
import seedling.corp.recyclerviewpractice.utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateTimeReminderFrag extends Fragment {

    private static final int SELECT_PICTURE_REQUEST_CODE = 7;
    TextView mTimeTextView, mDateTextView, mRepeatTextView, mPickImageTextView;
    TextView mTitleAwesomeTextView, mDateAwesomeTextView, mReminderAwesomeTextView,mRepeatAwesomeTextView,mPicAwesomeTextView;
    EditText mTitleEditText;
    ImageView mImageView;
    private Uri mImageUri;
    private int mImageSize;
    FloatingActionButton fabSaveReminder;
    TimeFragInterface mCallback;
    String mTransitionName;
    String mOriginalTitle = null;
    int mOriginalSysTime = 0;

    public CreateTimeReminderFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.e("oncreateview of time frag");
        // Load a larger size image to make the activity transition to the detail screen smooth
        mImageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;
//        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.frag_time_reminder_create, container, false);

        findViewsByIds(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mImageView.setTransitionName(mTransitionName);
        }

        //check if coming from already created reminder or FAB
        Bundle args = getArguments();
        if(args != null) {
            mOriginalTitle = args.getString(Constants.TITLE_BUNDLE_KEY);
            mOriginalSysTime = args.getInt(Constants.SYSTEM_TIME_BUNDLE_KEY);

            loadReminder(new TimeReminder(
                    Constants.TYPE_TIME,
                    args.getString(Constants.TITLE_BUNDLE_KEY),
                    args.getString(Constants.DATE_BUNDLE_KEY),
                    args.getString(Constants.TIME_BUNDLE_KEY),
                    args.getString(Constants.REPEAT_BUNDLE_KEY),
                    args.getString(Constants.PATH_BUNDLE_KEY),
                    args.getBoolean(Constants.ENABLED_BUNDLE_KEY),
                    args.getInt(Constants.SYSTEM_TIME_BUNDLE_KEY)
            ));
        } else {
            // code to set values if arguments are not set
            final Calendar mcurrentTime = Calendar.getInstance();
            final int year_curr = mcurrentTime.get(Calendar.YEAR);
            final int month_curr = mcurrentTime.get(Calendar.MONTH);
            final int day_curr = mcurrentTime.get(Calendar.DAY_OF_MONTH);

            //set today's date by default
            mDateTextView.setText(day_curr + "/" + (month_curr + 1) + "/" + year_curr);
        }

        //Dont auto open keypad cos of edittext focus
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        handleDatePicker();
        handleTimePicker();
        handleRepeatDialog();
        handleSaveFabClicks();
        //handle pic
        mPickImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
        handleMenuAndAB();
        setAwesomeFonts();
        return view;
    }

    private void setAwesomeFonts() {
        Typeface iconFont = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        mTitleAwesomeTextView.setTypeface(iconFont);
        mDateAwesomeTextView.setTypeface(iconFont);
        mRepeatAwesomeTextView.setTypeface(iconFont);
        mReminderAwesomeTextView.setTypeface(iconFont);
        mPicAwesomeTextView.setTypeface(iconFont);
    }

    private void handleMenuAndAB() {
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.create_time_frag_title);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_delete:
                TimeReminder mReminder = getReminderData();
                if (mReminder != null) {
                    SharedPreferences.Editor editor = getActivity().
                            getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, getActivity().MODE_PRIVATE)
                            .edit();
                    editor.remove(mOriginalTitle);
                    editor.apply();
                    //also cancel the associated alarm with the removed reminder
                    cancelAlarm(mOriginalSysTime);
                    Logger.e("Reminder saved: " + mReminder.getTitle());
                }
                mCallback.onDeleteTimeRemClicked();

                return true;
        }
        return false;
    }

    private void handleSaveFabClicks() {
        fabSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeReminder mReminder = getReminderData();
                if (mReminder != null) {
                    setDailyAlarm(mReminder);
                    //save the reminder details in shared preferences
                    SharedPreferences.Editor editor = getActivity().
                            getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, getActivity().MODE_PRIVATE)
                            .edit();

                    //check if its a new reminder..else delete the earlier one and save new
                    if(mOriginalTitle != null) {
                        //case of title being modified of an existing rminder
                        //so remove the existing reminder
                        Logger.e("remove alarm with title="+mOriginalTitle +", and id="+mOriginalSysTime);
                       editor.remove(mOriginalTitle).apply();
                        //also cancel the associated alarm with the removed reminder
                        cancelAlarm(mOriginalSysTime);
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(mReminder);
                    editor.putString(mReminder.getTitle(), json);
                    editor.apply();
                    Logger.e("Reminder saved: " + mReminder.getTitle());
                    mCallback.onSaveTimeRemFabClicked();
                }
            }
        });
    }


    public void setTransitionName(String name){
        this.mTransitionName = name;
    }

    private void setDailyAlarm(TimeReminder timeReminder){
        AlarmManager alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("title", timeReminder.getTitle());
        intent.putExtra("path", timeReminder.getImgPath());
        //This ll be used to create exact pending intent to cancel alarm
        intent.putExtra("id", timeReminder.getSystemTime());

        Logger.e("Set alarm for: "+timeReminder.getTitle() + ", and id= "+timeReminder.getSystemTime());

        String[] alarmTime = timeReminder.getTime().split(":");
        int hour = Integer.parseInt(alarmTime[0]);
        int min = Integer.parseInt(alarmTime[1]);

        String[] alarmDate = timeReminder.getDate().split("/");
        int day = Integer.parseInt(alarmDate[0]);
        int month = Integer.parseInt(alarmDate[1]);
        int year = Integer.parseInt(alarmDate[2]);

        //TODO: add every 3 hrs, monthly, yearly and weekdays
        long interval = AlarmManager.INTERVAL_DAY;
        if (timeReminder.getRepeat().equalsIgnoreCase("disabled"))
            interval = 0;
        else if (timeReminder.getRepeat().equalsIgnoreCase("daily"))
            interval = AlarmManager.INTERVAL_DAY;
        else if (timeReminder.getRepeat().equalsIgnoreCase("weekly"))
            interval = 7 * AlarmManager.INTERVAL_DAY;
        else if (timeReminder.getRepeat().equalsIgnoreCase("monthly"))
            interval = AlarmManager.INTERVAL_DAY;
        else if (timeReminder.getRepeat().equalsIgnoreCase("yearly"))
            interval = AlarmManager.INTERVAL_DAY;

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                getActivity(),
                timeReminder.getSystemTime(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
Logger.e("Alarm set for:"+day+"/"+month+"/"+year+" at "+hour+":"+min);
        alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                60000,
//                interval,
                alarmIntent);
    }

    private void cancelAlarm(int id){
        Logger.e("id of the alarm being cancelled = "+id);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void handleRepeatDialog() {
        mRepeatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog levelDialog;
                final CharSequence[] items = {
                        getResources().getString(R.string.repeat_disabled),
                        getResources().getString(R.string.repeat_weekday),
                        getResources().getString(R.string.repeat_daily),
                        getResources().getString(R.string.repeat_weekly),
                        getResources().getString(R.string.repeat_monthly),
                        getResources().getString(R.string.repeat_yearly)
                };
                // Creating and Building the Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.repeat_heading));
                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                mRepeatTextView.setText(getResources().getString(R.string.repeat_disabled));
                                break;
                            case 1:
                                mRepeatTextView.setText(getResources().getString(R.string.repeat_weekday));
                                break;
                            case 2:
                                mRepeatTextView.setText(getResources().getString(R.string.repeat_daily));
                                break;
                            case 3:
                                mRepeatTextView.setText(getResources().getString(R.string.repeat_weekly));
                                break;
                            case 4:
                                mRepeatTextView.setText(getResources().getString(R.string.repeat_monthly));
                                break;
                            case 5:
                                mRepeatTextView.setText(getResources().getString(R.string.repeat_yearly));
                                break;
                        }
                    }
                });
                levelDialog = builder.create();
                levelDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                levelDialog.show();
            }
        });
    }

    private void handleTimePicker() {
        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                mTimeTextView.setText(
                                        selectedMinute > 9 ? selectedHour + ":" + selectedMinute
                                                : selectedHour + ":0" + selectedMinute
                                );
                            }
                        }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

    }

    private void handleDatePicker() {
        final Calendar mcurrentTime = Calendar.getInstance();
        final int year_curr = mcurrentTime.get(Calendar.YEAR);
        final int month_curr = mcurrentTime.get(Calendar.MONTH);
        final int day_curr = mcurrentTime.get(Calendar.DAY_OF_MONTH);

        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(getActivity(),
                        //date set listener
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                if ((year > year_curr) ||
                                        (monthOfYear > month_curr && year == year_curr) ||
                                        (
                                                dayOfMonth >= day_curr
                                                        && year == year_curr
                                                        && monthOfYear == month_curr
                                        )
                                        ) {
                                    mDateTextView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                } else{
                                    Toast.makeText(getActivity(),
                                            getResources().getString(R.string.wrong_date),Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        }, year_curr, month_curr, day_curr) {
                };
                datePickerDialog.show();
            }
        });
    }

    public void loadReminder(TimeReminder timeReminder){
        Logger.e("loadReminder enter");

        String path = timeReminder.getImgPath();
        if (path != null) {
            mImageUri = Uri.parse(path);
            Glide.with(getActivity())
                    .load(mImageUri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .override(mImageSize, mImageSize)
                    .error(R.drawable.notavailable)
                    .into(mImageView);
        }

        mTitleEditText.setText(timeReminder.getTitle());
        mDateTextView.setText(timeReminder.getDate());
        mTimeTextView.setText(timeReminder.getTime());
        mRepeatTextView.setText(timeReminder.getRepeat());
    }

    public TimeReminder getReminderData(){
        if (mTitleEditText.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(),getResources().getString(R.string.empty_title),Toast.LENGTH_SHORT).show();
            return null;
        }
        else if (mDateTextView.getText().toString().equalsIgnoreCase(getString(R.string.field_date_hint))) {
            Toast.makeText(getActivity(),getResources().getString(R.string.empty_date),Toast.LENGTH_SHORT).show();
            return null;
        }
        else if (mDateTextView.getText().toString().equalsIgnoreCase(getString(R.string.wrong_date))) {
            Toast.makeText(getActivity(),getResources().getString(R.string.wrong_date),Toast.LENGTH_SHORT).show();
            return null;
        }
        else if (mTimeTextView.getText().toString().equalsIgnoreCase(getString(R.string.field_time_hint))) {
            Toast.makeText(getActivity(),getResources().getString(R.string.empty_time),Toast.LENGTH_SHORT).show();
            return null;
        }

        return new TimeReminder(
                Constants.TYPE_TIME,
                mTitleEditText.getText().toString(),
                mDateTextView.getText().toString(),
                mTimeTextView.getText().toString(),
                mRepeatTextView.getText().toString(),
                mImageUri == null ? null : mImageUri.toString(),
                true,
                (int)System.currentTimeMillis()
        );
    }

    private void findViewsByIds(View view){
        mTimeTextView = (TextView) view.findViewById(R.id.time_picker);
        mDateTextView = (TextView) view.findViewById(R.id.date_picker);
        mRepeatTextView = (TextView) view.findViewById(R.id.repeat_picker);
        mPickImageTextView = (TextView) view.findViewById(R.id.tv_choose_img);
        mPicAwesomeTextView = (TextView) view.findViewById(R.id.tv_attach_icon);
        mDateAwesomeTextView = (TextView) view.findViewById(R.id.tv_date_icon);
        mRepeatAwesomeTextView = (TextView) view.findViewById(R.id.tv_repeat_icon);
        mTitleAwesomeTextView = (TextView) view.findViewById(R.id.tv_title_icon);
        mReminderAwesomeTextView = (TextView) view.findViewById(R.id.tv_time_icon);
        mTitleEditText = (EditText) view.findViewById(R.id.edit_title);
        mImageView = (ImageView) view.findViewById(R.id.iv_pic);
        fabSaveReminder = (FloatingActionButton) view.findViewById(R.id.fab_save_time);
    }


    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DCIM), albumName);
        return file;
    }

    private void openImageIntent() {
        Logger.e("openImageIntent enter");
        // Determine Uri of camera image to save.
        final String fname = Utils.getUniqueImageFilename();
        final File root = getAlbumStorageDir(getActivity() , fname);
        mImageUri = Uri.fromFile(root);
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e("onActivityResult enter");

        //put entire onActivityResut in try catch to catch the exception thrown by some camera apps
        //when image taking is cancelled
        try {
            if (resultCode == Activity.RESULT_OK) {
                Logger.e("onActivityResult enter:resultCode == Activity.RESULT_OK");

                if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                    Logger.e("onActivityResult enter:resultCode == Activity.RESULT_OK,requestCode == SELECT_PICTURE_REQUEST_CODE");
                    final boolean isCamera;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }
                    Uri selectedImageUri;
                    if (isCamera) {
                        selectedImageUri = mImageUri;
                    } else {
                        selectedImageUri = data == null ? null : data.getData();
                        mImageUri = selectedImageUri;
                    }

                    Logger.e("img sent to popup" + mImageUri.toString());

                    Glide.with(getActivity())
                            .load(selectedImageUri)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .placeholder(R.drawable.empty_photo)
                            .override(mImageSize, mImageSize)
                            .error(R.drawable.notavailable)
                            .into(mImageView);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Logger.e("img not selected");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        Logger.e("Create Time frag ondetach enter !!");
        super.onDetach();
    }

    @Override
    public void onStop() {
        Logger.e("Create Time frag onStop enter !!");
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.e("Create Time frag onActivityCreated enter !!");
    }

    @Override
    public void onAttach(Context activity) {
        Logger.e("Create Time frag onattach enter!!");
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (TimeFragInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    // Container Activity must implement this interface
    public interface TimeFragInterface {
        void onSaveTimeRemFabClicked();
        void onDeleteTimeRemClicked();
    }
}
