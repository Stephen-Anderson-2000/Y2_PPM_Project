package com.example.ppm_project;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PatientHomeActivity extends AppCompatActivity
{
    private Button whatIsThisButton;
    private Button filePicker;
    private Button carerButton;
    private Button helpButton;
    private Button calibrateButton;
    private TextView userNameBox;
    private LocationManager myLocManager;
    private LocationListener myLocListener;
    private String TAG = "PatientHomeActivity";
    private ArrayList<Double> sArray = new ArrayList<>();
    private ArrayList<Double> xArray = new ArrayList<>();
    private ArrayList<Double> yArray = new ArrayList<>();
    private ArrayList<Double> zArray = new ArrayList<>();
    protected boolean calibrating = false;
    AlertDialog messageAlertDialog;
    AlertDialog gpsAlertDialog;
    AlertDialog loadingFileDialog;
    AlertDialog calibrationDialog;
    private static Account CurrentAccount = WelcomeActivity.getAccountDetails();
    private static Carer CurrentCarer = CarerInfoActivity.getAccountDetails();
    Patient currentPatient;
    public static PatientHomeActivity PatientHomeActivity;
    private String currentCarerToken = "";
    private DatabaseReference reff;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //CurrentUserID currentUserID = new CurrentUserID();
        currentPatient = new Patient(CurrentAccount);

        System.out.println(currentPatient);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_home);

        PatientHomeActivity = this;

        makeButtons();
        setupDialogBoxes();



        myLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location myLocation) { currentPatient.setPatientLocation(fetchLocation()); }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };

        getGPSPermissions();
        checkGPSStatus();
        getFilePermissions();

       // askForCarerName();

    }

    /*
    private void askForCarerName() {
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
                getCarerToken(input.getText().toString());
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

    private void getCarerToken(String carerNumber) {
        reff = FirebaseDatabase.getInstance().getReference("account").child(carerNumber);

        reff.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentCarerToken = dataSnapshot.child("cloudID").getValue().toString();
                Log.i(TAG, "Carers FCM Token: " + currentCarerToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    */

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
            CurrentCarer = CarerInfoActivity.getAccountDetails();
    }

    private void alertCarer() {
        if(CurrentAccount.getHasCarer()) {
            updatePatientLocation();

            final ProgressDialog Dialog = new ProgressDialog(PatientHomeActivity);
            Dialog.setMessage("Sending Help Message...");
            Dialog.setCancelable(false);
            Dialog.show();

            String url = "https://fcm.googleapis.com/fcm/send";
            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyCNIcgHOV7t3I-u9arDqmBSQj34oiiofoo");
            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);

            try {
                JSONObject params = new JSONObject();

                JSONArray registration_ids = new JSONArray();
                registration_ids.put(CurrentCarer.getCloudID());
                //registration_ids.put("cAOkmSJTcHE:APA91bGZBZBtt1Dac62lu8cR_I50oML3AhyRtndKmD_JllANa60ALQHOWDYj4qoUCu8JnxgU7irOo3he3B7oPihpgDsAQEdMTorwXawA48mDmPygNs_oRd16Mrodppjk6pfIXePT9hsD"); //debug testing

                params.put("registration_ids", registration_ids);

                JSONObject notificationObject = new JSONObject();
                notificationObject.put("body", "Patient Needs help!\nTheir location is: " + currentPatient.getPatientPlusCode());
                notificationObject.put("title", "Alert");
                notificationObject.put("click_action", ".Services.NotificationClick");

                params.put("notification", notificationObject);

                StringEntity entity = new StringEntity(params.toString());

                client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                        Dialog.dismiss();
                        Log.i(TAG, responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                        Dialog.dismiss();
                        Log.i(TAG, responseString);
                    }
                });

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("No Carer");
            alert.setMessage("It seems that you haven't set up a carer yet! Click the carer button and enter your carers ID which can be found on their app");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.create().show();
        }
    }

    public void makeButtons()
    {
        //GUI Button initialisation and event listener
        whatIsThisButton = (Button) findViewById(R.id.whatIsThisButton);
        filePicker = findViewById(R.id.researchDataButton);
        carerButton = (Button) findViewById(R.id.patientButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        calibrateButton = (Button) findViewById(R.id.calibrateButton);
        userNameBox = findViewById(R.id.patientNameBox);
        userNameBox.setText(CurrentAccount.getFirstName());

        filePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 10);
            }
        });

        whatIsThisButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientHomeActivity.this, whatIsThisPopUpActivity.class));
            }
        });

        carerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openCarerInfoActivity();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendHelp();
                alertCarer();
            }
        });

        calibrateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("text/*");
                startActivityForResult(fileIntent, 15);
            }
        });
    }

    public void setupDialogBoxes()
    {
        messageAlertDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        messageAlertDialog.setTitle("Alert");
        messageAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        gpsAlertDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        gpsAlertDialog.setTitle("Alert");
        gpsAlertDialog.setMessage("The GPS location on your device is currently disabled. Please enable it" +
                " for full functionality of the app.");
        gpsAlertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                Button okButton = ((AlertDialog) gpsAlertDialog).getButton(AlertDialog.BUTTON_POSITIVE);

                okButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (!myLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                        else { gpsAlertDialog.dismiss(); }
                    }
                });
                Button closeButton = ((AlertDialog) gpsAlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        fetchLocation();
                        gpsAlertDialog.dismiss();
                    }
                });
            }
        });

        gpsAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { }
        });
        gpsAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        loadingFileDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        loadingFileDialog.setTitle("Loading");
        loadingFileDialog.setMessage("Loading the file. Please wait.");
        loadingFileDialog.setCancelable(false);

        calibrationDialog = new AlertDialog.Builder(PatientHomeActivity.this).create();
        calibrationDialog.setTitle("Calibrated");
        calibrationDialog.setButton(calibrationDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == 10) {
            try { new LoadCSVFile().execute(findFilePath(data)); }
            catch (Exception e) { System.out.println("Failed to read file. Caught exception: " + e); }
        }
        if (requestCode == 15)
        {
            try
            {
                calibrating = true;
                new LoadCSVFile().execute(findFilePath(data));
            }
            catch (Exception e) { Log.v(TAG, "Caught exception when loading .csv", e); calibrating = false; }
        }
    }

    private String findFilePath(@Nullable Intent data)
    {
        Uri uri = data.getData();
        File csvFile = new File(uri.getPath());
        csvFile.getAbsolutePath();

        String fullFilePath = csvFile.getAbsolutePath();
        String[] arrFilePath = fullFilePath.split(":");
        return "/" + arrFilePath[1];
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                currentPatient.setPatientLocation(fetchLocation());
                break;
            default:
                break;
        }
    }

    private void openCarerInfoActivity()
    {
        Intent intent = new Intent(this, CarerInfoActivity.class);
        startActivity(intent);
    }
