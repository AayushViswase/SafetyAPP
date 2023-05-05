package com.example.safetyapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CaptureImage extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private ImageView imageView;


    private Uri photoUri;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CaptureImage.this,HomePageActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Capture Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        imageView = findViewById(R.id.imageView);
        Button sendImage = findViewById(R.id.Send_image);
        sendImage.setOnClickListener(v -> sendImageOnMail("aayushviswase10@gmail.com","aayushviswase07@gmail.com"));
        Button captureButton = findViewById(R.id.button_capture);
        requestCameraPermission();
        captureButton.setOnClickListener(v -> {
            requestCameraPermission(); // call requestCameraPermission() when the capture button is clicked
        });

    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "My Image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image Captured By Camera");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            // Save the image to the MediaStore
            ContentResolver resolver = getContentResolver();
            photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            System.out.println(photoUri);
            // Save the captured image to the MediaStore
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }
    private void sendImageOnMail(String senderEmail, String recipientEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "HELP!!!");
        emailIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_CC, new String[]{senderEmail});

        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Show the captured image in the ImageView
            System.out.println(photoUri);

            imageView.setImageURI(photoUri);
        }
    }




    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                captureImage();
            }
        } else {
            captureImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
