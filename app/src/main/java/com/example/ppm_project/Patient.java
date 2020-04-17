package com.example.ppm_project;

import android.location.Location;

public class Patient extends Account {
    private Location patientLocation;
    private String patientPlusCodeURL;
    private Carer theCarer;
    private boolean isInDistress = false;
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
    public void setTheCarer(Carer newCarer) { this.theCarer = newCarer; }
    public void setThresholdValue(double givenThreshold) {this.thresholdValue = givenThreshold; }
    public void setPatientPlusCode(String plusCodeUrl) { this.patientPlusCodeURL = plusCodeUrl; }


    public Location getPatientLocation() { return patientLocation; }
    public Carer getTheCarer() { return theCarer; }
    public double getThresholdValue() {return thresholdValue; }
    public String getPatientPlusCode() { return patientPlusCodeURL; }

    public boolean checkInDistress()
    {
        // Stub definition. Runs some code to see if the patient is in distress, potentially changing their
        // isInDistress variable and returns the value or maybe sends a help message
        return isInDistress;
    }

    public void sendHelpMessage()
    {
        HelpMessage newMessage = new HelpMessage();
        newMessage.setSender(this);
        newMessage.setRecipient(theCarer);
      //  newMessage.setSenderLocation(patientLocation);
       // theCarer.receiveMessage(newMessage);
    }

}