//TODO needs changing to work with new account system: can now get carer details by calling CurrentCarer.getFirstName() etc

    public void updatePatientLocation()
    {
        currentPatient.setPatientLocation(fetchLocation());
        try
        {
            URL gpsURL = new URL("https://plus.codes/api?address=" + currentPatient.getPatientLocation().getLatitude() + "," + currentPatient.getPatientLocation().getLongitude());
            new SetPlusCode().execute(gpsURL);
        }
        catch (java.net.MalformedURLException e) { }
    }

/*    public void sendHelp()
    {
        try
        {
            if (myLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Carer theCarer = currentPatient.getTheCarer();

                currentPatient.setPatientLocation(fetchLocation());
                URL gpsURL = new URL("https://plus.codes/api?address=" + currentPatient.getPatientLocation().getLatitude() + "," + currentPatient.getPatientLocation().getLongitude());
                new SetPlusCode().execute(gpsURL);

                currentPatient.sendHelpMessage();

               // if (currentPatient.getPatientLocation() != null && currentPatient.getPatientPlusCode() != null)
               // {
                //    messageAlertDialog.setMessage("The carer: " + theCarer.getFirstName() + "\nReceived the message from: " + theCarer.getTheReceivedMessage().getSender().getFirstName() +
                        //    "\n\nTheir GPS location is: " + currentPatient.getPatientLocation() + "\n\n Their plus code is: " + currentPatient.getPatientPlusCode().substring(19));
                    // substring start index is 19 to remove the rest of the plus code's url
              //  }
              //  else
              //  {
               //     messageAlertDialog.setMessage("Currently locations are null, will take a few seconds to update. Message sent anyway.");
              //  }
              //  messageAlertDialog.show();
          //  }
         //   else
          //  {
            //    checkGPSStatus();
           // }
        }
        catch (SecurityException e) { Log.e(TAG, "Location permission not granted.", e); }
        catch (MalformedURLException e) {Log.e(TAG, "Error making gps url", e); }
    }
*/

    private Location fetchLocation()
    {
        try
        {
            myLocManager.requestLocationUpdates("gps", 0, 0, myLocListener);
            return this.myLocManager.getLastKnownLocation("gps");
        }
        catch (SecurityException e) { Log.e(TAG, "Location permission not granted.", e); }
        return null;
    }

    private void getGPSPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void checkGPSStatus() { if (!myLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { gpsAlertDialog.show(); } }

    private void getFilePermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private class LoadCSVFile extends AsyncTask<String, Void, String> {
        String TAG = "LoadCSVFiles Class";
        @Override
        protected void onPreExecute() { loadingFileDialog.show(); }

        @Override
        protected String doInBackground(String... theFilePath)
        {
            readFile(theFilePath[0]);
            return theFilePath[0];
        }

        private void readFile(String filePath) {
            //StringBuilder allData = new StringBuilder();
            try
            {
                if (filePath != "" && filePath != null)
                {
                    try
                    {
                        CSVReader reader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory() + filePath));

                        String[] row;

                        reader.readNext();
                        while ((row = reader.readNext()) != null)
                        {
                            String[] csvData = row;//.split(",");

                            sArray.add(Double.valueOf(csvData[2]));
                            xArray.add(Double.valueOf(csvData[3]));
                            yArray.add(Double.valueOf(csvData[4]));
                            zArray.add(Double.valueOf(csvData[5]));
                        }
                    }
                    catch (java.io.IOException s)
                    {
                        System.out.println(s.getMessage());
                    }
                }
            }
            catch (Exception e) { Log.v(TAG, "Caught exception in readFile()", e); }
        }

        @Override
        protected void onPostExecute(String filePath)
        {
            if (filePath != "" && filePath != null && sArray.size() != 0)
            {
                AccelerationData accDat = new AccelerationData();// = analyseFile();
                accDat.setVals(sArray, xArray, yArray, zArray);

                if (calibrating)
                {
                    Calibration calTest = new Calibration();

                    currentPatient.setThresholdValue(calTest.calculateThreshold(sArray, calTest.sortVarArray(calTest.getVarArray(sArray, calTest.calculateMagnitude(xArray, yArray, zArray)))));

                    loadingFileDialog.hide();

                    calibrationDialog.setMessage("Calibration Complete! \nThreshold value: " + currentPatient.getThresholdValue());
                    calibrationDialog.show();
                    calibrating = false;
                }
                else
                {
                    loadingFileDialog.hide();
                    if (accDat.isPatientHavingEpisode(currentPatient.getThresholdValue())){
                        messageAlertDialog.setMessage("PATIENT IS LIKELY HAVING AN EPISODE!");
                        messageAlertDialog.show();
                        //sendHelp();
                    } else {
                        messageAlertDialog.setMessage("Patient is showing no signs of an episode.");
                        messageAlertDialog.show();
                    }
                }
                sArray.clear();
                xArray.clear();
                yArray.clear();
                zArray.clear();
            }
            try
            {
                currentPatient.setPatientLocation(fetchLocation());
                URL gpsURL = new URL("https://plus.codes/api?address=" + currentPatient.getPatientLocation().getLatitude() + "," + currentPatient.getPatientLocation().getLongitude());
                new SetPlusCode().execute(gpsURL);
                System.out.println(currentPatient.getPatientPlusCode());
            }
            catch (Exception e) { }
        }
    }

    private class SetPlusCode extends AsyncTask<URL, Void, String> {

        String TAG = "PlusCodeClass";

        @Override
        protected String doInBackground(URL... theURL) {
            try {
                JSONObject plusCodeJSON = getPlusCodeObject(theURL[0]);
                String urlPlusCode = "https://plus.codes/" + plusCodeJSON.getJSONObject("plus_code").getString("global_code");
                return urlPlusCode;
            } catch (Exception ex) {
                Log.e(TAG, "Found exception when parsing JSON", ex);
            }
            return null;
        }

        private JSONObject getPlusCodeObject(URL theURL) {
            try {
                InputStream inStream = theURL.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("UTF-8")));
                String rawJSONString = readStream(reader);
                return new JSONObject(rawJSONString);
            } catch (Exception ex) {
                Log.e(TAG, "Found exception when fetching JSON", ex);
                return null;
            }
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
            currentPatient.setPatientPlusCode(urlPlusCode);
        }
    }

}

