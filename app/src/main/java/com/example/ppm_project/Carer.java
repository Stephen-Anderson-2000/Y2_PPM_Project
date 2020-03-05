package com.example.ppm_project;

import java.util.ArrayList;

public class Carer extends Account {
    ArrayList<Patient> patientArrayList = new ArrayList<>();
    HelpMessage theReceivedMessage;

    public void addPatient(Patient newPatient)
    {
        for (Patient aPatient: patientArrayList)
        {
            if (aPatient.getUserID() == newPatient.getUserID())
            {
                return;
            }
        }
        patientArrayList.add(newPatient);
    }

    public void removePatient(Patient oldPatient)
    {
        for (Patient aPatient: patientArrayList)
        {
            if (aPatient.getUserID() == oldPatient.getUserID())
            {
                patientArrayList.remove(oldPatient);
            }
        }
    }

    public ArrayList<Patient> getPatientArrayList() { return patientArrayList; }

    public void receiveMessage(HelpMessage receivedMessage) {
        this.theReceivedMessage = receivedMessage;
    }

    public HelpMessage getTheReceivedMessage() { return theReceivedMessage; }
}
