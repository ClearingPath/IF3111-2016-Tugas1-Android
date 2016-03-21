package com.example.user.gps;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

public class SubmitMessage extends Activity {
    private Spinner spinner1;
    private Button submitButton;
    private Toolbar tb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Submit Answer");
        setContentView(R.layout.activity_submit_message);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);

    }

    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        submitButton = (Button) findViewById(R.id.btnSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Container.setValue(String.valueOf(spinner1.getSelectedItem()));
                        Toast.makeText(SubmitMessage.this,
                                "OnClickListener : " +
                                        "\nSpinner 1 : " + String.valueOf(spinner1.getSelectedItem()),
                                Toast.LENGTH_SHORT).show();
            }

        });
    }
}
