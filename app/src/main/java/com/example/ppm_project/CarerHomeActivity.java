package com.example.ppm_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CarerHomeActivity extends AppCompatActivity {

    String TAG = "CarerHomeActivity";

    private TextView carerNameBox;
    private Button myIDButton;
    private static Account CurrentAccount = WelcomeActivity.getAccountDetails();
    private Button filePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_home);

        carerNameBox = findViewById(R.id.carerNameBox);
        filePicker = findViewById(R.id.researchDataButton);
        myIDButton = (Button)findViewById(R.id.myIDButton);

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

        myIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIDDialog();
            }
        });

        carerNameBox.setText(CurrentAccount.getFirstName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {

        }
    }

    private void showIDDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Your User ID");
        alert.setMessage(CurrentAccount.getUserID());
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

}

