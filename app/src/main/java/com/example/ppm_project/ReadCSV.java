package com.example.ppm_project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadCSV extends AppCompatActivity {

    private Context mContext;
    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();
    private Boolean fileRead = false;

    public String readFile(Context localContext, String actualFilePath) {
        mContext = localContext;
        StringBuilder allData = new StringBuilder();
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

                    for(int i = 3; i < csvData.length; i++){
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
    }

    private boolean isReadStoragePermissionGranted() {
        String TAG = "ReadCSV";
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE)
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

    public AccelerationData analyseFile() {
        if (fileRead){
            AccelerationData accDat = new AccelerationData();
            accDat.setVals(xArray, yArray, zArray);
            return accDat;
        } else {
            System.out.println("ERROR: Read file before analyzing it");
        }
        return null;
    }

}
