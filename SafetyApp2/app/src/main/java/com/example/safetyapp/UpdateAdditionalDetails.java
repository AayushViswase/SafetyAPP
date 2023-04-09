package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateAdditionalDetails extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId = user.getUid();


    private EditText nameEditText1, emailEditText1, phoneEditText1;
    private EditText nameEditText2, emailEditText2, phoneEditText2;
    private EditText nameEditText3, emailEditText3, phoneEditText3;

    private DatabaseReference databaseReference;

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
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                // Create a HashMap to store the details
                HashMap<String, Object> detailsMap = new HashMap<>();
                detailsMap.put("name1", name1);
                detailsMap.put("email1", email1);
                detailsMap.put("phone1", phone1);

                detailsMap.put("name2", name2);
                detailsMap.put("email2", email2);
                detailsMap.put("phone2", phone2);

                detailsMap.put("name3", name3);
                detailsMap.put("email3", email3);
                detailsMap.put("phone3", phone3);

                // Add the details to the Firebase Realtime Database node
                userDetailsRef.setValue(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateAdditionalDetails.this, "Details added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateAdditionalDetails.this, "Failed to add details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
