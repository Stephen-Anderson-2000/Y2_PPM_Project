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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CarerHomeActivity extends AppCompatActivity {
    AlertDialog messageAlertDialog;

    String TAG = "CarerHomeActivity";
Carer currentCarer;
    AccountList theAccounts = new AccountList();

    private TextView carerNameBox;
    private Button myIDButton;
    private static Account CurrentAccount = WelcomeActivity.getAccountDetails();

    private boolean messageIsReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_home);

        carerNameBox = findViewById(R.id.carerNameBox);
        filePicker = findViewById(R.id.researchDataButton);
        myIDButton = (Button)findViewById(R.id.myIDButton);

        filePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 10);
            }
        });
        //GUI Button initialisation and event listener
        Button whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);

        whatIsThisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarerHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        myIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIDDialog();
            }
        });

        carerNameBox.setText(CurrentAccount.getFirstName());

   /*     Thread checkReceivedThread = new Thread(new CheckMessageReceived(currentCarer));
        checkReceivedThread.start();

        if (currentCarer.theReceivedMessage != null) {
            displayMessageResultsDialog();
        }
*/

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
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

//    private void displayMessageResultsDialog() {
//        AlertDialog alertDialog = new AlertDialog.Builder(CarerHomeActivity.this).create();
//        alertDialog.setTitle("Alert");
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        try
//        {
//           HelpMessage theReceivedMessage = currentCarer.getTheReceivedMessage();
//            System.out.println("Setting message with Location");
//            alertDialog.setMessage("Patient: " + theReceivedMessage.getSender().getFirstName() +
//                    " is in distress. They are currently at GPS location: " + theReceivedMessage.getSenderLocation());
//            alertDialog.show();
//            currentCarer.receiveMessage(null);
//            openPatientLocation(theReceivedMessage.getSenderLocation());
//        }
//        catch (Exception e)
//        {
//            System.out.println("Caught exception: " + e);
//        }
//
//    }
//
//    private void openPatientLocation(Location patientLocation) {
//        try
//        {
//            if (patientLocation != null) {
//                URL theURL = new URL("https://plus.codes/api?address=" + patientLocation.getLatitude() + "," + patientLocation.getLongitude());
//                new DisplayPatientLocation().execute(theURL);
//            }
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex);
//        }
//    }
//
//
//    private class CheckMessageReceived implements Runnable{
//
//        Carer currentCarer;
//        private boolean messageIsReceived = false;
//
//        public CheckMessageReceived(Carer theCarer)
//        {
//            currentCarer = theCarer;
//        }
//
//        public void run()
//        {
//            while (true)
//            {
//                try
//                {
//                    HelpMessage receivedMessage = currentCarer.getTheReceivedMessage();
//                    if (receivedMessage != null)
//                    {
//                        System.out.println("Received the help message from " + receivedMessage.getSender().getFirstName());
//                        messageIsReceived = true;
//                        // display the popup
//                        break;
//                    }
//                    Thread.sleep(1000);
//                }
//                catch (InterruptedException e) { }
//            }
//        }
//    }


    private class DisplayPatientLocation extends AsyncTask<URL, Void, String> {

        String TAG = "PlusCodeClass";

        @Override
        protected String doInBackground(URL... theURL)
        {
            try {
                JSONObject plusCodeJSON = getPlusCodeObject(theURL[0]);
                String urlPlusCode = "https://plus.codes/" + plusCodeJSON.getJSONObject("plus_code").getString("global_code");
                return urlPlusCode;
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Found exception when parsing JSON", ex);
            }
            return null;
        }

        private JSONObject getPlusCodeObject(URL theURL)
        {
            try
            {
                InputStream inStream = theURL.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("UTF-8")));
                String rawJSONString = readStream(reader);
                return new JSONObject(rawJSONString);
            }
            catch (Exception ex) { Log.e(TAG, "Found exception when fetching JSON", ex); return null; }
        }


        private String readStream(Reader rd) throws IOException {

            StringBuilder strBuilder = new StringBuilder();
            int data;
            while ((data = rd.read()) != -1) {
                strBuilder.append((char) data);
            }
            return strBuilder.toString();
        }

        @Override
        protected void onPostExecute(String urlPlusCode) {
            System.out.println(urlPlusCode);
            Uri gmmIntentUri = Uri.parse(urlPlusCode);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
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

    private void showIDDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Your User ID");
        alert.setMessage(CurrentAccount.getUserID());
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

}

