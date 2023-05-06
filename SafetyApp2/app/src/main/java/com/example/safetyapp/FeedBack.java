package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FeedBack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        RatingBar ratingBar1 = findViewById(R.id.rating_bar);
        RatingBar ratingBar2 = findViewById(R.id.rating_bar2);
        RatingBar ratingBar3 = findViewById(R.id.rating_bar3);
        Button submitButton = findViewById(R.id.rating_submit_button);
        // Retrieve the Intent that started this activity
        Intent intent = getIntent();

// Retrieve the data from the Intent
        String textSource = intent.getStringExtra("source");
        String textDestination = intent.getStringExtra("destination");
        String time = intent.getStringExtra("time");
        String selectedTimeOption = intent.getStringExtra("interval");
        // Retrieve the TextView from the layout
        TextView textView_sourceValue = findViewById(R.id.textView_sourceValue);
        textView_sourceValue.setText(textSource);
        TextView textView_destinationValue = findViewById(R.id.textView_destinationValue);
        textView_destinationValue.setText(textDestination);
        TextView textView_TimePeriodValue = findViewById(R.id.textView_TimePeriodValue);
        textView_TimePeriodValue.setText(time);
        TextView textView_IntervalValue = findViewById(R.id.textView_IntervalValue);
        textView_IntervalValue.setText(selectedTimeOption);




        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating1 = ratingBar1.getRating();
                float rating2 = ratingBar2.getRating();
                float rating3 = ratingBar3.getRating();

                // Do something with the integer value
                Toast.makeText(FeedBack.this, "Rating1: " + rating2+"Rating1: " + rating2+"Rating3: " + rating3, Toast.LENGTH_SHORT).show();
            }
        });
    }

}