package com.example.ppm_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CarerInfoActivity extends AppCompatActivity {
    Button carerDetailsButton;
    TextView carerNameTitle;
    private DatabaseReference reff;
    private static Carer carer;
    private static Account CurrentAccount = WelcomeActivity.getAccountDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_info);

        carerDetailsButton = findViewById(R.id.carerDetailsButton);
        carerNameTitle = (TextView)findViewById(R.id.carerNameTitle);

        setCarerTitle();

        carerDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForCarerID();
            }
        });
    }

    private void askForCarerID() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your current carers ID Number");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        if(CurrentAccount.getHasCarer()){
            input.setText(carer.getUserID());
        }
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authenticateCarer(input.getText().toString());


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void authenticateCarer(final String UserID) {

        reff = FirebaseDatabase.getInstance().getReference("account").child(UserID);;

        reff.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firebaseIsCarer = dataSnapshot.child("isCarer").getValue().toString();
                    if (firebaseIsCarer == "true") {
                        setCarerDetails(UserID);
                    } else{
                        //else it is not a carer so alert user
                        showNotCarerError();

                    }
                }
                else {
                    //else carer id entered is incorrect or doesnt exist so alert user
                    showCarerIDError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCarerDetails(final String UserID) {

        carer = new Carer();
        reff = FirebaseDatabase.getInstance().getReference("account").child(UserID);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carer.setFirstName(dataSnapshot.child("firstName").getValue().toString());
                carer.setLastName(dataSnapshot.child("lastName").getValue().toString());
                carer.setEmailAddress(dataSnapshot.child("emailAddress").getValue().toString());
                carer.setCloudID(dataSnapshot.child("cloudID").getValue().toString());
                carer.setUserID(UserID);
                CurrentAccount.setHasCarer(true);
                setCarerTitle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static Carer getAccountDetails() {
        return carer;
    }

    private void setCarerTitle(){
        if(CurrentAccount.getHasCarer()) {
            carerNameTitle.setText(carer.getFirstName());
        }
    }

    private void showCarerIDError(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Incorrect Carer ID Number");
        alert.setMessage("It seems that the Carer ID you have entered is incorrect, please try again");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    private void showNotCarerError(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("User is not a carer");
        alert.setMessage("It seems that the User ID you have entered belongs to someone who is not a carer, please try again");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }
}

