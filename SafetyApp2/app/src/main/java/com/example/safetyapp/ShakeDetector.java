package com.example.safetyapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShakeDetector extends AppCompatActivity implements SensorEventListener {

    private final double lastAcceleration;
    private final double currentAcceleration;
    private final HomePageActivity homePageActivity;
    private final double acceleration;
    protected SensorManager sensorManager;

    private String name, email, mobile;

    private Context mContext;

    private int shakeCount = 0;

    public ShakeDetector(HomePageActivity homePageActivity) {
        this.homePageActivity = homePageActivity;
        this.sensorManager = (SensorManager) homePageActivity.getSystemService(Context.SENSOR_SERVICE);
        this.acceleration = 0f;
        this.currentAcceleration = SensorManager.GRAVITY_EARTH;
        this.lastAcceleration = SensorManager.GRAVITY_EARTH;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        if (acceleration > 120) {
            shakeCount++;
            if (shakeCount >= 3) {
                Toast.makeText(homePageActivity, "Shake:" + shakeCount, Toast.LENGTH_SHORT).show();
                System.out.println("sfdsafsdfdsaf");
                mailRequest();
                shakeCount = 0;
            } else {
                Toast.makeText(homePageActivity, "Shake:" + shakeCount, Toast.LENGTH_SHORT).show();
                System.out.println("shakeCount");
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //
    private void mailRequest() {

        System.out.println("11111111111111111111111111111111111111");
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        String userID = firebaseUser.getUid();
        String i = "Person_1", j = "Person_2", k = "Person_3";
        DataRetrive(userID, i, firebaseUser, email);
        DataRetrive(userID, j, firebaseUser, email);
        DataRetrive(userID, k, firebaseUser, email);

    }

    private void DataRetrive(String userID, String i, FirebaseUser firebaseUser, String userEmail) {
        System.out.println("Firebase??????");

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference();
        referenceProfile.child("Registered User").child(userID).child("Details").child(i).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UpdateDetails readUserDetail = snapshot.getValue(UpdateDetails.class);
                if (readUserDetail != null) {

                    name = readUserDetail.name;
                    email = readUserDetail.email;
                    mobile = readUserDetail.mobile;
                    System.out.println(name + " " + email + " " + mobile + " " + firebaseUser.getEmail());
                    sendMail(email, userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }

    private void sendMail(String email, String userMail) {
        System.out.println("ssssssssssssssssssssssssssssssssssss");
        send se = new send(email, "Emergency Alert");
        se.execute();
    }
}