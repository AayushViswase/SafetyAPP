package com.example.safetyapp;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonRunner extends AppCompatActivity {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_runner);
        Intent intent = getIntent();

// Retrieve the data from the Intent
        String textSource = intent.getStringExtra("source").toUpperCase();
        String textDestination = intent.getStringExtra("destination").toUpperCase();
        String time = intent.getStringExtra("time").toUpperCase();
        String selectedTimeOption = intent.getStringExtra("interval").toUpperCase();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        PyObject module = py.getModule("script");

        String source = "Wakad";
        String destination = "Magarpatta";
        String time_period = "AM";
        String time_interval = "06:00-09:00";
        PyObject runModels = module.get("run_model");
        PyObject result = runModels.call(source, destination, time_period, time_interval);
        String a = result.toString();
        if(!a.equals("0")) {

            System.out.println(result);
            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(a);

            matcher.find();
            String val1 = matcher.group(1);
            double val11 = Float.parseFloat(val1);
            matcher.find();
            String val2 = matcher.group(1);
            double val22 = Float.parseFloat(val2);
            matcher.find();
            String val3 = matcher.group(1);
            double val33 = Float.parseFloat(val3);

            RatingBar ratingBar1 = findViewById(R.id.rating_bar);
            RatingBar ratingBar2 = findViewById(R.id.rating_bar2);
            RatingBar ratingBar3 = findViewById(R.id.rating_bar3);
            Button show_route = findViewById(R.id.showRoute);
            // Retrieve the Intent that started this activity

            // Retrieve the TextView from the layout
            ratingBar1.setRating((float) val11);
            ratingBar2.setRating((float) val22);
            ratingBar3.setRating((float) val22);
            TextView textView_sourceValue = findViewById(R.id.textView_sourceValue);
            textView_sourceValue.setText(textSource);
            TextView textView_destinationValue = findViewById(R.id.textView_destinationValue);
            textView_destinationValue.setText(textDestination);
            TextView textView_TimePeriodValue = findViewById(R.id.textView_TimePeriodValue);
            textView_TimePeriodValue.setText(time);
            TextView textView_IntervalValue = findViewById(R.id.textView_IntervalValue);
            textView_IntervalValue.setText(selectedTimeOption);
            TextView textView_rating_labelValue = findViewById(R.id.textView_rating_labelValue);
            textView_rating_labelValue.setText(val1);
            TextView textView_rating2_labelValue = findViewById(R.id.textView_rating2_labelValue);
            textView_rating2_labelValue.setText(val2);
            TextView textView_rating_label3Value = findViewById(R.id.textView_rating_label3Value);
            textView_rating_label3Value.setText(val3);

// Print the extracted values
            System.out.println(val1); // Output: 2.61365652
            System.out.println(val2); // Output: 3.
            System.out.println(val3); // Output: 2.

            System.out.println("************************** is " + result + " " + a);
            show_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Uri uri= Uri.parse("https://www.google.com/maps/dir/"+source+"/"+destination);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        intent.setPackage("com.google.android.apps.maps");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
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




