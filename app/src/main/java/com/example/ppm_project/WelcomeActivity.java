package com.example.ppm_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class WelcomeActivity extends AppCompatActivity {
   private ToggleButton patientToggle;
   private ToggleButton carerToggle;
   private Button ok;
   private EditText nameBox;
   private SignInButton signInButton;
   private GoogleSignInClient mGoogleSignInClient;
   private FirebaseAuth auth;


   public int enteredUserID = -1;
   public AccountList theAccounts = new AccountList();
   CurrentUserID currentUserID = new CurrentUserID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // makePatientsCarers();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = (SignInButton)findViewById(R.id.googleSignInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        patientToggle = (ToggleButton) findViewById(R.id.patientToggle);
        carerToggle = (ToggleButton) findViewById(R.id.carerToggle);

        patientToggle.setOnCheckedChangeListener(changeChecker);
        carerToggle.setOnCheckedChangeListener(changeChecker);

        nameBox = (EditText)findViewById(R.id.nameBox);

        ok = (Button) findViewById(R.id.okButton);

  /*        ok.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                handleSignInResult(task);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }


        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            setCredentials(account);
        } catch(ApiException e){

        }
    }

    private void setCredentials(GoogleSignInAccount account){

        nameBox.setText(account.getGivenName());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();


                        } else {

                        }

                        // ...
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


  /*  private void makePatientsCarers()
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

        Patient patient4 = new Patient();
        patient4.setUserID(6);
        patient4.setFirstName("Emerson");

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
        theAccounts.addPatientToList(patient4);

        System.out.println(carer1.getPatientArrayList().get(1));
    } */



}

