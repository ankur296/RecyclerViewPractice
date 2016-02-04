package seedling.corp.recyclerviewpractice.backend;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.activity.MainActivity;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.Logger;

/**
 * Created by Ankur Nigam on 11-12-2015.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();
    private String title;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        //TODO: Rxd a NPE for geofencingEvent here..check hwz tht possible
        if (geofencingEvent.hasError()) {
//            String errorMessage = GeofenceErrorMessages.getErrorString(this,
//                    geofencingEvent.getErrorCode());
//            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
                ) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            title = intent.getStringExtra("title");

            if (Logger.TEMP_CODE) {
                sendNotification(geofenceTransitionDetails);
                if (title.equalsIgnoreCase(""))
                    Toast.makeText(this, "HOLY SHIT: no title", Toast.LENGTH_SHORT).show();
            }

            Handler mHandler = new Handler(getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAlertDialog( title,
                            intent.getStringExtra("path"));
                }
            }, 5000);
            Logger.e("path rxd in service = "+intent.getStringExtra("path"));
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, "geofence_transition_invalid_type");
        }
    }

    private void showAlertDialog(String msg, String path){

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View rootView = li.inflate(R.layout.popup, null);
        LinearLayout outerLayout = (LinearLayout)rootView.findViewById(R.id.lay_popup);
        TextView titleTextView = (TextView)rootView.findViewById(R.id.title_popup_tv);
        Button cancelButton= (Button)rootView.findViewById(R.id.button_no);
        Button okayButton= (Button)rootView.findViewById(R.id.button_yes);
        final ImageView popupImageView = (ImageView)rootView.findViewById(R.id.popup_pic_iv);
        Uri uri = null;
        if (path!=null)
            uri = Uri.parse(path);

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
                .setCancelable(true)
                .setView(rootView)
                .create();

        newDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        newDialog.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        newDialog.show();

        titleTextView.setText(msg);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog.dismiss();
            }
        });
    }


    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
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
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(title)
//                .setContentText(title)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exit";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "Dwell";
            default:
                return "unknown";
        }
    }

}
