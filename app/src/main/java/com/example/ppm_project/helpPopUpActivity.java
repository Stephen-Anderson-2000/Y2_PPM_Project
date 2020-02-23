package com.example.ppm_project;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

public class helpPopUpActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_message_pop_up);

        //getting width and height based on resolution of device
        DisplayMetrics dm =  new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width =  dm.widthPixels;
        int height = dm.heightPixels;

        //setting pop up window size to a percentage of the screen resolution
        double widthMultiplier = 0.8;
        double heightMultiplier = 0.6;
        getWindow().setLayout((int)(width*widthMultiplier),(int)(height*heightMultiplier));
    }
}
