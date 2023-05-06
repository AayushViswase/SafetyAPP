package com.example.safetyapp;

public class addFeedBack {
    public String Source;
    public String Destination;
    public String Time_Period;
    public String Interval;
    public float Road_Condition;
    public float Public_Activities;
    public float Feel_Safe;

    public addFeedBack() {
    }
    public addFeedBack(String textSource, String textDestination, String time, String selectedTimeOption, float rating1, float rating2, float rating3) {
        this.Source = textSource;
        this.Destination = textDestination;
        this.Time_Period = time;
        this.Interval = selectedTimeOption;
        this.Road_Condition = rating1;
        this.Public_Activities = rating2;
        this.Feel_Safe = rating3;
    }


}
