package com.example.ppm_project;

import java.util.Vector;

public class Carer extends Account {
    Vector<Patient> patientVector;
    HelpMessage theReceivedMessage;

    public void addPatient(Patient newPatient)
    {
        for (Patient aPatient: patientVector)
        {
            if (aPatient.getUserID() == newPatient.getUserID())
            {
                return;
            }
        }
        patientVector.add(newPatient);
    }

    public void removePatient(Patient oldPatient)
    {
        // Stub definition. Need to loop through to find and remove the passed in patient object
    }

    public void receiveMessage(HelpMessage receivedMessage) {
        this.theReceivedMessage = receivedMessage;
        System.out.println("Received");
    }

    public HelpMessage getTheReceivedMessage() { return theReceivedMessage; }
}
