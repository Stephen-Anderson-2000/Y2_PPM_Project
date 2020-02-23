package com.example.ppm_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
    String TAG = "CarerHomeActivity";
    public String  actualFilePath="";

    TextView textPath;
    Button filePicker;

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
                startActivity(new Intent(CarerHomeActivity.this, popUp.class));


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            actualFilePath = split[1];

            textPath.setText(readCsvFile(actualFilePath));
        }
    }

    protected String readCsvFile(String actualFilePath) {
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

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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

