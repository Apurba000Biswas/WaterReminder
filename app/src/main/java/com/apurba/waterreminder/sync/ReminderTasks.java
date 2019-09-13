package com.apurba.waterreminder.sync;


import android.content.Context;

import com.apurba.waterreminder.utilities.NotificationUtils;
import com.apurba.waterreminder.utilities.PreferenceUtilities;

public class ReminderTasks{
    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action){
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)){
            incrementWaterCount(context);
        }else if (ACTION_DISMISS_NOTIFICATION.equals(action)){
            NotificationUtils.clearAllNotifications(context);
        }
    }
    private static void incrementWaterCount(Context context){
        PreferenceUtilities.incrementWaterCount(context);
        NotificationUtils.clearAllNotifications(context);
    }
// TODO (4) Add a Context called context to the argument list
// TODO (5) From incrementWaterCount, call the PreferenceUtility method that will ultimately update the water count
}


