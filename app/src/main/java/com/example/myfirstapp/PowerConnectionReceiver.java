package com.example.myfirstapp;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerConnectionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = "";
        switch (intent.getAction()) {
            case Intent.ACTION_POWER_CONNECTED:
                action = "Power Connected";
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                action = "Power Disconnected";
                break;
        }

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        Date action_time = Calendar.getInstance().getTime();

        ComponentName componentName = new ComponentName(context, BatteryPostJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("batteryPct", String.valueOf(batteryPct));
        bundle.putString("action", action);
        bundle.putString("action_time", String.valueOf(action_time));
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(bundle)
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }
}
