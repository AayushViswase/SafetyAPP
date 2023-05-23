package com.example.safetyapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonRunner extends AppCompatActivity {
    private String textSource;
    private String textDestination;
    private String time;
    private String selectedTimeOption;
    private String a;


    public void onBackPressed() {
        Intent intent=new Intent(PythonRunner.this,FeedBack.class);
        intent.putExtra("source", textSource);
        intent.putExtra("destination", textDestination);
        intent.putExtra("time", time);
        intent.putExtra("interval", selectedTimeOption);



        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_runner);
        Intent intent = getIntent();

// Retrieve the data from the Intent
        textSource = intent.getStringExtra("source");
        textDestination = intent.getStringExtra("destination");
        time = intent.getStringExtra("time").toUpperCase();
        selectedTimeOption = intent.getStringExtra("interval");
        a=intent.getStringExtra("a");
        if(!a.equals("0")) {

            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(a);
            DecimalFormat df = new DecimalFormat("#.##");
            matcher.find();
            String val1 = matcher.group(1);
            float val11 = Float.parseFloat(val1);

            String formatted1 = df.format(val11);
            float val111 = Float.parseFloat(formatted1);
            matcher.find();
            String val2 = matcher.group(1);
            float val22 = Float.parseFloat(val2);
            String formatted2 = df.format(val22);
            float val222 = Float.parseFloat(formatted2);
            matcher.find();
            String val3 = matcher.group(1);
            float val33 = Float.parseFloat(val3);
            String formatted3 = df.format(val33);
            float val333 = Float.parseFloat(formatted3);

            RatingBar ratingBar1 = findViewById(R.id.rating_bar);
            RatingBar ratingBar2 = findViewById(R.id.rating_bar2);
            RatingBar ratingBar3 = findViewById(R.id.rating_bar3);
            Button show_route = findViewById(R.id.showRoute);
            // Retrieve the Intent that started this activity

            // Retrieve the TextView from the layout
            ratingBar1.setRating(val111);
            ratingBar2.setRating(val222);
            ratingBar3.setRating(val333);
            TextView textView_sourceValue = findViewById(R.id.textView_sourceValue);
            textView_sourceValue.setText(textSource);
            TextView textView_destinationValue = findViewById(R.id.textView_destinationValue);
            textView_destinationValue.setText(textDestination);
            TextView textView_TimePeriodValue = findViewById(R.id.textView_TimePeriodValue);
            textView_TimePeriodValue.setText(time);
            TextView textView_IntervalValue = findViewById(R.id.textView_IntervalValue);
            textView_IntervalValue.setText(selectedTimeOption);
            TextView textView_rating_labelValue = findViewById(R.id.textView_rating_labelValue);
            textView_rating_labelValue.setText(Float.toString(val111));
            TextView textView_rating2_labelValue = findViewById(R.id.textView_rating2_labelValue);
            textView_rating2_labelValue.setText(Float.toString(val222));
            TextView textView_rating_label3Value = findViewById(R.id.textView_rating_label3Value);
            textView_rating_label3Value.setText(Float.toString(val333));


            show_route.setOnClickListener(view -> {
                Uri uri = Uri.parse("https://www.google.com/maps/dir/" + textSource + "/" + textDestination);
                Intent intent12 = new Intent(Intent.ACTION_VIEW, uri);
                intent12.setPackage("com.google.android.apps.maps");
                intent12.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent12);

            });


            }else{
            Toast.makeText(this, "No Data Available", Toast.LENGTH_SHORT).show();

            Intent intent1=new Intent(PythonRunner.this,FeedBack.class);
            // Create a new Intent object with the current activity and the target activity class

            intent1.putExtra("source", textSource);
            intent1.putExtra("destination", textDestination);
            intent1.putExtra("time", time);
            intent1.putExtra("interval", selectedTimeOption);


            startActivity(intent1);


        }
    }
}




