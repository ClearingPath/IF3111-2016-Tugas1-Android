package com.example.erickchandra.tubes1_android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SubmitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AsyncResponse {
    public AsyncResponse delegate = null;

    String msgRecv;

    Button submitButton;
    Spinner spinnerAnswer;
    String selectedItemString;
    int selectedItemPosition;
    Intent myIntent;
    String intentMsgStr;
    MessageRecvParser intentMsgMSP;
    MessageSendParser msp;
    ClientSync cs;
    String csRecvMsg;

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

        myIntent = getIntent();
        intentMsgStr = myIntent.getStringExtra("Message");
        intentMsgMSP = new MessageRecvParser(intentMsgStr);
    }

    public void submitAnswer() {
        msp = new MessageSendParser("answer", intentMsgMSP.getNIM(), getPlaceCodeStr(), intentMsgMSP.getLat(), intentMsgMSP.getLng(), intentMsgMSP.getToken());
//        cs.SendAndThenRecvMessage();
//        csRecvMsg = cs.getRecvMsg();
        cs = new ClientSync(this, msp.getJSONObjectStr());
        cs.delegate = this;
        cs.execute();
    }

    public String getPlaceCodeStr() {
        switch (selectedItemPosition) {
            case 0: return "gku_barat";
            case 1: return "gku_timur";
            case 2: return "intel";
            case 3: return "cc_barat";
            case 4: return "cc_timur";
            case 5: return "dpr";
            case 6: return "sunken";
            case 7: return "perpustakaan";
            case 8: return "pau";
            case 9: return "kubus";
            default: return "";
        }
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

    @Override
    public void processFinish(String output) {
        msgRecv = output;

        Intent returnIntent = new Intent();
        returnIntent.putExtra("SubmitReplyMessage", msgRecv);
        Log.d(this.getClass().toString(), "SUBMIT ACTIVITY csRecvMsg" + msgRecv);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
