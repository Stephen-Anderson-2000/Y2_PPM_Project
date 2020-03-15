package com.example.ppm_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CarerHomeActivity extends AppCompatActivity {
    AlertDialog messageAlertDialog;

    String TAG = "CarerHomeActivity";

    AccountList theAccounts = new AccountList();
    Carer currentCarer;

    private TextView carerNameBox;

    private boolean messageIsReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_home);

        carerNameBox = findViewById(R.id.carerNameBox);

        //GUI Button initialisation and event listener
        Button whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);

        whatIsThisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarerHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        CurrentUserID currentUserID = new CurrentUserID();
        currentCarer = theAccounts.getCarerByID(currentUserID.getTheUser());

        carerNameBox.setText(currentCarer.getFirstName());

        checkMessageReceived();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {

        }
    }

    private void displayMessageResultsDialog(final HelpMessage receivedMessage) {
        messageAlertDialog = new AlertDialog.Builder(CarerHomeActivity.this).create();
        messageAlertDialog.setTitle("Message For Help Received!");
        messageAlertDialog.setMessage("Patient: " + receivedMessage.getSender().getFirstName() + " " + receivedMessage.getSender().getLastName() +
                " is in distress! \nThey are currently at plus code: " + receivedMessage.getSenderLocation().substring(19) + "\n\n\nOpen their location on Google Maps?");
        messageAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openPatientLocation(receivedMessage.getSenderLocation());
                dialog.dismiss();
                messageIsReceived = false;
            }
        });
        messageAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                messageIsReceived = false;
            }
        });

    }

    private void openPatientLocation(String patientPlusCodeUrl) {
        try
        {
            if (patientPlusCodeUrl != null) {
                //URL theURL = new URL("https://plus.codes/api?address=" + patientLocation.getLatitude() + "," + patientLocation.getLongitude());
                //new DisplayPatientLocation().execute(patientPlusCodeUrl);
                Uri gmmIntentUri = Uri.parse(patientPlusCodeUrl);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void checkMessageReceived() {

        new Thread()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        HelpMessage receivedMessage = currentCarer.getTheReceivedMessage();
                        if (receivedMessage != null && !messageIsReceived)
                        {
                            System.out.println("Received the help message from " + receivedMessage.getSender().getFirstName());
                            messageIsReceived = true;
                            try
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displayMessageResultsDialog(currentCarer.getTheReceivedMessage());
                                        messageAlertDialog.show();
                                    }
                                });
                            }
                            catch (Exception e) { Log.e(TAG, "Caught exception when trying to show message alert dialog", e); }
                            break;
                        }
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e) { }
                }
            }
        }.start();
    }

}

