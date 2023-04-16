package com.example.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteProfileActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPwd;
    private Button buttonReAuthenticate,buttonDeleteUser;
    private static final String TAG="DeleteProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Delete Your Profile");

        progressBar = findViewById(R.id.progressBar);
        editTextUserPwd = findViewById(R.id.editText_delete_user_pwd);
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonReAuthenticate = findViewById(R.id.button_delete_user_authenticate);

        //Disable Delete user until user is Authenticated
        buttonDeleteUser.setEnabled(false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong" + "UserDetails not available", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(DeleteProfileActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(v -> {
            userPwd=editTextUserPwd.getText().toString();
            if(TextUtils.isEmpty(userPwd)){
                Toast.makeText(DeleteProfileActivity.this, "Password needed", Toast.LENGTH_SHORT).show();
                editTextUserPwd.setError("Please enter you current password to authenticate");
                editTextUserPwd.requestFocus();
            }else{
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential= EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()),userPwd);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);

                        editTextUserPwd.setEnabled(false);


                        buttonReAuthenticate.setEnabled(false);
                        buttonDeleteUser.setEnabled(true);

                        textViewAuthenticated.setText("You are Authenticated.You can delete profile now");
                        Toast.makeText(DeleteProfileActivity.this, "Password has been verified"+"You can delete profile now", Toast.LENGTH_SHORT).show();

                        buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this,R.color.dark_reen));

                        buttonDeleteUser.setOnClickListener(v1 -> showAlertDialog());
                    }else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch (Exception e){
                            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete user and Related DATA?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible");


        //Open email Apps if User clicks continue button
        builder.setPositiveButton("Continue", (dialog, which) -> deleteUserData(firebaseUser));
        //Return to user profile activity is user presses cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Intent intent=new Intent(DeleteProfileActivity.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        });

        AlertDialog alertDialog= builder.create();

        //change Color
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red)));
        alertDialog.show();
    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                authProfile.signOut();
                Toast.makeText(DeleteProfileActivity.this, "Profile has been deleted.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void deleteUserData(FirebaseUser firebaseUser) {

        if(firebaseUser.getPhotoUrl()!=null){
            FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
            StorageReference storageReference=firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(unused -> Log.d(TAG,"OnSuccess: Photo Deleted")).addOnFailureListener(e -> {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }


        //Delete Data from Realtime Database
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered User");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(unused -> {

            Log.d(TAG,"ONSuccess:User Data Deleted");
            deleteUser();
        }).addOnFailureListener(e -> {
            Log.d(TAG, e.getMessage());
            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Intent intent=new Intent(DeleteProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_change_password) {
            Intent intent = new Intent(DeleteProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_delete_profile) {
            Intent intent = new Intent(DeleteProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(DeleteProfileActivity.this,MainActivity.class);
            //Clear stack tpo prevent user coming back to userProfile Activation back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            Toast.makeText(DeleteProfileActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
}
