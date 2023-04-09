package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class UpdateAdditionalDetails extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Registered User");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_additional_details);
        // Get a reference to the currently logged-in user's UID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = Objects.requireNonNull(currentUser).getUid();

// Get references to the EditText views and submit button
        EditText nameEditText = findViewById(R.id.name_edittext);
        EditText emailEditText = findViewById(R.id.email_edittext);
        EditText phoneEditText = findViewById(R.id.phone_edittext);
        Button submitButton = findViewById(R.id.submit_button);

// Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the user's inputs from the EditText fields
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                // Store the new user details in Firebase under the currently logged-in user's UID
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                userRef.child("name").setValue(name);
                userRef.child("email").setValue(email);
                userRef.child("phone").setValue(phone);

                // Clear the EditText fields
                nameEditText.setText("");
                emailEditText.setText("");
                phoneEditText.setText("");

                // Display a success message to the user
                Toast.makeText(getApplicationContext(), "Details saved successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}