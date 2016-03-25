package com.luqman.androidmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.util.Log;

public class Answer extends AppCompatActivity {
    public static final String TAG = Answer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Spinner spinner = (Spinner) findViewById(R.id.locations_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void submit(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.locations_spinner);
        String ans = spinner.getSelectedItem().toString();

        Log.d(TAG, "answer is " + ans);


    }
}
