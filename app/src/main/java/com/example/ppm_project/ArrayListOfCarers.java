package com.example.ppm_project;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ArrayListOfCarers {
    public static ArrayList<Patient> carersList = new ArrayList<Patient>();
    private static final String fileName = "";

    public void saveCarerArrayList()
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

    public void loadCarerArrayList()
    {
        // Stub definition. Need to find a good way to load the data with minimal parsing
    }

    public boolean verifyCarerDetails(Patient thePatient)
    {
        boolean detailsValid = false;

        for (Patient aPatient: carersList)
        {
            if (carersList.contains(thePatient))
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
}
