package com.example.ppm_project;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AccountList {
    public static ArrayList<Patient> carersList = new ArrayList<Patient>();
    public static ArrayList<Patient> patientsList = new ArrayList<Patient>();
    private final String carersFileName = "";
    private final String patientsFileName = "";

    public void main()
    {
        loadCarerArrayList();
        loadPatientArrayList();
    }

    public void saveCarerArrayList()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(carersFileName));
            // Need to find a way to properly write to a file. Can be hard coded for testing purposes
        }
        catch (IOException e)
        {
            Log.e("Exception", "Failed writing patient array to file: " + e.toString());
        }
    }

    public void loadCarerArrayList()
    {
        // Stub definition. Need to find a good way to load the data with minimal parsing
    }

    public boolean verifyCarerDetails(int thePatientID)
    {
        boolean detailsValid = false;

        for (Patient aPatient: carersList)
        {
            if (aPatient.getUserID() == thePatientID)
            {
                detailsValid = true;
            }
        }
        return detailsValid;
    }

    public void addCarerToList(Patient newPatient)
    {
        if (!carersList.contains(newPatient))
        {
            carersList.add(newPatient);
        }
    }

    public void removeCarerFromList(Patient oldPatient)
    {
        if (carersList.contains(oldPatient))
        {
            carersList.remove(oldPatient);
        }
    }

    public void savePatientArrayList()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(patientsFileName));
            // Need to find a way to properly write to a file. Can be hard coded for testing purposes
        }
        catch (IOException e)
        {
            Log.e("Exception", "Failed writing patient array to file: " + e.toString());
        }
    }

    public void loadPatientArrayList()
    {
        // Stub definition. Need to find a good way to load the data with minimal parsing
    }

    public boolean verifyPatientDetails(int thePatientID)
    {
        boolean detailsValid = false;

        for (Patient aPatient: patientsList)
        {
            if (aPatient.getUserID() == thePatientID)
            {
                detailsValid = true;
            }
        }
        return detailsValid;
    }

    public void addPatientToList(Patient newPatient)
    {
        if (!patientsList.contains(newPatient))
        {
            patientsList.add(newPatient);
        }
    }

    public void removePatientFromList(Patient oldPatient)
    {
        if (patientsList.contains(oldPatient))
        {
            patientsList.remove(oldPatient);
        }
    }
}
