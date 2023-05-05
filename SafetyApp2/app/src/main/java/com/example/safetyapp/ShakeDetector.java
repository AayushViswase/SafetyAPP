package com.example.safetyapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

public class ShakeDetector extends AppCompatActivity implements SensorEventListener {

    private final HomePageActivity homePageActivity;
    protected SensorManager sensorManager;


    private String name, email, mobile;

    private int shakeCount = 0;
    private LocationManager locationManager;

    public ShakeDetector(HomePageActivity homePageActivity) {
        this.homePageActivity = homePageActivity;
        this.sensorManager = (SensorManager) homePageActivity.getSystemService(Context.SENSOR_SERVICE);
        this.locationManager = (LocationManager) homePageActivity.getSystemService(Context.LOCATION_SERVICE);


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

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        String userID = firebaseUser.getUid();
        String i = "Person_1", j = "Person_2", k = "Person_3";
        DataRetrieve(userID, i, firebaseUser);
        DataRetrieve(userID, j, firebaseUser);
        DataRetrieve(userID, k, firebaseUser);

    }

    private void DataRetrieve(String userID, String i, FirebaseUser firebaseUser) {

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
                    new Thread(() -> sendMailMsg(email)).start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }


    private void sendMailMsg(String email){

        // Create a LocationManager object to obtain the user's location
        LocationManager locationManager = (LocationManager) homePageActivity.getSystemService(Context.LOCATION_SERVICE);
        // Check if the user has granted permission to access their location
        if (ActivityCompat.checkSelfPermission(homePageActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request the user's location
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Send an email with the user's location
            String username = "aayushviswase008@gmail.com";
            String password = "ctiibcsgzchbbpeu";
            String recipientEmail = email.toString();
            String subject = "Emergency Alert";
            System.out.println("sendmail");
                CompletableFuture<Boolean> emailResult = SendEmailTask.sendEmail(username, password, recipientEmail, subject, location);
                emailResult.thenAccept(success -> {
                            if (success) {
                                homePageActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(homePageActivity, "Email sent successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                homePageActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(homePageActivity, "Email sending failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });
            } else {
            // Request permission to access the user's location
            ActivityCompat.requestPermissions(homePageActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        }
    }

