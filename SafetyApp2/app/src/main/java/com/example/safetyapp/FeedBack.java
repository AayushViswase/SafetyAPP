package com.example.safetyapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedBack extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId;

    {
        assert user != null;
        userId = user.getUid();
    }
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
                addFeedBackDetails(textSource,textDestination,time,selectedTimeOption,rating1,rating2,rating3);

                // Do something with the integer value
                //Toast.makeText(FeedBack.this, "Rating1: " + rating1+"Rating1: " + rating2+"Rating3: " + rating3, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFeedBackDetails(String textSource, String textDestination, String time, String selectedTimeOption, float rating1, float rating2, float rating3) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        assert firebaseUser != null;
        addFeedBack addFeedback = new addFeedBack(textSource, textDestination, time, selectedTimeOption, rating1, rating2, rating3);

        // Get a reference to the feedback node
        DatabaseReference feedbackReference = reference.child("feedback");

        // Get the current count of feedback entries
        feedbackReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();

                // Add the new feedback entry with the incremented count as its ID
                feedbackReference.child(String.valueOf(count + 1)).setValue(addFeedback)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Your feedback is greatly appreciated.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Get the error message from task.getException()
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Get the error message
                String errorMessage = databaseError.getMessage();

                // Log the error message
                Log.e(TAG, "Database error: " + errorMessage);

                // Show a toast message to the user
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }

        });
    }




}