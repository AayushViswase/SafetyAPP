package com.example.safetyapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class HomePageActivity extends AppCompatActivity {
    private static final int VOLUME_DOWN_KEYCODE = 25;

    private static final int PERMISSIONS_REQUEST_INTERNET = 1;

    private ShakeDetector shakeDetector;



    private static final int SPEECH_REQUEST_CODE = 0;


    @Override
    public void onBackPressed() {
        Toast.makeText(HomePageActivity.this, "Can Not Close App. Please remove from memory", Toast.LENGTH_SHORT).show();
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_page);

//            // Check if the app has internet permission
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//                // Request internet permission
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_INTERNET);
//            }




            shakeDetector = new ShakeDetector(this);

            // Register the listener with the sensor manager
            Sensor accelerometer = shakeDetector.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector.sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);

            Button instructionButton = findViewById(R.id.buton_instruction);
            Button profileButton = findViewById(R.id.button_profile);
            Button safeZoneButton=findViewById(R.id.Safe_Zone);
            Button pathFinder=findViewById(R.id.Path_Finder);
            pathFinder.setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, PathFinder.class);
                startActivity(intent);

            });
            safeZoneButton.setOnClickListener(v -> {
//                startActivity(new Intent(HomePageActivity.this,SafeZone.class));
//                finish();
                Uri uri = Uri.parse("geo:0,0?q=police+station+nearby+me");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);


            });
            // Set onClickListener for instruction button
            instructionButton.setOnClickListener(view -> {
                // Handle instruction button click
                showAlertDialog();

            });



            // Set onClickListener for profile button
            profileButton.setOnClickListener(view -> {
                // Handle profile button click
                startActivity(new Intent(HomePageActivity.this,UserProfileActivity.class));

            });

        }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == VOLUME_DOWN_KEYCODE) {
//            Intent intent = new Intent(HomePageActivity.this, CaptureImage.class);
//            startActivity(intent);
//            finish();
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            startSpeechToText();
//            return true;
//
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }
private boolean isVolumeUpKeyPressed = false;
    private Handler mHandler = new Handler();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            isVolumeUpKeyPressed = true;
            mHandler.postDelayed(startIntentRunnable, 5000);
            //startSpeechToText();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Intent intent = new Intent(HomePageActivity.this, CaptureImage.class);
            startActivity(intent);
            finish();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            startSpeechToText();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            isVolumeUpKeyPressed = false;
            mHandler.removeCallbacks(startIntentRunnable);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private Runnable startIntentRunnable = new Runnable() {
        @Override
        public void run() {
            if (isVolumeUpKeyPressed) {
                Intent intent = new Intent(HomePageActivity.this, AudioRecord.class);
                startActivity(intent);
                finish();
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == PERMISSIONS_REQUEST_INTERNET) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission denied, show a message to the user or exit the app
                Toast.makeText(this, "Internet permission required to send email.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener to conserve resources
        shakeDetector.sensorManager.unregisterListener(shakeDetector);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener again when the activity resumes
        Sensor accelerometer = shakeDetector.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector.sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(HomePageActivity.this);
        builder.setTitle("App Instructions");
        //msg updated BY ANUPAM
        builder.setMessage("To send message please shake your handset thrice..\n" +
                        "\n"+
                        "To start recording press Volume Down Button ONCE\n"+
                        "\n"+
                        "To capture image press Volume Up Button ONCE\n"+
                        "\n"+
                        "To find nearet police station and your current location press Safe Zone Button\n"+"\n"+
                "To know about the route before travel press Path Finder Button"+"\n");




        //Return to Home activity is user presses OK button
        builder.setNeutralButton("OK", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog= builder.create();

        //change Color
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red)));
        alertDialog.show();
    }

    private void startSpeechToText() {
        // Create an intent to launch the speech to text activity
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Start the activity and wait for the result
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the speech to text result
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if (spokenText != null) {
                sendTextEmail(spokenText);
                Toast.makeText(HomePageActivity.this, "Mail Sent Successfully ", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendTextEmail(String spokenText) {

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        String userID = firebaseUser.getUid();
        String i = "Person_1", j = "Person_2", k = "Person_3";
        DataRetrieve1(userID, i, spokenText);
        DataRetrieve1(userID, j, spokenText);
        DataRetrieve1(userID, k, spokenText);

    }

    private void DataRetrieve1(String userID, String i, String spokenText) {

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference();
        referenceProfile.child("Registered User").child(userID).child("Details").child(i).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UpdateDetails readUserDetail = snapshot.getValue(UpdateDetails.class);
                if (readUserDetail != null) {
                    String email = readUserDetail.email;
                    new Thread(() -> sendMailMsg1(email,spokenText)).start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendMailMsg1(String email, String spokenText){

        // Create a LocationManager object to obtain the user's location

        // Check if the user has granted permission to access their location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request the user's location

            // Send an email with the user's location
            String username = "aayushviswase008@gmail.com";
            String password = "ctiibcsgzchbbpeu";
            String subject = "Text";
            System.out.println("sendmail");
            CompletableFuture<Boolean> emailResult = SendSpeechText.sendEmailST(username, password, email, subject,spokenText);
            emailResult.thenAccept(success -> {
                if (success) {
                    System.out.println("Text Email sent successfully");
                } else {
                    System.out.println("Text Email sending failed");
                }
            }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        } else {
            // Request permission to access the user's location
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

}

