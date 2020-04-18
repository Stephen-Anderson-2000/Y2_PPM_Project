package com.example.ppm_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationClick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.codes/7GXHX4HM+3C"));
        startActivity(intent);

        finish();
    }
}
