package com.example.myfirstapp;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;

import okhttp3.MediaType;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        String smsBody = "";
        String sender = null;

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                smsBody += smsMessage.getMessageBody();
                sender = smsMessage.getOriginatingAddress();
            }
        }

        ComponentName componentName = new ComponentName(context, SmsPostJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("sender", sender);
        bundle.putString("body", smsBody);
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