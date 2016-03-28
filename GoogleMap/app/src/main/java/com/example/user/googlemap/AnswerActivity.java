package com.example.user.googlemap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

    Button buttonSubmit;
    Spinner locationAnswer;
    private String answer;
    Socket socket = null;
    private final String ServerIP = "167.205.34.132";
    private final int ServerPort = 3111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addLocationSpinner();

        locationAnswer = (Spinner)findViewById(R.id.locationItem);
        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                answer = String.valueOf(locationAnswer.getSelectedItem());
                new Submit().execute();
            }
        });
    }

    public void addLocationSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.locationItem);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.LocationItem, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public class Submit extends AsyncTask<Void, Void, String> {
        private String status, token;
        private double latitude, longitude;

        @Override
        protected String doInBackground(Void... args) {

            try {
                socket = new Socket(ServerIP, ServerPort);
                try {
                    Looper.prepare();
                    LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                    LocationListener ll = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    };
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
                    Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                JSONObject json = new JSONObject();
                json.put("answer", answer);
                json.put("com", "answer");
                json.put("nim", "13513005");
                json.put("token", sp.getString("token", null));
                json.put("longitude", longitude);
                json.put("latitude", latitude);

                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                String log = readFromFile();
                writeToFile(log + "\nClient:\n" + json + "\n" + date + "\n");

                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(json.toString());
                out.flush();
                System.err.println(json);

                String res = in.readLine();
                System.err.println(res);
                return res;

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(socket != null){
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
        protected void onPostExecute(String response) {
            try {
                (Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG)).show();

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();

                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                String log = readFromFile();
                writeToFile(log + "\nServer:\n" + response + "\n" + date + "\n");

                JSONObject jsonResponse = new JSONObject(response);
                status = jsonResponse.getString("status");
                if (status.equals("ok")){
                    longitude = jsonResponse.getDouble("longitude");
                    latitude = jsonResponse.getDouble("latitude");
                    edit.putString("longitude", Double.toString(longitude));
                    edit.putString("latitude", Double.toString(latitude));
                    Toast.makeText(getApplicationContext(), "Right Answer", Toast.LENGTH_LONG).show();
                }
                else if (status.equals("wrong_answer")){
                    Toast.makeText(getApplicationContext(), "Wrong Answer", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_LONG).show();
                }
                token = jsonResponse.getString("token");

                edit.putString("token", token);
                edit.commit();
                Intent intent = new Intent();
                intent.putExtra("status", status);
                setResult(RESULT_OK, intent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void writeToFile(String data)
    {
        String path = Environment.getExternalStorageDirectory() + File.separator  + "PBD";
        // Create the folder.
        File folder = new File(path);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, "log.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        String path = Environment.getExternalStorageDirectory() + File.separator  + "PBD";

        //Get the text file
        File file = new File(path,"log.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        ret = text.toString();
        return ret;
    }
}
