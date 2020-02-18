package com.example.ppm_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CarerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_home);

        //GUI Button initialisation and event listener
        Button whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);

        whatIsThisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarerHomeActivity.this, popUp.class));


            }
        });
    }
}

