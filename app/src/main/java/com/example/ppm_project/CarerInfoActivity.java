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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_info);

        carerDetailsButton = findViewById(R.id.carerDetailsButton);
        carerNameTitle = findViewById(R.id.carerNameTitle);

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
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authenticateCarer(input.getText().toString());
                carerNameTitle.setText(carer.getFirstName());

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

        reff = FirebaseDatabase.getInstance().getReference("account");
        final Query query = reff.orderByChild(UserID).limitToFirst(1);

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(UserID).exists()) {
                    reff = FirebaseDatabase.getInstance().getReference("account").child(UserID);
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String firebaseIsCarer = dataSnapshot.child("isCarer").getValue().toString();
                            if (firebaseIsCarer == "true") {
                                setCarerDetails(UserID);
                            }
                            else {

                                //else it is not a carer so alert user
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCarerDetails(final String UserID) {
        reff = FirebaseDatabase.getInstance().getReference("account").child(UserID);
        carer = new Carer();
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //set details of carer here
                carer.setFirstName(dataSnapshot.child("firstName").getValue().toString());
                carer.setLastName(dataSnapshot.child("lastName").getValue().toString());
                carer.setEmailAddress(dataSnapshot.child("emailAddress").getValue().toString());
                carer.setCloudID(dataSnapshot.child("cloudID").getValue().toString());
                carer.setUserID(UserID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static Carer getAccountDetails() {
        return carer;
    }
}

