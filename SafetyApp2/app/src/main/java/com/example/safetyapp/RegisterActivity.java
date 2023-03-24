package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDoB,editTextRegisterMobile,editTextRegisterPwd,editTextRegisterConfirmPwd;
   private ProgressBar progressBar;
   private RadioGroup radioGroupRegisterGender;
   private RadioButton radioButtonRegisterGenderSelected;
   private DatePickerDialog picker;
   private  static final String TAG="RegisterActivity";

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    Toast.makeText(RegisterActivity.this,"You can Register now",Toast.LENGTH_LONG).show();
    editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
    editTextRegisterEmail=findViewById(R.id.editText_register_email);
    editTextRegisterDoB=findViewById(R.id.editText_register_dob);
    editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
    editTextRegisterPwd=findViewById(R.id.editText_register_password);


    //RadioButton for gender
    radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
    radioGroupRegisterGender.clearCheck();

    //Setting up DatePicker on EditText
    editTextRegisterDoB.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v){
            final Calendar calendar= Calendar.getInstance();
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            int month=calendar.get(Calendar.MONTH);
            int year=calendar.get(Calendar.YEAR);

            //Date picker dialog
            picker =new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    editTextRegisterDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
            },year,month,day);
            picker.show();
        }
    });

    Button buttonRegister=findViewById(R.id.button_register);
    buttonRegister.setOnClickListener(new View.OnClickListener() {
        @SuppressLint("ResourceType")
        @Override
        public void onClick(View v) {
            int selectedGenderId=radioGroupRegisterGender.getCheckedRadioButtonId();
            radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

            //Obtain the Entered data
            String textFullName=editTextRegisterFullName.getText().toString();
            String textEmail=editTextRegisterEmail.getText().toString();
            String textDoB=editTextRegisterDoB.getText().toString();
            String textMobile=editTextRegisterMobile.getText().toString();
            String textPwd=editTextRegisterPwd.getText().toString();
            String textComfirmPwd=editTextRegisterConfirmPwd.getText().toString();
            String textGender;

            //Vlaidate mobile no.
            String mobileRegex="[6-9][0-9]{9}";
            Matcher mobileMatcher;
            Pattern mobilePattern=Pattern.compile(mobileRegex);
            mobileMatcher=mobilePattern.matcher(textMobile);


            if(TextUtils.isEmpty(textFullName)){
                Toast.makeText(RegisterActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                editTextRegisterFullName.setError("Full name Required");
                editTextRegisterFullName.requestFocus();
            } else if(TextUtils.isEmpty(textEmail)) {
                Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                editTextRegisterEmail.setError("Email Required");
                editTextRegisterEmail.requestFocus();
            } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(RegisterActivity.this, "Please Re-enter your email", Toast.LENGTH_LONG).show();
                editTextRegisterEmail.setError("Valid email Required");
                editTextRegisterEmail.requestFocus();
            } else if(TextUtils.isEmpty(textDoB)) {
                Toast.makeText(RegisterActivity.this, "Please enter your Date of Birth", Toast.LENGTH_LONG).show();
                editTextRegisterDoB.setError("Date of Birth is required Required");
                editTextRegisterDoB.requestFocus();
            } else if (radioGroupRegisterGender.getCheckedRadioButtonId()==1){
                Toast.makeText(RegisterActivity.this, "Please Select your gender", Toast.LENGTH_LONG).show();
                radioButtonRegisterGenderSelected.setError("Gender is required Required");
                radioButtonRegisterGenderSelected.requestFocus();
            } else if(TextUtils.isEmpty(textMobile)){
                Toast.makeText(RegisterActivity.this, "Please enter your Mobile no.", Toast.LENGTH_LONG).show();
                editTextRegisterMobile.setError("Mobile No. is required Required");
                editTextRegisterMobile.requestFocus();
            } else if (textMobile.length()!=10) {
                Toast.makeText(RegisterActivity.this, "Please Re-enter your mobile no.", Toast.LENGTH_LONG).show();
                editTextRegisterMobile.setError("Mobile No, should be 10 digit");
                editTextRegisterMobile.requestFocus();
            } else if(!mobileMatcher.find()){
                Toast.makeText(RegisterActivity.this, "Please Re-enter your mobile no.", Toast.LENGTH_LONG).show();
                editTextRegisterMobile.setError("Mobile No, is not valid");
                editTextRegisterMobile.requestFocus();

            }else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(RegisterActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                editTextRegisterDoB.setError("Password is Required");
                editTextRegisterDoB.requestFocus();
            } else if(textPwd.length()<6){
                Toast.makeText(RegisterActivity.this,"Password should be at least 6 digit",Toast.LENGTH_LONG).show();
                editTextRegisterPwd.setError("Password to weak");
                editTextRegisterPwd.requestFocus();
            } else if (TextUtils.isEmpty(textComfirmPwd)) {
                Toast.makeText(RegisterActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                editTextRegisterConfirmPwd.setError("Password Confirmation is Required");
                editTextRegisterConfirmPwd.requestFocus();
            } else if (!textComfirmPwd.equals(textPwd)) {
                Toast.makeText(RegisterActivity.this, "Please enter same Password", Toast.LENGTH_LONG).show();
                editTextRegisterConfirmPwd.setError("Password Confirmation is Required");
                editTextRegisterConfirmPwd.requestFocus();
                editTextRegisterPwd.clearComposingText();
                editTextRegisterConfirmPwd.clearComposingText();
            } else{
                textGender=radioButtonRegisterGenderSelected.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPwd);
            }


        }
    });

}
//Register USer using the credential given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    //Update DIsplay Name of USer
                    UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(userProfileChangeRequest);
                    //Enter USer data into the firebase database.
                    ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textDoB,textGender,textMobile);

                    //EXTRACTING  USER FROM REFRENCE FROM DATABASE FOR "register users"
                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered User");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//Send Verification Email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this,"User Registerered succesfully.Please verify your email",Toast.LENGTH_LONG).show();


                                //Open User Profile after susseful registration
                                Intent intent= new Intent(RegisterActivity.this,UserProfileActivity.class);
                                //to Prevent User from returning back to resiter activity in pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close Register Activity
                            }else {
                                Toast.makeText(RegisterActivity.this,"User Registeration failed.try again",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
                else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        editTextRegisterPwd.setError("Your password is to weak.KIndly use a mix ");
                        editTextRegisterPwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterPwd.setError("Email is invalid or already in use.Kindly Re-enter ");
                        editTextRegisterPwd.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterPwd.setError("User already register with this email.Use another email");
                        editTextRegisterPwd.requestFocus();
                    }catch(Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}