package com.example.ppm_project;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;


public class PatientHomeActivity extends AppCompatActivity {
    private Button whatIsThisButton;
    private Button filePicker;
    private Button carerButton;
    private Button helpButton;
    private TextView textPath;
    private LocationManager myLocManager;
    private LocationListener myLocListener;

    private String actualFilePath = "";
    private String TAG = "PatientHomeActivity";
    AccountList theAccounts = new AccountList();
    Patient currentPatient;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CurrentUserID currentUserID = new CurrentUserID();
        currentPatient = theAccounts.getPatientByID(currentUserID.getTheUser());

        System.out.println(currentPatient.getFirstName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_home);

        //GUI Button initialisation and event listener
        whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);
        filePicker = findViewById(R.id.researchDataButton);
        carerButton = (Button) findViewById(R.id.patientButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        textPath = findViewById(R.id.filePath); //TEMP

        filePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 10);
            }
        });

        myLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location myLocation)
            {
                fetchLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider)
            {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                        { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET },
                        10);
            }
            else { fetchLocation(); }
        }
        else { fetchLocation(); }



        whatIsThisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        carerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCarerInfoActivity();

            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHelp();
            }
        });

        alertDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            try
            {
                Uri uri = data.getData();
                File file = new File(uri.getPath());//create path from uri
                final String[] split = file.getPath().split(":");//split the path.
                actualFilePath = split[1];
                ReadCSV csvReader = new ReadCSV();
                csvReader.readFile(this, actualFilePath);
                AccelerationData accDat = csvReader.analyseFile();
                System.out.println("Successfully read");
                if (accDat.isPatientHavingEpisode()){
                    alertDialog.setMessage("PATIENT IS LIKELY HAVING AN EPISODE!");
                    alertDialog.show();
                } else {
                    alertDialog.setMessage("Patient is showing no signs of an episode.");
                    alertDialog.show();
                }
            }
            catch (Exception e) { System.out.println("Failed to read file."); }
        }
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

    public void sendHelp()
    {
        try
        {
            Patient thePatient = theAccounts.getPatientByID(1);
            Carer theCarer = theAccounts.getCarerByID(2);
            try
            {
                thePatient.setPatientLocation(myLocManager.getLastKnownLocation("gps"));
                thePatient.sendHelpMessage();
                alertDialog.setMessage("The carer: " + theCarer.getFirstName() + "\nReceived the message from: " + theCarer.getTheReceivedMessage().getSender().getFirstName() +
                                        "\n\nTheir GPS location is: " + thePatient.getPatientLocation());
                alertDialog.show();
            }
            catch (SecurityException e) { }
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
        myLocManager.requestLocationUpdates("gps", 1000, 0, myLocListener);
        myLocManager.requestLocationUpdates("network", 1000, 0, myLocListener);
        currentPatient.setPatientLocation(this.myLocManager.getLastKnownLocation("gps"));
    }

}

