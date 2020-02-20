package com.example.ppm_project;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;

public class Patient extends Account {
    private Location patientLocation;
    private Carer theCarer;
    private boolean isInDistress = false;

    public void setPatientLocation(Location patientLocation) { this.patientLocation = patientLocation; }
    public void setTheCarer(Carer theCarer) { this.theCarer = theCarer; }

    public Location getPatientLocation() { return patientLocation; }
    public Carer getTheCarer() { return theCarer; }

    public boolean checkInDistress()
    {
        // Stub definition. Runs some code to see if the patient is in distress, potentially changing their
        // isInDistress variable and returns the value or maybe sends a help message
        return isInDistress;
    }

    public HelpMessage sendHelpMessage()
    {
        HelpMessage newMessage = new HelpMessage();
        newMessage.setSender(this);
        newMessage.setRecipient(theCarer);
        newMessage.setSenderLocation(patientLocation);

        return newMessage;
    }
}