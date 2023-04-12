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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ShakeDetector extends AppCompatActivity implements SensorEventListener {

    private double lastAcceleration;
    private double currentAcceleration;
    private HomePageActivity homePageActivity;
    private double acceleration;
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
            if (shakeCount > 3) {
                System.out.println("sfdsafsdfdsaf");
                //mailRequest();
                shakeCount = 0;
            } else {
                //Toast.makeText(context, "Shake count: " + shakeCount, Toast.LENGTH_SHORT).show();
                System.out.println("shakeCount");
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

//
//    private void mailRequest() {
//
//        System.out.println("hiii Anumapm");
//        FirebaseAuth authProfile = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = authProfile.getCurrentUser();
//
//        assert firebaseUser != null;
//        String userID = firebaseUser.getUid();
//        DatabaseReference refrenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
//        refrenceProfile.child(userID);
//        refrenceProfile.child("Details");
//        refrenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ReadWriteUserDetails readUserDetail = snapshot.getValue(ReadWriteUserDetails.class);
//                if (readUserDetail != null) {
//                    email = firebaseUser.getEmail();
//                }
//
//
//                String i = "Person_1", j = "Person_2", k = "Person_3";
//                DataRetrive(userID, i, firebaseUser,email);
//                DataRetrive(userID, j, firebaseUser,email);
//                DataRetrive(userID, k, firebaseUser,email);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "onCancelled", error.toException());
//            }
//
//
//            private void DataRetrive(String userID, String i, FirebaseUser firebaseUser, String userEmail) {
//                System.out.println("Firebase??????");
//                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference();
//                referenceProfile.child("Registered User").child(userID).child("Details").child(i).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        UpdateDetails readUserDetail = snapshot.getValue(UpdateDetails.class);
//                        if (readUserDetail != null) {
//                            name = firebaseUser.getDisplayName();
//                            email = firebaseUser.getEmail();
//                            mobile = readUserDetail.mobile;
//                            sendMail(email,userEmail);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.w(TAG, "onCancelled", error.toException());
//                    }
//                });
//            }
//
//
//            private void sendMail(String email,String userMail) {
//                // Create a new session with SMTP authentication
//                Properties props = new Properties();
//                props.put("mail.smtp.auth", "true");
//                props.put("mail.smtp.starttls.enable", "true");
//                props.put("mail.smtp.host", "smtp.gmail.com");
//                props.put("mail.smtp.port", "587");
//                Session session = Session.getInstance(props);
//                try {
//                    // Create a new message
//                    Message message = new MimeMessage(session);
//                    message.setFrom(new InternetAddress(userMail));
//                    // Set the recipient email address
//                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
//                    // Set the email subject and body
//                    message.setSubject("Emergency Alert");
//                    message.setText("I need help. Please contact me as soon as possible.");
//                    // Send the message
//                    Transport.send(message);
//                    Toast.makeText(ShakeDetector.this, "Email sent successfully.", Toast.LENGTH_SHORT).show();
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                    Toast.makeText(ShakeDetector.this, "Error occurred while sending email.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//}