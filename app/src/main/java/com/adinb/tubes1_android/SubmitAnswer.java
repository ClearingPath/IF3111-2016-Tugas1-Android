package com.adinb.tubes1_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SubmitAnswer extends AppCompatActivity {

    final String address = "167.205.34.132";
    final int port = 3111;

    JSONObject json;
    JSONObject oldJson;
    String jsonRequest = "";
    String response = "";
    Button submitButton;
    Spinner locationSpinner;

    public class RequestTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket (address,port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(jsonRequest); // Send request
                response = br.readLine(); // Receive response

            }
            catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d("HOST", "UHOST");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IO", "IOException:" + e.toString());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast toast = Toast.makeText(getApplicationContext(), response,Toast.LENGTH_LONG);
            toast.show();
            Intent returnIntent = new Intent();

            returnIntent.putExtra("json", response);
            setResult(MainActivity.FINISH, returnIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("json", "{\"status\":\"back\"}");
        setResult(MainActivity.FINISH, mIntent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);
        setTitle(R.string.title_activity_submit_answer);

        // Set Spinner
        locationSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_array, android.R.layout.simple_spinner_item);
        final ArrayAdapter<CharSequence> codeadapter = ArrayAdapter.createFromResource(this, R.array.location_code_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        // Get intent
        Log.d("INTENT", getIntent().getExtras().getString(Intent.EXTRA_TEXT));
        try {
            oldJson = new JSONObject(getIntent().getExtras().getString(Intent.EXTRA_TEXT));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get button
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                json = new JSONObject();
                try {
                    json.put("com", "answer");
                    json.put("nim", "13513058");
                    json.put("answer", codeadapter.getItem(locationSpinner.getSelectedItemPosition()).toString());
                    // TODO terbalik
                    json.put("longitude", oldJson.getDouble("latitude"));
                    json.put("latitude", oldJson.getDouble("longitude"));
                    json.put("token", oldJson.getString("token"));
                    jsonRequest = json.toString();
                    Log.d("JSONTOSTRING", jsonRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SubmitAnswer.RequestTask().execute();
            }
        });
    }

}
