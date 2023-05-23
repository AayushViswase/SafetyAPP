package com.example.safetyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class PathFinder extends AppCompatActivity {
    private EditText editTextSource, editTextDestination;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Spinner spinner;
    private ProgressBar progressBar; // Added progress bar reference
    String time;
    Button buttonSearch,buttonRouteFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_finder);

        editTextSource = findViewById(R.id.editText_source);
        editTextDestination = findViewById(R.id.editText_destination);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.clearCheck();
        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progress_bar1); // Initializing progress bar

       buttonSearch = findViewById(R.id.button_search);
        buttonRouteFind=findViewById(R.id.routefinder);
        buttonRouteFind.setOnClickListener(v -> {
            String textSource = editTextSource.getText().toString().toUpperCase();
            String textDestination = editTextDestination.getText().toString().toUpperCase();
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + textSource + "/" + textDestination);
            Intent intent12 = new Intent(Intent.ACTION_VIEW, uri);
            intent12.setPackage("com.google.android.apps.maps");
            intent12.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent12);

        });
        buttonSearch.setOnClickListener(v -> {
            String textSource = editTextSource.getText().toString().toUpperCase();
            String textDestination = editTextDestination.getText().toString().toUpperCase();

            if (TextUtils.isEmpty(textSource)) {
                Toast.makeText(PathFinder.this, "Please enter Source", Toast.LENGTH_LONG).show();
                editTextSource.setError("Starting point");
                editTextSource.requestFocus();
            } else if (TextUtils.isEmpty(textDestination)) {
                Toast.makeText(PathFinder.this, "Please enter Destination", Toast.LENGTH_LONG).show();
                editTextDestination.setError("Destination point");
                editTextDestination.requestFocus();
            } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                // No radio button is selected
                Toast.makeText(PathFinder.this, "Please select AM or PM", Toast.LENGTH_LONG).show();
                radioGroup.requestFocus();
            } else {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if (radioButton != null) {
                    if (radioButton.getId() == R.id.radio_AM) {
                        // AM radio button is selected
                        time = "AM";
                    } else if (radioButton.getId() == R.id.radio_PM) {
                        // PM radio button is selected
                        time = "PM";
                    }
                }
                // Get the selected item from the spinner
                String selectedTimeOption = spinner.getSelectedItem().toString();

                // Show progress bar
                progressBar.setVisibility(View.VISIBLE);

                // Disable button to prevent multiple clicks
                buttonSearch.setEnabled(false);

                // Perform the task in the background
                performBackgroundTask(textSource, textDestination, selectedTimeOption);

            }
        });
    }

    private void performBackgroundTask(String source, String destination, String selectedTimeOption) {
        // Perform your background task here (e.g., calling the Python code)

        // Example: Simulating a delay of 2 seconds using a Handler

            // Create a new Intent object with the current activity and the target activity class
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
            }
            Python py = Python.getInstance();
            PyObject module = py.getModule("script");


            String time_period = time.toUpperCase();
            String time_interval = selectedTimeOption.toUpperCase();
            PyObject runModels = module.get("run_model");
            PyObject result = runModels.call(source, destination, time_period, time_interval);
            String a = result.toString();
            Intent intent = new Intent(PathFinder.this, PythonRunner.class);

            intent.putExtra("source", source);
            intent.putExtra("destination", destination);
            intent.putExtra("time", time_period);
            intent.putExtra("interval", time_interval);
            intent.putExtra("a", a);

            startActivity(intent);
            finish();

        // Hide progress bar
        progressBar.setVisibility(View.GONE);

        // Re-enable the button
        buttonSearch.setEnabled(true);
    }
}
