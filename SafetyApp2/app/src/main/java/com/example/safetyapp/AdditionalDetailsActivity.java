package com.example.safetyapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdditionalDetailsActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = Objects.requireNonNull(user).getUid();


    private EditText nameEditText1, emailEditText1, phoneEditText1;
    private EditText nameEditText2, emailEditText2, phoneEditText2;
    private EditText nameEditText3, emailEditText3, phoneEditText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_additional_details);

        // Get references to all the EditText views in the layout
        nameEditText1 = findViewById(R.id.name_edittext1);
        emailEditText1 = findViewById(R.id.email_edittext1);
        phoneEditText1 = findViewById(R.id.phone_edittext1);

        nameEditText2 = findViewById(R.id.name_edittext2);
        emailEditText2 = findViewById(R.id.email_edittext2);
        phoneEditText2 = findViewById(R.id.phone_edittext2);

        nameEditText3 = findViewById(R.id.name_edittext3);
        emailEditText3 = findViewById(R.id.email_edittext3);
        phoneEditText3 = findViewById(R.id.phone_edittext3);

        // Get a reference to the Firebase Realtime Database node where the details will be stored
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDetailsRef = databaseReference.child("Registered User").child(userId);


        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(view -> {
            // Get the details entered in the EditText views
            String name1 = nameEditText1.getText().toString();
            String email1 = emailEditText1.getText().toString();
            String phone1 = phoneEditText1.getText().toString();

            String name2 = nameEditText2.getText().toString();
            String email2 = emailEditText2.getText().toString();
            String phone2 = phoneEditText2.getText().toString();

            String name3 = nameEditText3.getText().toString();
            String email3 = emailEditText3.getText().toString();
            String phone3 = phoneEditText3.getText().toString();


            //Validate mobile no.




            // Create a HashMap to store the details
            HashMap<String, Object> detailsMap = new HashMap<>();
            if(Check(name1, email1, phone1, nameEditText1, emailEditText1, phoneEditText1)) {
                detailsMap.put("name1", name1);
                detailsMap.put("email1", email1);
                detailsMap.put("phone1", phone1);
            }

            detailsMap.put("name2", name2);
            detailsMap.put("email2", email2);
            detailsMap.put("phone2", phone2);

            detailsMap.put("name3", name3);
            detailsMap.put("email3", email3);
            detailsMap.put("phone3", phone3);

            // Add the details to the Firebase Realtime Database node
            userDetailsRef.setValue(detailsMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AdditionalDetailsActivity.this, "Details added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdditionalDetailsActivity.this, "Failed to add details", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean Check(String name, String email, String phone, EditText nameEditText, EditText emailEditText, EditText phoneEditText) {
        //Validate mobile no.
        String mobileRegex="[6-9]\\d{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegex);
        mobileMatcher=mobilePattern.matcher(phone);
        if(TextUtils.isEmpty(name)){
            Toast.makeText(AdditionalDetailsActivity.this,"Please enter full name",Toast.LENGTH_LONG).show();
            nameEditText.setError("Full name Required");
            nameEditText.requestFocus();
        } else if(TextUtils.isEmpty(email)) {
            Toast.makeText(AdditionalDetailsActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
            emailEditText.setError("Email Required");
            emailEditText.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(AdditionalDetailsActivity.this, "Please Re-enter your email", Toast.LENGTH_LONG).show();
            emailEditText.setError("Valid email Required");
            emailEditText.requestFocus();
        }else if(TextUtils.isEmpty(phone)){
            Toast.makeText(AdditionalDetailsActivity.this, "Please enter your Mobile no.", Toast.LENGTH_LONG).show();
            phoneEditText.setError("Mobile No. is required Required");
            phoneEditText.requestFocus();
        } else if (phone.length()!=10) {
            Toast.makeText(AdditionalDetailsActivity.this, "Please Re-enter your mobile no.", Toast.LENGTH_LONG).show();
            phoneEditText.setError("Mobile No, should be 10 digit");
            phoneEditText.requestFocus();
        } else if(!mobileMatcher.find()){
            Toast.makeText(AdditionalDetailsActivity.this, "Please Re-enter your mobile no.", Toast.LENGTH_LONG).show();
            phoneEditText.setError("Mobile No, is not valid");
            phoneEditText.requestFocus();
        }else{
            return true;
        }
        return false;
    }
}
