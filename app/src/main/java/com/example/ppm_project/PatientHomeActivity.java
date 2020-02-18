package com.example.ppm_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PatientHomeActivity extends AppCompatActivity {
private Button whatIsThisButton;
private Button carerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_home);

        //GUI Button initialisation and event listener
        whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);
        carerButton = (Button) findViewById(R.id.carerButton);

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

    private void openCarerInfoActivity(){
        Intent intent = new Intent(this, CarerInfoActivity.class);
        startActivity(intent);
    }
}

