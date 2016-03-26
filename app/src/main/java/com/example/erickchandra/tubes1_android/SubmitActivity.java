package com.example.erickchandra.tubes1_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SubmitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button submitButton;
    Spinner spinnerAnswer;
    String selectedItemString;
    int selectedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        setTitle("Submit Answer");

        spinnerAnswer = (Spinner) findViewById(R.id.spinner_choose);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerAnswer.setAdapter(adapter);
        spinnerAnswer.setOnItemSelectedListener(this);

        // For Submit Button
        submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer();
            }
        });
    }

    public void submitAnswer() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        selectedItemString = parent.getItemAtPosition(position).toString();
        selectedItemPosition = parent.getSelectedItemPosition();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + selectedItemString + "\nPosition: " + selectedItemPosition, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
}
