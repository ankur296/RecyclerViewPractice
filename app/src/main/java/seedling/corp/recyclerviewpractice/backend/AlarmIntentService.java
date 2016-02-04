package seedling.corp.recyclerviewpractice.backend;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;

import java.io.IOException;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.activity.MainActivity;
import seedling.corp.recyclerviewpractice.fragment.SettingsFragment;
import seedling.corp.recyclerviewpractice.model.TimeReminder;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.Logger;
import seedling.corp.recyclerviewpractice.utils.Utils;

public class AlarmIntentService extends IntentService {

    private static final String TAG = AlarmIntentService.class.getSimpleName();

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        Handler mHandler = new Handler(getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAlertDialog(
                        intent.getStringExtra("title"),
                        intent.getStringExtra("path"),
                        intent.getIntExtra("id", 0)
                );
            }
        }, 5000);
    }

    private void sendNotification(final String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("geofence_transition_notification_text")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private void setCorrectTheme(TextView textView, Button button1, Button button2){
        String currThemeSet = getSharedPreferences("theme",MODE_PRIVATE).getString("color", "default");

        if (currThemeSet.equalsIgnoreCase("red")) {
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_RED));
            button1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_RED));
            button2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_RED));

            GradientDrawable drawable = (GradientDrawable)button1.getBackground();
            drawable.setStroke(Utils.dpToPx(2),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_RED));
            GradientDrawable drawable2 = (GradientDrawable)button2.getBackground();
            drawable2.setStroke(Utils.dpToPx(2),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_RED));
        }
        else if (currThemeSet.equalsIgnoreCase("green")) {
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_GREEN));
            button1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_GREEN));
            button2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_GREEN));

            GradientDrawable drawable = (GradientDrawable)button1.getBackground();
            drawable.setStroke(Utils.dpToPx(2),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_GREEN));
            GradientDrawable drawable2 = (GradientDrawable)button2.getBackground();
            drawable2.setStroke(Utils.dpToPx(2),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_GREEN));
        }
        else {
            textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            button1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            button2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

            GradientDrawable drawable = (GradientDrawable)button1.getBackground();
            drawable.setStroke(Utils.dpToPx(2),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            GradientDrawable drawable2 = (GradientDrawable)button2.getBackground();
            drawable2.setStroke(Utils.dpToPx(2),
                    ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
    }

    private void showAlertDialog(final String msg, String path, final int alarmId){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View rootView = li.inflate(R.layout.popup, null);
        LinearLayout outerLayout = (LinearLayout)rootView.findViewById(R.id.lay_popup);
        TextView titleTextView = (TextView)rootView.findViewById(R.id.title_popup_tv);
        final Button cancelButton= (Button)rootView.findViewById(R.id.button_no);
        Button okayButton= (Button)rootView.findViewById(R.id.button_yes);
        final ImageView popupImageView = (ImageView)rootView.findViewById(R.id.popup_pic_iv);

        //set color of text based on theme
        setCorrectTheme(titleTextView, okayButton, cancelButton);

        Uri uri = null;
        if (path!=null)
            uri=Uri.parse(path);

        int imageSize = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;

        Glide.with(getApplicationContext())
                .load(uri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.color.lighter_gray)
                .error(R.mipmap.ic_launcher)
                .override(imageSize, imageSize)
                .into(new BitmapImageViewTarget(popupImageView) {
                          @Override
                          protected void setResource(Bitmap resource) {
                              RoundedBitmapDrawable circularBitmapDrawable =
                                      RoundedBitmapDrawableFactory.create(
                                              getApplicationContext().getResources(), resource);
                              circularBitmapDrawable.setCircular(true);
                              popupImageView.setImageDrawable(circularBitmapDrawable);
                          }
                      }
                );

        final AlertDialog newDialog = new AlertDialog.Builder(getApplicationContext(), R.style.myDialog)
                .setCancelable(false)
                .setView(rootView)
                .create();

        newDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        newDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        newDialog.show();

        //play ringtone
        startRingtone();
        startVibrate();
        titleTextView.setText(msg);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "okay", Toast.LENGTH_SHORT).show();
                stopRingtone();
                stopVibrator();
                newDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
                stopRingtone();
                stopVibrator();
                cancelAlarm(msg, alarmId);
                newDialog.dismiss();
            }
        });
    }


    private void cancelAlarm(String title, int id){
        Logger.e("id of the alarm being disabled  = "+id);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        //change the enable field of the TimeRemainder obj so that the UI shows RED clock
        updateEnabledState(title);
    }

    private void updateEnabledState(String title) {
        Gson gson = new Gson();
        SharedPreferences preferences = getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String json =   preferences.getString(title, null);
        Logger.e("Disabled reminder json = " + json);

        if (json.contains("\"type\":\"Time\"")) {
            Logger.e("time rem clicked");
            TimeReminder timeReminder = gson.fromJson(json, TimeReminder.class);
            timeReminder.setEnabled(false);
            json = gson.toJson(timeReminder);
            editor.putString(timeReminder.getTitle(), json);
            editor.apply();
        }
        else{
                TimeReminder timeReminder = gson.fromJson(json, TimeReminder.class);
        }
    }

    private void startVibrate(){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        boolean enableVibration = sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_VIBRATE, false);
        if (enableVibration)
        {
            mVibrator = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(50000);
        }
    }

    private void startRingtone(){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        //chk if play sound option disabled
        if (sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_SOUND,true)) {
            String ringtoneType = sharedPreferences.getString(SettingsFragment.KEY_PREF_RINGTONE, "default ringtone");
            Uri uri = Uri.parse(ringtoneType);
            playSound(this, uri);
        }
    }

    private void stopRingtone(){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        //chk if play sound option disabled
        if (sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_SOUND,true)) {
            mMediaPlayer.stop();
        }
    }

    private void stopVibrator(){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        boolean enableVibration = sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_VIBRATE,false);
        if (enableVibration)
        {
            mVibrator.cancel();
        }
    }

    private MediaPlayer mMediaPlayer;
    Vibrator mVibrator;

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }
}
