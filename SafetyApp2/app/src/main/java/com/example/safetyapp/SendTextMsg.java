package com.example.safetyapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ValueEventListener;

public class SendTextMsg {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Context context;

    public SendTextMsg(Context context) {
        this.context = context;
    }

//    public SendTextMsg(ValueEventListener context) {
//        this.context = (Context) context;
//    }

    public void sendSMS(String phoneNumber) {
        if (checkPermission()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                String message="Please help me...\n" + "Check your mail for location";
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(context, "SMS sent.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "SMS sending failed.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) context, Manifest.permission.SEND_SMS)) {
            Toast.makeText(context, "SMS permission required to send messages.", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission granted. You can now send SMS.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Permission denied. SMS sending failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

