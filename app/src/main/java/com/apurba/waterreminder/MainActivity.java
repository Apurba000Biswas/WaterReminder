package com.apurba.waterreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apurba.waterreminder.sync.ReminderTasks;
import com.apurba.waterreminder.sync.ReminderUtilities;
import com.apurba.waterreminder.sync.WaterReminderIntentService;
import com.apurba.waterreminder.utilities.PreferenceUtilities;


public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;

    private Toast mToast;

    IntentFilter mChargingIntentFilter;

    private ChargingBroadcastReceiver mChargingReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Get the views **/
        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        /** Set the original values in the UI **/
        updateWaterCount();
        updateChargingReminderCount();

        ReminderUtilities.scheduleChargingReminder(this);

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        mChargingIntentFilter = new IntentFilter();
        mChargingReceiver = new ChargingBroadcastReceiver();
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    }

    /**
     * Updates the TextView to display the new water count from SharedPreferences
     */
    private void updateWaterCount() {
        int waterCount = PreferenceUtilities.getWaterCount(this);
        mWaterCountDisplay.setText(waterCount+"");
    }

    /**
     * Updates the TextView to display the new charging reminder count from SharedPreferences
     */
    private void updateChargingReminderCount() {
        int chargingReminders = PreferenceUtilities.getChargingReminderCount(this);
        String formattedChargingReminders = getResources().getQuantityString(
                R.plurals.charge_notification_count, chargingReminders, chargingReminders);
        mChargingCountDisplay.setText(formattedChargingReminders);

    }

    private void showCharging(boolean isCharging){
        if (isCharging) {
            mChargingImageView.setImageResource(R.drawable.ic_power_pink_80px);

        } else {
            mChargingImageView.setImageResource(R.drawable.ic_power_grey_80px);
        }
    }

    /**
     * Adds one to the water count and shows a toast
     */
    public void incrementWater(View view) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, R.string.water_chug_toast, Toast.LENGTH_SHORT);
        mToast.show();

        Intent incrementWaterCountIntent = new Intent(this, WaterReminderIntentService.class);
        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);

        startService(incrementWaterCountIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            showCharging(batteryManager.isCharging());
        } else {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

            Intent currentBatteryStatusIntent = registerReceiver(null, ifilter);

            int batteryStatus = currentBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                    batteryStatus == BatteryManager.BATTERY_STATUS_FULL;
            showCharging(isCharging);
        }

        registerReceiver(mChargingReceiver, mChargingIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mChargingReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_WATER_COUNT.equals(key)) {
            updateWaterCount();
        } else if (PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT.equals(key)) {
            updateChargingReminderCount();
        }
    }


    private class ChargingBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isCharging = (action.equals(Intent.ACTION_POWER_CONNECTED));
            showCharging(isCharging);
        }
    }

}
