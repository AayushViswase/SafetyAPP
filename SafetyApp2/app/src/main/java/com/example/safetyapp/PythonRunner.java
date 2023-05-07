package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class PythonRunner extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (!Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
            }
            Python py = Python.getInstance();
            PyObject module = py.getModule("script");
            PyObject num = module.get("predicted_feels_safe");
            if (num != null) {
                String strNum = num.toString();
                System.out.println("************************** id " + strNum);
            }
        }
    }




