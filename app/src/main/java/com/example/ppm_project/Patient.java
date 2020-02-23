package com.example.ppm_project;

import android.location.Location;

public class Patient extends Account {
    private Location patientLocation;
    private Carer theCarer;
    private boolean isInDistress = false;

    public void setPatientLocation(Location patientLocation) { this.patientLocation = patientLocation; }
    public void setTheCarer(Carer newCarer) { this.theCarer = newCarer; }

    public Location getPatientLocation() { return patientLocation; }
    public Carer getTheCarer() { return theCarer; }

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
        newMessage.setSenderLocation(patientLocation);
        newMessage.sendHelpMessage();
    }

}