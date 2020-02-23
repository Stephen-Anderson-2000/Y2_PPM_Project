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

   public int enteredUserID;
   public AccountList theAccounts = new AccountList();
   CurrentUserID currentUserID = new CurrentUserID();

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
                if(carerToggle.isChecked() && theAccounts.getCarerByID(enteredUserID) != null)
                {
                    currentUserID.setTheUser(enteredUserID);
                    openCarerHomeActivity();
                }
                else if(patientToggle.isChecked() && theAccounts.getPatientByID(enteredUserID) != null)
                {
                    currentUserID.setTheUser(enteredUserID);
                    openPatientHomeActivity();
                }
                else
                {
                    //implement pop up to tell user to check if they are a patient or carer
                    //and to check their ID
                }
            }
        });
        makePatientCarer();
    }

    CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == patientToggle) { carerToggle.setChecked(false); enteredUserID = 1; }
            if(buttonView == carerToggle) { patientToggle.setChecked(false); enteredUserID = 2; }
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


    private void makePatientCarer()
    {
        Patient tempPatient = new Patient();
        tempPatient.setUserID(1);
        tempPatient.setFirstName("Stephen");

        Carer tempCarer = new Carer();
        tempCarer.setUserID(2);
        tempCarer.setFirstName("Sam");
        tempCarer.addPatient(tempPatient);

        tempPatient.setTheCarer(tempCarer);

        theAccounts.addCarerToList(tempCarer);
        theAccounts.addPatientToList(tempPatient);
    }

}

