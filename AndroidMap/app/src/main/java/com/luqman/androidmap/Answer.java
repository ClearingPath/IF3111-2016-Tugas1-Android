package com.luqman.androidmap;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.util.Log;
import org.json.simple.JSONObject;

public class Answer extends AppCompatActivity implements AsyncResponse {
    public static final String TAG = Answer.class.getSimpleName();

    private double longitude;
    private double latitude;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        // get information from intent
        Bundle bundle = getIntent().getExtras();
        longitude = Double.parseDouble(bundle.getString("longitude"));
        latitude = Double.parseDouble(bundle.getString("latitude"));
        token = (String) bundle.getString("token");

        Spinner spinner = (Spinner) findViewById(R.id.locations_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void submit(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.locations_spinner);
        String ans = spinner.getSelectedItem().toString();

        Log.d(TAG, "ANSWER IS " + ans);

        JSONObject message = new JSONObject();
        message.put("com", "answer");
        message.put("nim", "13513024");
        message.put("answer", ans);
        message.put("longitude", longitude);
        message.put("latitude", latitude);
        message.put("token", token);

        SocketHub socketHub = new SocketHub(this, message);
        socketHub.execute();
    }

    @Override
    public void processFinish(JSONObject response){
        String status = (String) response.get("status");
        Log.d(TAG, "STATUS " + status);
        if(status.equals("ok")) {

            new AlertDialog.Builder(Answer.this)
                    .setTitle("Accepted!")
                    .setMessage("You're answer is correct")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            double longitude = Double.parseDouble((String) response.get("longitude"));
            double latitude = Double.parseDouble((String) response.get("latitude"));
            String token = (String) response.get("token");

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("token", token);
            startActivity(intent);

        } else if (status.equals("wrong_answer")) {

            new AlertDialog.Builder(Answer.this)
                    .setTitle("Wrong Answer :(")
                    .setMessage("Unfortunately, your answer is wrong.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            double longitude = Double.parseDouble((String) response.get("longitude"));
            double latitude = Double.parseDouble((String) response.get("latitude"));
            String token = (String) response.get("token");

            Intent intent = new Intent(this, Answer.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("token", token);
            startActivity(intent);

        } else if(status.equals("finish")) {

            new AlertDialog.Builder(Answer.this)
                    .setTitle("Finished!")
                    .setMessage("You've been finished all the challenge. Congratulations!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }
}
