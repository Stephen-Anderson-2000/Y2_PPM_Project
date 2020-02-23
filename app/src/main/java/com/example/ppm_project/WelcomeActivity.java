package com.example.ppm_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
   private ToggleButton patientToggle;
   private ToggleButton carerToggle;
   private Button ok;
   private EditText idBox;

   public int enteredUserID = -1;
   public AccountList theAccounts = new AccountList();
   CurrentUserID currentUserID = new CurrentUserID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        makePatientsCarers();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        patientToggle = (ToggleButton) findViewById(R.id.patientToggle);
        carerToggle = (ToggleButton) findViewById(R.id.carerToggle);

        patientToggle.setOnCheckedChangeListener(changeChecker);
        carerToggle.setOnCheckedChangeListener(changeChecker);

        idBox = (EditText)findViewById(R.id.idBox);

        ok = (Button) findViewById(R.id.okButton);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    enteredUserID = Integer.parseInt(idBox.getText().toString());
                }
                catch (Exception e)
                {
                    System.out.println("Couldn't parse the ID box input to an integer");
                }
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
                    //implement pop up to tell user to check if they are a patient or carer and to check their ID
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


    private void makePatientsCarers()
    {
        Patient patient1 = new Patient();
        patient1.setUserID(1);
        patient1.setFirstName("Stephen");

        Carer carer1 = new Carer();
        carer1.setUserID(2);
        carer1.setFirstName("Richard");

        Patient patient2 = new Patient();
        patient2.setUserID(3);
        patient2.setFirstName("Irena");

        Patient patient3 = new Patient();
        patient3.setUserID(4);
        patient3.setFirstName("Sam");

        Carer carer2 = new Carer();
        carer2.setUserID(5);
        carer2.setFirstName("Nathan");

        carer1.addPatient(patient1);
        carer1.addPatient(patient2);
        carer2.addPatient(patient3);

        patient1.setTheCarer(carer1);
        patient2.setTheCarer(carer1);
        patient3.setTheCarer(carer2);

        theAccounts.addCarerToList(carer1);
        theAccounts.addPatientToList(patient1);
        theAccounts.addPatientToList(patient2);
        theAccounts.addCarerToList(carer2);
        theAccounts.addPatientToList(patient3);

        System.out.println(carer1.getPatientArrayList().get(1));
    }

}

