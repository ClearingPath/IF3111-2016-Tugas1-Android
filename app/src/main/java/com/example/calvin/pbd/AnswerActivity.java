package com.example.calvin.pbd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class AnswerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.answer_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getSupportActionBar().setTitle("Submit answer");

        ((Button) findViewById(R.id.submitButton)).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();
                    new SubmitAnswer().execute(answer);
                }
            }
        );
    }

    private class SubmitAnswer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Socket socket = new Socket("167.205.34.132", 3111);

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);

                JSONObject request = new JSONObject();
                request.put("com", "answer");
                request.put("nim", "13513077");
                request.put("answer", params[0]);
                request.put("latitude", sp.getLong("latitude", -1));
                request.put("longitude", sp.getLong("longitude", -1));
                request.put("token", sp.getString("token", null));

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                PrintWriter out = new PrintWriter(outputStream);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                out.println(request.toString());
                out.flush();
                String response = in.readLine();
                socket.close();
                Log.d("MyApp", response);
                return response;
            }
            catch (UnknownHostException e) {
                Log.d("MyApp", e.toString());
            }
            catch (IOException e) {
                Log.d("MyApp", e.toString());
            }
            catch (JSONException e) {
                Log.d("MyApp", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject responseJSON = new JSONObject(response);

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                String status = responseJSON.getString("status");

                editor.putString("status", status);
                if (status.equals("ok")) {
                    editor.putLong("latitude", responseJSON.getLong("longitude"));
                    editor.putLong("longitude", responseJSON.getLong("latitude"));
                    editor.putString("token", responseJSON.getString("token"));
                    setResult(MapsActivity.OK);
                }
                else if (status.equals("wrong_answer")) {
                    editor.putString("token", responseJSON.getString("token"));
                    setResult(MapsActivity.WRONG);
                }
                else if (status.equals("finish")) {
                    editor.putString("token", responseJSON.getString("token"));
                    setResult(MapsActivity.FINISH);
                }

                editor.putString("token", responseJSON.getString("token"));
                editor.commit();
                finish();
            }
            catch (JSONException e) {
                Log.d("PostExecute", e.toString());
            }
        }
    }
}
