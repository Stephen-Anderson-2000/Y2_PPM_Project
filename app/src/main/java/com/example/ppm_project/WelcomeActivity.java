package com.example.ppm_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
   private ToggleButton patientToggle;
   private ToggleButton carerToggle;
   private Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        patientToggle = (ToggleButton) findViewById(R.id.patientToggle);
        carerToggle = (ToggleButton) findViewById(R.id.carerToggle);

        patientToggle.setOnCheckedChangeListener(changeChecker);
        carerToggle.setOnCheckedChangeListener(changeChecker);

        ok = (Button) findViewById(R.id.okButton);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carerToggle.isChecked())
                {
                    openCarerHomeActivity();
                }
                else if(patientToggle.isChecked())
                {
                    openPatientHomeActivity();
                }
                else
                {
                    //implement pop up to tell user to check if they are a patient or carer
                }
            }
        });
    }

    CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == patientToggle) { carerToggle.setChecked(false); }
            if(buttonView == carerToggle) { patientToggle.setChecked(false); }
        }
    };

    private void openCarerHomeActivity(){
        Intent intent = new Intent(this, CarerHomeActivity.class);
        startActivity(intent);
    }

    private void openPatientHomeActivity(){
        Intent intent = new Intent(this, PatientHomeActivity.class);
        startActivity(intent);
    }

}

