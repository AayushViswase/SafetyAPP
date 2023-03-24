package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail,userNewEmail,userPassword;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail,editTextPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        getSupportActionBar().setTitle("Update Email");

        progressBar =findViewById(R.id.progressBar);
        editTextPwd=findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail=findViewById(R.id.editText_update_email_new);
        textViewAuthenticated=findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail=findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        //Set old email id on textview
        userOldEmail =firebaseUser.getEmail();
        TextView textViewOldEmail=findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong USer deatils not available", Toast.LENGTH_SHORT).show();

        }else{
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser=findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword=editTextPwd.getText().toString();

                if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(UpdateEmailActivity.this, "Password is needwdto contiune", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Please enter your password for authentication");
                    editTextPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail,userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified"+"You can update now", Toast.LENGTH_SHORT).show();

                                //set TextView to show that user is authenticated
                                textViewAuthenticated.setText("You are authenticated.You can update you email now.");

                                //disable update email button
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(true);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(false);
                                //chanege color
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.dark_reen));
                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail=editTextNewEmail.getText().toString();
                                        if(TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this, "New Email is required", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter new mail");
                                            editTextNewEmail.requestFocus();
                                        }else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(UpdateEmailActivity.this, "Please enter new email", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter new mail");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New Email cannot be same as old", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter new mail");
                                            editTextNewEmail.requestFocus();

                                        }
                                        else{
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }else{
                                try {
                                    throw task.getException();
                                }catch(Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
            firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isComplete()){
                        //verifiation mail
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(UpdateEmailActivity.this, "Email has been updated.Please verify your new Email", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(UpdateEmailActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
    }

    //Create ActionBar Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    //When any menu is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id==R.id.menu_update_profile) {
            Intent intent=new Intent(UpdateEmailActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UpdateEmailActivity.this,MainActivity.class);
            //Clear stack tpo prevent user comming back to userProfile Activityon back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            Toast.makeText(UpdateEmailActivity.this, "Somenthing went Wrong", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

}