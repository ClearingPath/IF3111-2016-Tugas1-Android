package com.ghazwan.tebakitb;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MainActivity extends AppCompatActivity {

    private EditText nimText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nimText = (EditText) findViewById(R.id.nimEditText);
    }

    /**
     * Send the intent to Maps Activity
     * Starting the challenge
     */
    public void startMapsActivity(View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * Handle the challenge request
     */

    public void challengeReqHandle(View view) {
        String nimInput = nimText.getText().toString();
        if(nimInput.equalsIgnoreCase("")) {
            Toast challengeReqErToast = Toast.makeText(this, "You have to enter your Student ID to start the challenge!", Toast.LENGTH_SHORT);
            challengeReqErToast.show();
        } else if(nimInput.length() != 8) {
            Toast challengeReqErToast = Toast.makeText(this, "Required all 8 digit Student ID to start the challenge!", Toast.LENGTH_SHORT);
            challengeReqErToast.show();
        }
        else {
            SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.app_shared_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("nim", nimInput);
            editor.apply();

            // check the connectivity
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetworkInfo != null &&
                    activeNetworkInfo.isConnectedOrConnecting();
            if(isConnected) {
                // start the challenge
            } else {
                Toast connErToast = Toast.makeText(this, "You have to connected to the internet to start the challenge!", Toast.LENGTH_SHORT);
                connErToast.show();
            }
        }
    }


}

