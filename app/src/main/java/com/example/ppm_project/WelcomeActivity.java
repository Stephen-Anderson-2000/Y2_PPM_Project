package com.example.ppm_project;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    private static String READ_SMS;
    private static String READ_PHONE_STATE;
    private static String READ_PHONE_NUMBERS;
    private ToggleButton patientToggle;
    private ToggleButton carerToggle;
    private Button ok;
    private EditText nameBox;
    private EditText emailBox;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    private DatabaseReference reff;
    private static Account account;
    private long maxid = 0;


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

        signInButton = (SignInButton) findViewById(R.id.googleSignInButton);
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

        nameBox = (EditText) findViewById(R.id.nameBox);
        emailBox = (EditText) findViewById(R.id.userEmailBox);

        ok = (Button) findViewById(R.id.okButton);

        reff = FirebaseDatabase.getInstance().getReference().child("account");


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (carerToggle.isChecked() || patientToggle.isChecked()) {
                        createAccount();
                        setFMCToken();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select if you are a carer or patient and try again", Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {

                }
            }
        });


    }

    private void setFMCToken() {
        // Get the current FCM Token of this device
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                //
                sendRegistrationToServer(token);

                // Log
                String msg = "FCM Token: " + token;
                Log.d(TAG, msg);
            }
        });
    }

    public static void sendRegistrationToServer(String token) {
        Account CurrentAccount = WelcomeActivity.getAccountDetails();

        CurrentAccount.setCloudID(token);

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("account");
        reff.child(CurrentAccount.getUserID()).setValue(CurrentAccount);
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            final String userID = acct.getId();
            reff = FirebaseDatabase.getInstance().getReference("account");
            final Query query = reff.orderByChild(userID).limitToFirst(1);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(userID).exists()) {
                        String firebaseUserID = dataSnapshot.child(userID).child("userID").getValue().toString();
                        if (firebaseUserID.equals(userID)) {
                            reff = FirebaseDatabase.getInstance().getReference("account").child(userID);
                            reff.addValueEventListener(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String firebaseIsCarer = dataSnapshot.child("isCarer").getValue().toString();
                                    if (firebaseIsCarer == "true") {
                                        setAccountDetails(true, acct);
                                        openCarerHomeActivity();
                                    } else {
                                        setAccountDetails(false, acct);
                                        openPatientHomeActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }else {
                            setCredentials();
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } catch (ApiException e) {

        }
    }


    private void setCredentials() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
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
            if (buttonView == patientToggle) {
                carerToggle.setChecked(false);
            }
            if (buttonView == carerToggle) {
                patientToggle.setChecked(false);
            }
        }
    };

    private void openCarerHomeActivity() {
        Intent intent = new Intent(this, CarerHomeActivity.class);
        startActivity(intent);
    }

    private void openPatientHomeActivity() {
        Intent intent = new Intent(this, PatientHomeActivity.class);
        startActivity(intent);
    }

    private void createAccount() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String ID = acct.getId();

        String firstName = nameBox.getText().toString();
        String lastName = acct.getFamilyName();
        String email = emailBox.getText().toString();
        String profileURL = acct.getPhotoUrl().toString();
        boolean isCarer = true;

        if (carerToggle.isChecked()) {
            isCarer = true;
        } else {
            isCarer = false;
        }

        account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmailAddress(email);
        account.setIsCarer(isCarer);
        account.setProfileURL(profileURL);
        account.setUserID(ID);

        Toast.makeText(getApplicationContext(), account.toString(), Toast.LENGTH_LONG);
        reff.child(account.getUserID()).setValue(account);


        if (isCarer) {
            openCarerHomeActivity();
        } else {
            openPatientHomeActivity();
        }

    }

    static Account getAccountDetails() {
        return account;
    }

    private void setAccountDetails(boolean isCarer, GoogleSignInAccount acct) {
        account = new Account();
        account.setFirstName(acct.getGivenName());
        account.setLastName(acct.getFamilyName());
        account.setIsCarer(isCarer);
        account.setEmailAddress(acct.getEmail());
        account.setUserID(acct.getId());
        account.setCloudID("");
        account.setMobileNumber("");
    }

    //Needed to read mobile number of users phone
    /*
    private String getMobileNumber() {
        String mPhoneNumber = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
                mPhoneNumber = tMgr.getLine1Number();
                return mPhoneNumber;
            }
        else {
                requestPermission();
                return mPhoneNumber;
            }

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager)  this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED  &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=      PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                break;
        }
    } */




    }



