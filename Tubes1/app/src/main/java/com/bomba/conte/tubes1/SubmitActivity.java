package com.bomba.conte.tubes1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 26/03/2016.
 */
public class SubmitActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar titlebar;
    Button submit;
    Spinner answerSpinner;

    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        titlebar = (android.support.v7.widget.Toolbar)findViewById(R.id.titlebar);
        setSupportActionBar(titlebar);
        getSupportActionBar().setTitle("Submit Answer");
        answerSpinner = (Spinner)findViewById(R.id.answer);

        List<String> answers = new ArrayList<String>();
        answers.add("gku_barat");
        answers.add("gku_timur");
        answers.add("intel");
        answers.add("cc_barat");
        answers.add("cc_timur");
        answers.add("dpr");
        answers.add("sunken");
        answers.add("perpustakaan");
        answers.add("pau");
        answers.add("kubus");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, answers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        answerSpinner.setAdapter(dataAdapter);

        submit = (Button)findViewById(R.id.submitButton);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v) {
                String answer = answerSpinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class AnswerSubmitter extends AsyncTask<Void, Void, String> {



        @Override
        protected String doInBackground(Void... arg0) {

            String response = "";
            Socket socket = null;
            PrintWriter printer;
            BufferedReader reader;

            try {
                socket = new Socket(MapsActivity.serverAddress, MapsActivity.serverPort);

                printer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printer.println(obj.toString());
                Log.d("CommLog", obj.toString());
                printer.flush();
                response = reader.readLine();
                reader.close();
                Log.d("CommLog", response);

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

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject resultJSON = null;
            SharedPreferences storage = getSharedPreferences("Maps", Activity.MODE_PRIVATE);
            SharedPreferences.Editor writer = storage.edit();
            try {
                resultJSON = new JSONObject(result);
                writer.putString("Token", resultJSON.getString("token"));
                writer.putLong("Latitude", resultJSON.getLong("latitude"));
                writer.putLong("Longitude", resultJSON.getLong("longitude"));
                writer.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
