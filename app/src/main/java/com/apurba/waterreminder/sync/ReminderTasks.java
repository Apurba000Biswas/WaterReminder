package com.apurba.waterreminder.sync;


import android.content.Context;

import com.apurba.waterreminder.utilities.PreferenceUtilities;

public class ReminderTasks{
    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";


    public static void executeTask(Context context, String action){
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)){
            incrementWaterCount(context);
        }
    }
    private static void incrementWaterCount(Context context){
        PreferenceUtilities.incrementWaterCount(context);
    }
// TODO (4) Add a Context called context to the argument list
// TODO (5) From incrementWaterCount, call the PreferenceUtility method that will ultimately update the water count
}


