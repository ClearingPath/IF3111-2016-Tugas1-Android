package com.candy.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Candy Olivia Mawalim on 25/03/2016.
 */
public class SubmitAnswerActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener {
    private boolean firstRun;
    private Button submitButton;
    private TextView response;
    private String respons = "";
    private String token = "";
    private double latitude;
    private double longitude;
    private String answer = "";
    private String ipAddress = "167.205.34.132";
    private String port = "3111";
    private String log;

    private boolean hasResponse;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_answer);
        firstRun = true;
        hasResponse = false;

        final Spinner spinner = (Spinner) findViewById(R.id.location);
        spinner.setOnItemSelectedListener(this);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        final List<String> locations = new ArrayList<String>(
                Arrays.asList("gku_barat", "gku_timur", "intel", "cc_barat", "cc_timur", "dpr", "oktagon", "perpustakaan", "pau", "kubus"));


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Bundle extras = getIntent().getExtras();
                longitude = extras.getDouble("longitude");
                latitude = extras.getDouble("latitude");
                token = extras.getString("token");
                log = extras.getString("log");


                answer = locations.get(spinner.getSelectedItemPosition());
                JSONObject jsonData = new JSONObject();

                try {
                    jsonData.put("com", "answer");
                    jsonData.put("nim", "13513031");
                    jsonData.put("answer", answer);
                    jsonData.put("longitude",longitude);
                    jsonData.put("latitude",latitude);
                    jsonData.put("token",token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Client myClient = new Client(ipAddress, Integer.parseInt(port), response);
                myClient.execute(jsonData);

                String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(new Date());
                if (log != null) {
                    log += "(Client)" + timeStamp + "   : " + jsonData + "\n\n";
                } else {
                    log = "(Client)" + timeStamp + "   : " + jsonData + "\n\n";
                }

                waitResponse();

            }
        });
    }

    public void waitResponse() {
        if (hasResponse) {

            if (!respons.equals("")) {
                Toast.makeText(SubmitAnswerActivity.this, respons, Toast.LENGTH_LONG).show();
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(new Date());
                if (log != null) {
                    log += "(Server)" + timeStamp + "   : " + respons + "\n\n";
                } else {
                    log = "(Server)" + timeStamp + "   : " + respons + "\n\n";
                }

            } else {
                Toast.makeText(SubmitAnswerActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
            }


            backToMainActivity();
        }
    }

    public void backToMainActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("log",log);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (firstRun) {
            firstRun = false;
        } else {
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }

    class Client extends AsyncTask<JSONObject, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        TextView textResponse;
        JSONObject jsonData;


        Client(String addr, int port, TextView textResponse) {
            dstAddress = addr;
            dstPort = port;
            this.textResponse = textResponse;
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            jsonData = params[0];

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                out.println(jsonData);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                respons = in.readLine();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            hasResponse = true;

        }
    }
}
