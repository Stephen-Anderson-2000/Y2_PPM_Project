package com.example.ppm_project;

import java.util.Vector;

public class Carer extends Account {
    Vector<Patient> patientVector;
    HelpMessage theReceivedMessage;

    public void addPatient(Patient newPatient)
    {
        // Stub definition. Need to add the passed in patient object to the vector
    }

    public void removePatient(Patient oldPatient)
    {
        // Stub definition. Need to loop through to find and remove the passed in patient object
    }

    public void receiveMessage(HelpMessage receivedMessage) { this.theReceivedMessage = receivedMessage; }
}
