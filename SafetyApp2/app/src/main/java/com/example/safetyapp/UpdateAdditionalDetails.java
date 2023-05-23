package com.example.safetyapp;

import android.content.Intent;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateAdditionalDetails extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId;

    {
        assert user != null;
        userId = user.getUid();
    }


    private EditText nameEditText1, emailEditText1, phoneEditText1;
    private EditText nameEditText2, emailEditText2, phoneEditText2;
    private EditText nameEditText3, emailEditText3, phoneEditText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);

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
            String i="Person_1",j="Person_2",k="Person_3";
            Check(name1, email1, phone1, nameEditText1, emailEditText1, phoneEditText1,i);
            Check(name2, email2, phone2, nameEditText2, emailEditText2, phoneEditText2,j);
            Check(name3, email3, phone3, nameEditText3, emailEditText3, phoneEditText3,k);
            Intent intent= new Intent(UpdateAdditionalDetails.this,UserProfileActivity.class);
            //to Prevent User from returning back to resister activity in pressing back button after details added
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //to close Details Activity
        });
    }

    private void Check(String name, String email, String phone, EditText nameEditText, EditText emailEditText, EditText phoneEditText,String person) {
        //Validate mobile no.
        String mobileRegex = "[6-9]\\d{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(phone);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(UpdateAdditionalDetails.this, "Please enter full name", Toast.LENGTH_SHORT).show();
            nameEditText.setError("Full name Required");
            nameEditText.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(UpdateAdditionalDetails.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Email Required");
            emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(UpdateAdditionalDetails.this, "Please Re-enter your email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Valid email Required");
            emailEditText.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(UpdateAdditionalDetails.this, "Please enter your Mobile no.", Toast.LENGTH_SHORT).show();
            phoneEditText.setError("Mobile No. is required Required");
            phoneEditText.requestFocus();
        } else if (phone.length() != 10) {
            Toast.makeText(UpdateAdditionalDetails.this, "Please Re-enter your mobile no.", Toast.LENGTH_SHORT).show();
            phoneEditText.setError("Mobile No, should be 10 digit");
            phoneEditText.requestFocus();
        } else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateAdditionalDetails.this, "Please Re-enter your mobile no.", Toast.LENGTH_SHORT).show();
            phoneEditText.setError("Mobile No, is not valid");
            phoneEditText.requestFocus();
        } else {
            updateUserDetails(name, email, phone,person);


        }
    }

    private void updateUserDetails(String name, String email, String phone,String person) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference();
        assert firebaseUser != null;
        UpdateDetails updateDetails = new UpdateDetails(name, email, phone);
        referenceProfile.child("Registered User").child(userId).child("Details").child(person).setValue(updateDetails);
    }
}



