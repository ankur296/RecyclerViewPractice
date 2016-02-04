package seedling.corp.recyclerviewpractice.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import seedling.corp.recyclerviewpractice.utils.Logger;

/**
 * Created by Ankur Nigam on 17-12-2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmIntentService.class.getName());

         startWakefulService(context, (intent.setComponent(comp)));
        Logger.e( intent.getStringExtra("title"));
        Logger.e("img rxd at Rxr "+ intent.getStringExtra("path"));
    }
}
