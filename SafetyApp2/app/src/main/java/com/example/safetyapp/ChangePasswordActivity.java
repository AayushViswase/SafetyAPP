package com.example.safetyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr,editTextPwdNew,editTextPwdConfirmNew;
    private TextView textViewAuthenticate;
    private Button buttonChangePwd,buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwsCurr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change Password");

        editTextPwdNew=findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr=findViewById(R.id.editText_change_pwd_current);
        //editTextPwdConfirmNew=findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticate=findViewById(R.id.textView_change_pwd_authenticated
        );
        progressBar =findViewById(R.id.progressBar);
        buttonReAuthenticate=findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd=findViewById(R.id.button_change_pwd);

        //Disable editText for new Password,confirm Password and make change in pwd button unclickeable
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong.User deatils not avalable", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();

        }else{
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwsCurr=editTextPwdCurr.getText().toString();
                if(TextUtils.isEmpty(userPwsCurr)){
                    Toast.makeText(ChangePasswordActivity.this, "Password needed", Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please enter you current password to authenticate");
                    editTextPwdCurr.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),userPwsCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);

                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(false);
                                editTextPwdConfirmNew.setEnabled(true);

                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                textViewAuthenticate.setText("You are Authenticated.you can change password");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified"+"change password now", Toast.LENGTH_SHORT).show();

                                buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.dark_reen));

                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew=editTextPwdNew.getText().toString();
        String userPwdConfirmNew=editTextPwdConfirmNew.getText().toString();
        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this, "New password  is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        } else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePasswordActivity.this, "Please confirm new password", Toast.LENGTH_SHORT).show();

            editTextPwdConfirmNew.setError("Please re-enter your new password");
            editTextPwdConfirmNew.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new password");
            editTextPwdNew.requestFocus();
        }else if (userPwsCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "Password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new password");
            editTextPwdNew.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password has been cganged", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ChangePasswordActivity.this,UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
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
            Intent intent=new Intent(ChangePasswordActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(ChangePasswordActivity.this,MainActivity.class);
            //Clear stack tpo prevent user comming back to userProfile Activityon back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            Toast.makeText(ChangePasswordActivity.this, "Somenthing went Wrong", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

}