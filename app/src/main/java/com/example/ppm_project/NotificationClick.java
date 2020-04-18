package com.example.ppm_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationClick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null)
        {
            String plusCodeURL = getIntent().getStringExtra("plusCodeURL");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(plusCodeURL));
            startActivity(intent);
        }
        
        finish();
    }
}
