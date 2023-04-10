package com.example.safetyapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UploadProfilePicActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);
        setContentView(R.layout.activity_upload_profile_pic);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Upload Profile Picture");
        progressBar =findViewById(R.id.progressBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button buttonUploadPicChoose=findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic=findViewById(R.id.upload_pic_button);
        imageViewUploadPic=findViewById(R.id.imageView_profile_dp);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        storageReference= FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri=firebaseUser.getPhotoUrl();

        //Set Users current DP in ImageVIew(if uploaded already).We will pocasso since setImage
        //Regular URIs
        Picasso.with(UploadProfilePicActivity.this).load(uri).into(imageViewUploadPic);

        //Choosing image to upload
        buttonUploadPicChoose.setOnClickListener(v -> openFileChooser());

        //Upload Image
        buttonUploadPic.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            UploadPic();
        });




    }

    private void UploadPic() {
        if(uriImage!=null){
            //save the image with uid pf thr currently logged user
            StorageReference fileRefrence=storageReference.child(Objects.requireNonNull(authProfile.getCurrentUser()).getUid()+"/display-pic."+getFileExtention(uriImage));

            //Upload image to storage
            fileRefrence.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {

                fileRefrence.getDownloadUrl().addOnSuccessListener(uri -> {
                    firebaseUser=authProfile.getCurrentUser();

                    //finally set the display image of the user after upload
                    UserProfileChangeRequest profileUpdate=new  UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                    firebaseUser.updateProfile(profileUpdate);
                });
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UploadProfilePicActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(UploadProfilePicActivity.this,UserProfileActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> Toast.makeText(UploadProfilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePicActivity.this, "No file Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }
    //alternative for deprecated method///*********************************************************************************
//    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//                    if (result != null) {
//                        // Do something with the selected file
//                    }
//                }
//            });
//
//    private void openFileChooser() {
//        mGetContent.launch("image/*");
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriImage=data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
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
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(UploadProfilePicActivity.this);
        }else if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id==R.id.menu_update_profile) {
            Intent intent=new Intent(UploadProfilePicActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_change_password) {
            Intent intent = new Intent(UploadProfilePicActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_delete_profile) {
            Intent intent = new Intent(UploadProfilePicActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UploadProfilePicActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UploadProfilePicActivity.this,MainActivity.class);
            //Clear stack tpo prevent user coming back to userProfile Activation back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            Toast.makeText(UploadProfilePicActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
}