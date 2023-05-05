package com.example.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
private EditText editTextLoginEmail,editTextLoginPwd;
private ProgressBar progressBar;
private FirebaseAuth authProfile;
private static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        editTextLoginEmail=findViewById(R.id.editText_login_email);
        editTextLoginPwd=findViewById(R.id.editText_login_pwd);
        progressBar=findViewById(R.id.progressBar);

        authProfile=FirebaseAuth.getInstance();

        //Reset Password
        TextView textViewLinkResetPwd=findViewById(R.id.textView_forgot_password_link);
        textViewLinkResetPwd.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "You can reset your password now", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
        });
        //register
        TextView textViewLinkRegister=findViewById(R.id.textView_register_link);
        textViewLinkRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));


        //Show Hide PPassword
        ImageView imageViewShowHidePwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(v -> {
            if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
            }else{
                editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
            }
        });

        //login User
        Button buttonLogin=findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            String textEmail=editTextLoginEmail.getText().toString();
            String textPwd=editTextLoginPwd.getText().toString();

            if (TextUtils.isEmpty(textEmail)){
                Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                editTextLoginEmail.setError("Email is required");
                editTextLoginEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(LoginActivity.this,"Please re-enter your mail",Toast.LENGTH_SHORT).show();
                editTextLoginEmail.setError("Valid Email required");
                editTextLoginEmail.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(LoginActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
                editTextLoginPwd.setError("Password is required");
                editTextLoginPwd.requestFocus();
            }else {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(textEmail,textPwd);
            }
        });
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, task -> {
            FirebaseUser firebaseUser;
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Verify Email", Toast.LENGTH_SHORT).show();
                //get instance of current User
                firebaseUser = authProfile.getCurrentUser();
                //Check if email is verified before user can access their profile
                if (Objects.requireNonNull(firebaseUser).isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    firebaseUser.sendEmailVerification();
                    authProfile.signOut();
                    showAlertDialog();
                }
            }
            else {

                try{
                    throw Objects.requireNonNull(task.getException());

                }catch (FirebaseAuthInvalidUserException e){
                    editTextLoginEmail.setError("User does not exist or is no longer valid.Please register again");
                    editTextLoginEmail.requestFocus();
                }catch (FirebaseAuthInvalidCredentialsException e){
                    editTextLoginEmail.setError("Invalid credentials.Kindly,check and re enter");
                    editTextLoginEmail.requestFocus();
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(LoginActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please Verify you Email now.You can not Login without email verification.");

        //Open email Apps if User clicks continue button
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }
    //Check user is already logged in
    @Override
    protected void onStart() {

        super.onStart();
        if(authProfile.getCurrentUser()!=null) {
            Toast.makeText(LoginActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }

        else {
            Toast.makeText(LoginActivity.this,"You can Login Now",Toast.LENGTH_SHORT).show();

        }
    }
}