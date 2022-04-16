package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TelephonyManager telemamanger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        @SuppressLint({"MissingPermission", "HardwareIds"}) String simNumber = telemamanger.getLine1Number();
        TextView textView = findViewById(R.id.phone_number);
        textView.setText(simNumber);

    }

    /** Called when the user taps the Show messages button */
    public void showMessages(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
        Intent intent = new Intent(this, ShowMessagesActivity.class);
        startActivity(intent);
    }
}