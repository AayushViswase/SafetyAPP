package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
private EditText editTextUpdateName,editTextUpdateDoB,editTextUpdateMobile;
private RadioGroup radioGroupUpdateGender;
private RadioButton radioButtonUpdateGenderSelected;
private String textFullName,textDoB,textMobile,textGender;
private FirebaseAuth authProfile;
private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setTitle("Update Profile Details");

        progressBar=findViewById(R.id.progressBar);
        editTextUpdateName=findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB=findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile=findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender=findViewById(R.id.radio_group_update_profile_gender);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        //show profile data
        showProfile(firebaseUser);

        //Upload Profile pic
        Button buttonUplaodProfilePic=findViewById(R.id.button_update_profile);
        buttonUplaodProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
                    startActivity(intent);
                    finish();

            }
        });
        //UPDATE EMAIL
        Button buttonUpdateEmail=findViewById(R.id.button_update_profile);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();

            }
        });

        //Setting up DatePicker on EditText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String textSADoB[]=textDoB.split("/");
                int day=Integer.parseInt(textSADoB[0]);
                int month=Integer.parseInt(textSADoB[1])-1;
                int year=Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                
                //Date picker dialog
                picker =new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        
        //Update Profile
        Button buttonUpdateProfile=findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });


    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID=radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected=findViewById(selectedGenderID);

        //Vlaidate mobile no.
        String mobileRegex="[6-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegex);
        mobileMatcher=mobilePattern.matcher(textMobile);


        if(TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name Required");
            editTextUpdateName.requestFocus();
        }  else if(TextUtils.isEmpty(textDoB)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your Date of Birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of Birth is required Required");
            editTextUpdateDoB.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfileActivity.this, "Please Select your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required Required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if(TextUtils.isEmpty(textMobile)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your Mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is required Required");
            editTextUpdateMobile.requestFocus();
        } else if (textMobile.length()!=10) {
            Toast.makeText(UpdateProfileActivity.this, "Please Re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No, should be 10 digit");
            editTextUpdateMobile.requestFocus();
        } else if(!mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Please Re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No, is not valid");
            editTextUpdateMobile.requestFocus();

        }else{
            //Obtaining the data entered by user
            textGender=radioButtonUpdateGenderSelected.getText().toString();
            textFullName=editTextUpdateName.getText().toString();
            textDoB=editTextUpdateDoB.getText().toString();
            textMobile=editTextUpdateMobile.getText().toString();

            //Enter user data into the firebase database
            ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textDoB,textGender,textMobile);

            DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Register Users");
            
            String userID=firebaseUser.getUid();
            
            progressBar.setVisibility(View.VISIBLE);
            
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //setteting new display name
                        UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdate);

                        Toast.makeText(UpdateProfileActivity.this, "Update seccessfull", Toast.LENGTH_SHORT).show();

                        //Stop returning to update profileactivity
                        Intent intent=new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw  task.getException();
                        }catch (Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }

    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered=firebaseUser.getUid();

        //Extracting user reference from database for registered users
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails=snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    textFullName=firebaseUser.getDisplayName();
                    textDoB=readUserDetails.doB;
                    textGender=readUserDetails.gender;
                    textMobile=readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    //Show Gender through radio button
                    if(textGender.equals("Male")){
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_male);
                    }
                    else{
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_female);

                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //creating action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id==R.id.menu_update_profile) {
            Intent intent=new Intent(UpdateProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UpdateProfileActivity.this,MainActivity.class);
            //Clear stack tpo prevent user comming back to userProfile Activityon back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            Toast.makeText(UpdateProfileActivity.this, "Somenthing went Wrong", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
}