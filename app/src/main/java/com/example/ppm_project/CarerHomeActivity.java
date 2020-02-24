package com.example.ppm_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;
import java.util.ArrayList;

public class CarerHomeActivity extends AppCompatActivity {
    public String  actualFilePath="";

    AccountList theAccounts = new AccountList();
    Carer currentCarer;

    TextView textPath;
    Button filePicker;

    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();
    private Boolean fileRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_home);

        textPath = findViewById(R.id.filePath); //TEMP
        filePicker = findViewById(R.id.researchDataButton);

        filePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 10);
            }
        });

        //GUI Button initialisation and event listener
        Button whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);

        whatIsThisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarerHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        CurrentUserID currentUserID = new CurrentUserID();
        currentCarer = theAccounts.getCarerByID(currentUserID.getTheUser());

        Thread checkReceivedThread = new Thread(new CheckMessageReceived(currentCarer));
        checkReceivedThread.start();

        if (currentCarer.theReceivedMessage != null) {
            displayMessageResultsDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            actualFilePath = split[1];

            ReadCSV csvReader = new ReadCSV();
            textPath.setText(readFile(actualFilePath));

            AlertDialog alertDialog = new AlertDialog.Builder(CarerHomeActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
    }

    public String readFile(String actualFilePath) {
        StringBuilder allData = new StringBuilder();
        try {
            if (isReadStoragePermissionGranted()) {
                try {
                    String row;
                    BufferedReader csvReader = new BufferedReader(new FileReader(actualFilePath));
                    csvReader.readLine();
                    while ((row = csvReader.readLine()) != null) {
                        String[] csvData = row.split(",");
                        xArray.add(Double.valueOf(csvData[3]));
                        yArray.add(Double.valueOf(csvData[4]));
                        zArray.add(Double.valueOf(csvData[5]));

                        for (int i = 3; i < csvData.length; i++) {
                            allData.append(csvData[i]).append("  ");
                        }
                        allData.append("\n");
                        fileRead = true;
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
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void displayMessageResultsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(CarerHomeActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        try
        {
            System.out.println("Setting message with Location");
            alertDialog.setMessage("Patient: " + currentCarer.theReceivedMessage.getSender().getFirstName() +
                    " is in distress. They are currently at GPS location: " + currentCarer.theReceivedMessage.getSenderLocation());
            alertDialog.show();
            currentCarer.receiveMessage(null);
        }
        catch (Exception e)
        {
            System.out.println("Caught exception: " + e);
        }

    }
}

