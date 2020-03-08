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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class WelcomeActivity extends AppCompatActivity {
   private ToggleButton patientToggle;
   private ToggleButton carerToggle;
   private Button ok;
   private EditText nameBox;
   private EditText emailBox;
   private SignInButton signInButton;
   private GoogleSignInClient mGoogleSignInClient;
   private FirebaseAuth auth;
   private DatabaseReference reff;


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
        emailBox = (EditText)findViewById(R.id.userEmailBox);

        ok = (Button) findViewById(R.id.okButton);



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try {
                  if(carerToggle.isChecked() || patientToggle.isChecked()){
                      createAccount();
                  }
                   else{
                       Toast.makeText(getApplicationContext(), "Please select if you are a carer or patient and try again", Toast.LENGTH_SHORT);
                  }
                }
                catch (Exception e)
                {

                }
            }
        });
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
        emailBox.setText(account.getEmail());

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

    private void createAccount(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        reff = FirebaseDatabase.getInstance().getReference().child("users");


        int ID = Integer.parseInt(acct.getId().trim());
        String firstName = nameBox.getText().toString().trim();
        String lastName = acct.getFamilyName().trim();
        String email = emailBox.getText().toString().trim();
        String profileURL = acct.getPhotoUrl().toString().trim();
        boolean isCarer;

        if(carerToggle.isChecked()){
            isCarer = true;
        }
        else{
            isCarer = false;
        }

        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmailAddress(email);
        account.setIsCarer(isCarer);
        account.setProfileURL(profileURL);
        account.setUserID(ID);

        reff.push().setValue(account);




    }



}

