package com.davidkwan.tubes1android;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SubmitAnswerActivity extends AppCompatActivity {

    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        dropdown = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"gku_barat", "gku_timur", "intel", "cc_barat", "cc_timur", "dpr", "oktagon",
                                        "perpustakaan", "pau", "kubus"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int selectedItemPosition = dropdown.getSelectedItemPosition();
                String answer = (String) dropdown.getItemAtPosition(selectedItemPosition);
                new SubmitAnswer().execute(answer);
            }
        });
    }

    private class SubmitAnswer extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String answer = params[0];
            try {
                Socket socket = new Socket(MapsActivity.ip, MapsActivity.port);
                JSONObject request = new JSONObject();
                request.put("com", "answer");
                request.put("nim", "13513019");
                request.put("answer", answer);
                request.put("longitude", MapsActivity.longitude);
                request.put("latitude", MapsActivity.latitude);
                request.put("token", MapsActivity.token);

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                PrintWriter out = new PrintWriter(outputStream);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                System.out.println("Request sent: " + request.toString());
                out.println(request.toString());
                out.flush();

                String result = in.readLine();
                System.out.println("Response received: " + result);

                inputStream.close();
                outputStream.close();
                socket.close();

                return result;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Add a marker from the received response
            JSONObject jsonResult = null;
            try {
                jsonResult = new JSONObject(result);
                String status = jsonResult.getString("status");
                if(status.equals("ok")) {
                    MapsActivity.latitude = jsonResult.getDouble("latitude");
                    MapsActivity.longitude = jsonResult.getDouble("longitude");
                    MapsActivity.token = jsonResult.getString("token");
                    System.out.println("Correct Answer");

                    Context context = getApplicationContext();
                    CharSequence text = "Correct Answer!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else if (status.equals("wrong_answer")){
                    MapsActivity.token = jsonResult.getString("token");
                    System.out.println("Wrong Answer");

                    Context context = getApplicationContext();
                    CharSequence text = "Wrong Answer!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else if (status.equals("finish")){
                    MapsActivity.token = jsonResult.getString("token");
                    System.out.println("Finish!");

                    Context context = getApplicationContext();
                    CharSequence text = "Finish!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
