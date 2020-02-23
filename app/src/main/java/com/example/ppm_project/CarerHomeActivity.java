package com.example.ppm_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.*;

public class CarerHomeActivity extends AppCompatActivity {
    public String  actualFilePath="";

    AccountList theAccounts = new AccountList();
    Carer currentCarer;

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
                startActivity(new Intent(CarerHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        CurrentUserID currentUserID = new CurrentUserID();
        currentCarer = theAccounts.getCarerByID(currentUserID.getTheUser());
        System.out.println(currentCarer.getFirstName());

        Thread checkReceivedThread = new Thread(new CheckMessageReceived(currentCarer));
        checkReceivedThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            actualFilePath = split[1];
            ReadCSV csvReader = new ReadCSV();
            textPath.setText(csvReader.readFile(this, actualFilePath));

            System.out.println("Successfully read");
        }
    }




}

