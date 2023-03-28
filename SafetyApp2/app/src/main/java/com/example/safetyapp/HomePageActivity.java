package com.example.safetyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class HomePageActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Toast.makeText(HomePageActivity.this, "Can Not Close App. Please remove from memory", Toast.LENGTH_SHORT).show();
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_page);


            Button instructionButton = findViewById(R.id.buton_instruction);
            Button profileButton = findViewById(R.id.button_profile);

            // Set onClickListener for instruction button
            instructionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle instruction button click
                    showAlertDialog();

                }
            });



            // Set onClickListener for profile button
            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle profile button click
                    startActivity(new Intent(HomePageActivity.this,UserProfileActivity.class));

                }
            });
        }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(HomePageActivity.this);
        builder.setTitle("App Instructions");
        builder.setMessage("WRITE INSTRUCTIONS *");



        //Return to Home activity is user presses OK button
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog= builder.create();

        //cahnge Color
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        alertDialog.show();
    }
}

