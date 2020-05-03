package com.example.ppm_project;

import android.location.Location;

public class Patient extends Account {
    private Location patientLocation;
    private String patientPlusCodeURL;
    private double thresholdValue;

    public Patient(Account _account)
    {
        this.setFirstName(_account.getFirstName());
        this.setLastName(_account.getLastName());
        this.setEmailAddress(_account.getEmailAddress());
        this.setMobileNumber(_account.getMobileNumber());
        this.setUserID(_account.getUserID());
        this.setCloudID(_account.getCloudID());
        this.setHasCarer(_account.getHasCarer());
    }

    public void setPatientLocation(Location patientLocation) { this.patientLocation = patientLocation; }
    public void setThresholdValue(double givenThreshold) {this.thresholdValue = givenThreshold; }
    public void setPatientPlusCode(String plusCodeUrl) { this.patientPlusCodeURL = plusCodeUrl; }


    public Location getPatientLocation() { return patientLocation; }
    public double getThresholdValue() {return thresholdValue; }
    public String getPatientPlusCode() { return patientPlusCodeURL; }

}