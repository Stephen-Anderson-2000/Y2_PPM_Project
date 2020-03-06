package com.example.ppm_project;

import com.opencsv.CSVReader;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class PatientHomeActivity extends AppCompatActivity
{
    private Button whatIsThisButton;
    private Button filePicker;
    private Button carerButton;
    private Button helpButton;
    private Button calibrateButton;
    private TextView userNameBox;
    private LocationManager myLocManager;
    private LocationListener myLocListener;
    private String TAG = "PatientHomeActivity";
    private ArrayList<Double> sArray = new ArrayList<>();
    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();
    private double thresholdValue;
    protected String filePath = "";
    AlertDialog messageAlertDialog;
    AlertDialog gpsAlertDialog;
    AlertDialog calibrationDialog;
    AccountList theAccounts = new AccountList();
    Patient currentPatient;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        CurrentUserID currentUserID = new CurrentUserID();
        currentPatient = theAccounts.getPatientByID(currentUserID.getTheUser());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_home);

        makeButtons();
        setupDialogBoxes();

        myLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location myLocation) { currentPatient.setPatientLocation(fetchLocation()); }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };

        checkGPSPermissions();
        checkGPSStatus();

    }

    public void makeButtons()
    {
        //GUI Button initialisation and event listener
        whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);
        filePicker = findViewById(R.id.researchDataButton);
        carerButton = (Button) findViewById(R.id.patientButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        calibrateButton = (Button) findViewById(R.id.calibrateButton);
        userNameBox = findViewById(R.id.patientNameBox);
        userNameBox.setText(currentPatient.getFirstName());

        filePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 10);
            }
        });

        whatIsThisButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        carerButton.setOnClickListener(new View.OnClickListener()
        {
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

        calibrateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 15);
            }
        });
    }

    public void setupDialogBoxes()
    {
        messageAlertDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        messageAlertDialog.setTitle("Alert");
        messageAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        gpsAlertDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        gpsAlertDialog.setTitle("Alert");
        gpsAlertDialog.setMessage("The GPS location on your device is currently disabled. Please enable it" +
                " for full functionality of the app.");
        gpsAlertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                Button okButton = ((AlertDialog) gpsAlertDialog).getButton(AlertDialog.BUTTON_POSITIVE);

                okButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (!myLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                        else { gpsAlertDialog.dismiss(); }
                    }
                });
                Button closeButton = ((AlertDialog) gpsAlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        fetchLocation();
                        gpsAlertDialog.dismiss();
                    }
                });
            }
        });

        gpsAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { }
        });
        gpsAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });


        calibrationDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        calibrationDialog.setTitle("Calibrating from file");
        calibrationDialog.setMessage("Please wait for the calibration to finish");
        calibrationDialog.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == 10) {
            try
            {
                Uri uri = data.getData();
                File file = new File(uri.getPath());//create path from uri
                final String[] split = file.getPath().split(":");//split the path.
                String actualFilePath = split[1];

                AccelerationData accDat = new AccelerationData();// = analyseFile();
                //readFile(actualFilePath);
                accDat.setVals(sArray, xArray, yArray, zArray);

                if (accDat.isPatientHavingEpisode(currentPatient.getThresholdValue())){
                    messageAlertDialog.setMessage("PATIENT IS LIKELY HAVING AN EPISODE!");
                    messageAlertDialog.show();
                    sendHelp();
                } else {
                    messageAlertDialog.setMessage("Patient is showing no signs of an episode.");
                    messageAlertDialog.show();
                }
            }
            catch (Exception e) { System.out.println("Failed to read file. Caught exception: " + e); }
        }
        if (requestCode == 15)
        {
            try
            {
                Uri uri = data.getData();
                File csvFile = new File(uri.getPath());
                csvFile.getAbsolutePath();

                String fullFilePath = csvFile.getAbsolutePath();
                String[] arrFilePath = fullFilePath.split(":");
                filePath = "/" + arrFilePath[1];

                System.out.println("Absolute path: " + csvFile.getAbsolutePath());
                System.out.println("Filepath: " + arrFilePath[1]);

                Intent returnIntent = getIntent();
                returnIntent.putExtra("Result", 1);
                setResult(Activity.RESULT_OK);
            }
            catch (Exception e) { Log.v(TAG, "Caught exception when loading .csv", e); }
            }
        if (resultCode == RESULT_OK)
        {
            System.out.println("On Activity Result. Result code: " + resultCode);
            Thread calibrationThread = new Thread(new LoadCSVFiles(filePath));
            calibrationThread.start();
            calibrationDialog.show();

            boolean threadAlive = true;
            while(threadAlive)
            {
                if (calibrationThread.getState() == Thread.State.TERMINATED)
                {
                    threadAlive = false;
                    calibrationDialog.dismiss();
                }
            }
            AccelerationData accDat = new AccelerationData();// = analyseFile();

            accDat.setVals(sArray, xArray, yArray, zArray);
            Calibration calTest = new Calibration();

            currentPatient.setThresholdValue(calTest.calculateThreshold(sArray, calTest.getVarArray(sArray, calTest.calculateMagnitude(xArray, yArray, zArray))));
            System.out.println(currentPatient.getThresholdValue());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 10:
                currentPatient.setPatientLocation(fetchLocation());
                break;
            default:
                break;
        }
    }

    private void openCarerInfoActivity()
    {
        Intent intent = new Intent(this, CarerInfoActivity.class);
        startActivity(intent);
    }

    public void sendHelp()
    {
        try
        {
            if (myLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Carer theCarer = currentPatient.getTheCarer();
                currentPatient.setPatientLocation(fetchLocation());
                System.out.println(currentPatient.getPatientLocation());
                currentPatient.sendHelpMessage();
                messageAlertDialog.setMessage("The carer: " + theCarer.getFirstName() + "\nReceived the message from: " + theCarer.getTheReceivedMessage().getSender().getFirstName() +
                        "\n\nTheir GPS location is: " + currentPatient.getPatientLocation());
                messageAlertDialog.show();
            }
            else
            {
                checkGPSStatus();
            }
        }
        catch (SecurityException e) { Log.e(TAG, "Location permission not granted.", e); }
    }

    private Location fetchLocation()
    {
        try
        {
            myLocManager.requestLocationUpdates("gps", 0, 0, myLocListener);
            return this.myLocManager.getLastKnownLocation("gps");
        }
        catch (SecurityException e) { Log.e(TAG, "Location permission not granted.", e); }
        return null;
    }

    public String readFile(String fileName) {
        StringBuilder allData = new StringBuilder();
        try {
            if (isReadStoragePermissionGranted()) {
                try {
                    //File csvfile = new File(filePath);
                    File csvfile = new File(Environment.getExternalStorageDirectory() + "/Carer App CSV Files/Calibrating.csv");
                    System.out.println("Found file at " + csvfile.getAbsolutePath());
                    CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                    //CSVReader reader = new CSVReader(new FileReader(filePath));
                    System.out.println("CSVReader created");
                    String[] row;
                    //BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
                    //csvReader.readLine();
                    reader.readNext();
                    while ((row = reader.readNext()) != null) {
                        String[] csvData = row;//.split(",");

                        sArray.add(Double.valueOf(csvData[2]));
                        xArray.add(Double.valueOf(csvData[3]));
                        yArray.add(Double.valueOf(csvData[4]));
                        zArray.add(Double.valueOf(csvData[5]));

                        for (int i = 2; i < csvData.length; i++) {
                            allData.append(csvData[i]).append("  ");
                        }
                        allData.append("\n");
                        //fileRead = true;
                    }

                } catch (java.io.IOException s) {
                    System.out.println(s.getMessage());
                }
            }
            return allData.toString();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
            return "";
        }
    }

    private boolean isReadStoragePermissionGranted() {
        String TAG = "ReadCSV";
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Log.v(TAG,"Read permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        //permission is automatically granted on sdk < 23 upon installation
        Log.v(TAG,"Read permission is granted");
        return true;
    }

    private void checkGPSPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void checkGPSStatus() { if (!myLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { gpsAlertDialog.show(); } }


    private class LoadCSVFiles extends Thread {

        public LoadCSVFiles(String _filePath) { filePath = _filePath;; }

        public void run()
        {
            readFile();
        }

        public String readFile() {
            StringBuilder allData = new StringBuilder();
            try {
                if (isReadStoragePermissionGranted() && filePath != "") {
                    try {
                        //File csvfile = new File(filePath);
                        File csvfile = new File(Environment.getExternalStorageDirectory() + filePath);
                        CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                        //CSVReader reader = new CSVReader(new FileReader(filePath));
                        String[] row;
                        //BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
                        //csvReader.readLine();
                        reader.readNext();
                        while ((row = reader.readNext()) != null) {
                            String[] csvData = row;//.split(",");

                            sArray.add(Double.valueOf(csvData[2]));
                            xArray.add(Double.valueOf(csvData[3]));
                            yArray.add(Double.valueOf(csvData[4]));
                            zArray.add(Double.valueOf(csvData[5]));

                            for (int i = 2; i < csvData.length; i++) {
                                allData.append(csvData[i]).append("  ");
                            }
                            allData.append("\n");
                        }

                    } catch (java.io.IOException s) {
                        System.out.println(s.getMessage());
                    }
                }
                return allData.toString();
            } catch (Exception e) {
                System.out.println("Caught exception: " + e);
                return "";
            }
        }
    }

}



