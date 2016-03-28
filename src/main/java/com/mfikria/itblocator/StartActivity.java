package com.mfikria.itblocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class StartActivity extends Activity implements AsyncResponse{
    private static final String SERVER_ADDRESS = "";
    private static final int SERVER_PORT = 8800;
    private String test;
    Button buttonStart;
    TextView viewTest;
    Client cl;
    String response;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        buttonStart  = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl = new Client();
                cl.delegate = StartActivity.this;
                cl.execute("{\"com\":\"req_loc\",\"nim\":\"13513009\"}");

            }
        });
    }

    @Override
    public void processFinish(String output) {
        response = output;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
