package com.example.myfirstapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BatteryPostJobService extends JobService {

    private static final String TAG = "BatteryPostJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(JobParameters params) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String myApiKey = BuildConfig.API_KEY;
                String myApiUrl = BuildConfig.BATTERY_API_URL;

                RequestBody formBody = new FormBody.Builder()
                        .add("batteryPct", params.getExtras().getString("batteryPct"))
                        .add("action", params.getExtras().getString("action"))
                        .add("action_time", params.getExtras().getString("action_time"))
                        .build();

                Request request = new Request.Builder()
                        .url(myApiUrl)
                        .post(formBody)
                        .addHeader("Authorization", myApiKey)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                try {
                    Response response = call.execute();
                    Objects.requireNonNull(response.body()).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return true;
    }
}
