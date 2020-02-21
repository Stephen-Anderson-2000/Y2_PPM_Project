package com.example.ppm_project;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ArrayListOfPatients {
    public static ArrayList<Patient> patientsList = new ArrayList<Patient>();
    private static final String fileName = "";

    public void savePatientArrayList()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
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

    public boolean verifyPatientDetails(Patient thePatient)
    {
        boolean detailsValid = false;

        for (Patient aPatient: patientsList)
        {
            if (patientsList.contains(thePatient))
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
