package com.example.husni.mapapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;



public class SubmitAnswerActivity extends AppCompatActivity implements OnItemSelectedListener {

    private Spinner locationSpinner;
    private Button submitButton;
    private TextView response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        response = (TextView) findViewById(R.id.responseTextView);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void submitAnswerButton(View view) {
//        Context context = getApplicationContext();
//        String spinnerText = locationSpinner.getSelectedItem().toString();
//        int duration = Toast.LENGTH_LONG;
//
//        Toast toast = Toast.makeText(context, spinnerText, duration);
//        toast.show();
    }


}
