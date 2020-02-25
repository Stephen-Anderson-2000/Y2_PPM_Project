package com.example.ppm_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CarerHomeActivity extends AppCompatActivity {
    public String  actualFilePath="";

    String TAG = "CarerHomeActivity";

    AccountList theAccounts = new AccountList();
    Carer currentCarer;

    TextView textPath;
    Button filePicker;

    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();
    private Boolean fileRead = false;
    private TextView carerNameBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.carer_home);

        textPath = findViewById(R.id.filePath); //TEMP
        carerNameBox = findViewById(R.id.carerNameBox);
        filePicker = findViewById(R.id.researchDataButton);

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

        CurrentUserID currentUserID = new CurrentUserID();
        currentCarer = theAccounts.getCarerByID(currentUserID.getTheUser());

        carerNameBox.setText(currentCarer.getFirstName());

        Thread checkReceivedThread = new Thread(new CheckMessageReceived(currentCarer));
        checkReceivedThread.start();

        if (currentCarer.theReceivedMessage != null) {
            displayMessageResultsDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            Uri uri = data.getData();
            File file = new File(uri.getPath());//create path from uri
            final String[] split = file.getPath().split(":");//split the path.
            actualFilePath = split[1];

            ReadCSV csvReader = new ReadCSV();
            textPath.setText(readFile(actualFilePath));

            AlertDialog alertDialog = new AlertDialog.Builder(CarerHomeActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
        }
    }

    public String readFile(String actualFilePath) {
        StringBuilder allData = new StringBuilder();
        try {
            if (isReadStoragePermissionGranted()) {
                try {
                    String row;
                    BufferedReader csvReader = new BufferedReader(new FileReader(actualFilePath));
                    csvReader.readLine();
                    while ((row = csvReader.readLine()) != null) {
                        String[] csvData = row.split(",");
                        xArray.add(Double.valueOf(csvData[3]));
                        yArray.add(Double.valueOf(csvData[4]));
                        zArray.add(Double.valueOf(csvData[5]));

                        for (int i = 3; i < csvData.length; i++) {
                            allData.append(csvData[i]).append("  ");
                        }
                        allData.append("\n");
                        fileRead = true;
                    }
                } catch (java.io.IOException s) {
                    System.out.println(s.getMessage());
                }
            }
            return allData.toString();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e);
            return "";
        }
    }

    private boolean isReadStoragePermissionGranted() {
        String TAG = "ReadCSV";
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void displayMessageResultsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(CarerHomeActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        try
        {
            HelpMessage theReceivedMessage = currentCarer.getTheReceivedMessage();
            System.out.println("Setting message with Location");
            alertDialog.setMessage("Patient: " + theReceivedMessage.getSender().getFirstName() +
                    " is in distress. They are currently at GPS location: " + theReceivedMessage.getSenderLocation());
            alertDialog.show();
            currentCarer.receiveMessage(null);
            openPatientLocation(theReceivedMessage.getSenderLocation());
        }
        catch (Exception e)
        {
            System.out.println("Caught exception: " + e);
        }

    }

    private void openPatientLocation(Location patientLocation) {
        try
        {
            if (patientLocation != null) {
                URL theURL = new URL("https://plus.codes/api?address=" + patientLocation.getLatitude() + "," + patientLocation.getLongitude());
                new DisplayPatientLocation().execute(theURL);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }


    private class CheckMessageReceived implements Runnable{

        Carer currentCarer;
        private boolean messageIsReceived = false;

        public CheckMessageReceived(Carer theCarer)
        {
            currentCarer = theCarer;
        }

        public void run()
        {
            while (true)
            {
                try
                {
                    HelpMessage receivedMessage = currentCarer.getTheReceivedMessage();
                    if (receivedMessage != null)
                    {
                        System.out.println("Received the help message from " + receivedMessage.getSender().getFirstName());
                        messageIsReceived = true;
                        // display the popup
                        break;
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) { }
            }
        }
    }


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
            }
        }
    }


}

