package com.mfikria.itblocator;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Application;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerActivity extends Activity implements AsyncResponse{

    Client cl;
    JSONObject response;
    JSONObject request;
    Button buttonSubmit;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        if(!bundle.isEmpty()) {
            String receivedString = bundle.getString("request");
            try {
                request = new JSONObject(receivedString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
//        cl = new Client();
//        cl.delegate = this;
        buttonSubmit  = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner mySpinner=(Spinner) findViewById(R.id.spinner);
                String locName = mySpinner.getSelectedItem().toString();

                try {
                    request.put("answer", locName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                cl = new Client();
                cl.delegate = AnswerActivity.this;
                cl.execute(request.toString());
            }
        });
    }

    @Override
    public void processFinish(String output) {

        intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("response", output);
        intent.putExtras(bundle);

        setResult(RESULT_OK,intent);
        finish();
    }
}
