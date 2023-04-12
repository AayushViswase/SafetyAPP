package com.example.safetyapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    private ShakeDetector shakeDetector;
    @Override
    public void onBackPressed() {
        Toast.makeText(HomePageActivity.this, "Can Not Close App. Please remove from memory", Toast.LENGTH_SHORT).show();
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_page);
            shakeDetector = new ShakeDetector(this);

            // Register the listener with the sensor manager
            Sensor accelerometer = shakeDetector.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector.sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);

            Button instructionButton = findViewById(R.id.buton_instruction);
            Button profileButton = findViewById(R.id.button_profile);

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
        builder.setMessage("To send message please shake your handset thrice.." +
                "To start recording press Volume Down Button ONCE");




        //Return to Home activity is user presses OK button
        builder.setNeutralButton("OK", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog= builder.create();

        //cahnge Color
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red)));
        alertDialog.show();
    }

}

