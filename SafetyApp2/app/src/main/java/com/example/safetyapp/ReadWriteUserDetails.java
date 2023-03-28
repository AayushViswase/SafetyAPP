package com.example.safetyapp;

public class ReadWriteUserDetails {
    public String fullName,doB,gender,mobile;


    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String doB,String fullName,String gender,String mobile) {
        this.fullName = fullName;
        this.doB = doB;
        this.gender = gender;
        this.mobile = mobile;
    }
}
