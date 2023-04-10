package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the title
        Button buttonLogin=findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        });

        //Open Register Activity
        TextView textViewRegister=findViewById(R.id.textView_register_link);
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
    }
}