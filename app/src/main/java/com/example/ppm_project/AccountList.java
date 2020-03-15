package com.example.ppm_project;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AccountList {
    private static ArrayList<Carer> carersList = new ArrayList<Carer>();
    private static ArrayList<Patient> patientsList = new ArrayList<Patient>();
    private final String carersFileName = "";
    private final String patientsFileName = "";

    public void main()
    {
        setCarerArrayList();
        setPatientArrayList();
    }

    public void saveCarerArrayList()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(carersFileName));
            // Need to find a way to properly write to a file. Can be hard coded for testing and demonstration purposes
        }
        catch (IOException e)
        {
            Log.e("Exception", "Failed writing patient array to file: " + e.toString());
        }
    }

    public void setCarerArrayList()
    {
        // Stub definition. Need to find a good way to load the data with minimal parsing
    }

    public ArrayList<Carer> getCarersList()
    {
        return carersList;
    }

    public Carer getCarerByID(int accountID)
    {
        for (Carer aCarer: carersList)
        {
            if (Integer.parseInt(aCarer.getUserID())== accountID)
            {
                return aCarer;
            }
        }
        return null;
    }

    public void addCarerToList(Carer newCarer)
    {
        if (!carersList.contains(newCarer))
        {
            carersList.add(newCarer);
        }
    }

    public void removeCarerFromList(Carer oldCarer)
    {
        if (carersList.contains(oldCarer))
        {
            carersList.remove(oldCarer);
        }
    }

    public void savePatientArrayList()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(patientsFileName));
            // Need to find a way to properly write to a file. Can be hard coded for testing and demonstration purposes
        }
        catch (IOException e)
        {
            Log.e("Exception", "Failed writing patient array to file: " + e.toString());
        }
    }

    public void setPatientArrayList()
    {
        // Stub definition. Need to find a good way to load the data with minimal parsing
    }

    public ArrayList<Patient> getPatientsList()
    {
        return patientsList;
    }

    public Patient getPatientByID(int accountID)
    {
        for (Patient aPatient: patientsList)
        {
            if (Integer.parseInt(aPatient.getUserID())== accountID)
            {
                return aPatient;
            }
        }
        return null;
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
