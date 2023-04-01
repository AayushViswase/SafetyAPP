package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdditionalDetailsActivity extends AppCompatActivity {
    EditText firstPersonEmail, firstPersonPhone, secondPersonEmail, secondPersonPhone;
    Button saveDetailsButton;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);
        getSupportActionBar().setTitle("Additional Details");

        firstPersonEmail = findViewById(R.id.first_person_email_field);
        firstPersonPhone = findViewById(R.id.first_person_phone_field);
        secondPersonEmail = findViewById(R.id.second_person_email_field);
        secondPersonPhone = findViewById(R.id.second_person_phone_field);
        saveDetailsButton = findViewById(R.id.submit_button);

        // Get current user and user id
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        // Get database reference for the current user
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Set click listener for save details button
        saveDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });
    }

    private void saveDetails() {
        String firstEmail = firstPersonEmail.getText().toString().trim();
        String firstPhone = firstPersonPhone.getText().toString().trim();
        String secondEmail = secondPersonEmail.getText().toString().trim();
        String secondPhone = secondPersonPhone.getText().toString().trim();

        // Validate input fields
        if (firstEmail.isEmpty()) {
            firstPersonEmail.setError("Email is required!");
            firstPersonEmail.requestFocus();
            return;
        }

        if (firstPhone.isEmpty()) {
            firstPersonPhone.setError("Phone number is required!");
            firstPersonPhone.requestFocus();
            return;
        }

        if (secondEmail.isEmpty()) {
            secondPersonEmail.setError("Email is required!");
            secondPersonEmail.requestFocus();
            return;
        }

        if (secondPhone.isEmpty()) {
            secondPersonPhone.setError("Phone number is required!");
            secondPersonPhone.requestFocus();
            return;
        }

        // Save details to database
        databaseReference.child("firstEmail").setValue(firstEmail);
        databaseReference.child("firstPhone").setValue(firstPhone);
        databaseReference.child("secondEmail").setValue(secondEmail);
        databaseReference.child("secondPhone").setValue(secondPhone);
        Intent intent =new Intent(AdditionalDetailsActivity.this,HomePageActivity.class);
        startActivity(intent);
        finish();


        Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_SHORT).show();
    }
}
