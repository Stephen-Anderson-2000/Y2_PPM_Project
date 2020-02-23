package com.example.ppm_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class PatientHomeActivity extends AppCompatActivity {
    private Button whatIsThisButton;
    private Button carerButton;
    private Button helpButton;
    private LocationManager myLocManager;
    private LocationListener myLocListener;

    AccountList theAccounts = new AccountList();
    Patient thisPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //makePatientCarer();

        try
        {
            thisPatient = theAccounts.getPatientByID(1);
        }
        catch (Exception e)
        {
            System.out.println("Account array not working");
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_home);

        //GUI Button initialisation and event listener
        whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);
        carerButton = (Button) findViewById(R.id.patientButton);
        helpButton = (Button) findViewById(R.id.helpButton);

        myLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location myLocation)
            {
                System.out.println("My location is " + myLocation);
                //thisPatient.setLocation(myLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider)
            {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET },
                        10);
            }
        }
        else { fetchLocation(); }



        whatIsThisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientHomeActivity.this, popUp.class));
            }
        });

        carerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCarerInfoActivity();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode){
            case 10:
                fetchLocation();
                break;
            default:
                break;
        }
    }

    private void openCarerInfoActivity(){
        Intent intent = new Intent(this, CarerInfoActivity.class);
        startActivity(intent);
    }

    public void sendHelp(View view)
    {
        // Need to find a way to get the carer and patient into it

        try
        {
            Patient thePatient = theAccounts.getPatientByID(1);
            Carer theCarer = theAccounts.getCarerByID(2);
            Location patientLoc = thePatient.getPatientLocation();


            // Don't run the following without permission as it causes an unhandled exception and the app freezes
            // It is merely for testing purposes
            System.out.println(this.myLocManager.getLastKnownLocation("gps"));
            thePatient.sendHelpMessage();
            System.out.println("Sent");

            System.out.println("The carer received the message from: " + theCarer.getTheReceivedMessage().getSender().getFirstName());

        }
        catch (Exception e)
        {
            System.out.println("Chances are the carer and patient objects can't be found");
        }

    }

    void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        myLocManager.requestLocationUpdates("gps", 10000, 2, myLocListener);
        thisPatient.setPatientLocation(this.myLocManager.getLastKnownLocation("gps"));
    }

}

