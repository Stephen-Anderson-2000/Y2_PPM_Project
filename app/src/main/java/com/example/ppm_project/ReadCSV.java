package com.example.ppm_project;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileReader;

public class ReadCSV extends AppCompatActivity {

    private String TAG = "ReadCSV";
    private Context mContext;

    public String readFile(Context localContext, String actualFilePath) {
        mContext = localContext;
        StringBuilder allData = new StringBuilder();
        if (isReadStoragePermissionGranted()) {
            try {
                String row;
                BufferedReader csvReader = new BufferedReader(new FileReader(actualFilePath));
                while ((row = csvReader.readLine()) != null) {
                    String[] csvData = row.split(",");
                    for(int i = 0; i < csvData.length; i++){
                        allData.append(csvData[i]).append(" ");
                    }
                }
            } catch (java.io.IOException s) {
                System.out.println(s.getMessage());
            }
        }
        return allData.toString();
    }

    private  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

}
