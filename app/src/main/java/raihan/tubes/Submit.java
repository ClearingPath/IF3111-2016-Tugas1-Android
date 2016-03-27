package raihan.tubes;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Submit extends AppCompatActivity implements Response {
    Button submit;
    public static final String TAG = MainActivity.class.getSimpleName();
    Spinner locationSpinner;
    TextView response;

    String status;
    private double latitude;
    private double longitude;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submit = (Button) findViewById(R.id.send);
        response = (TextView) findViewById(R.id.textView3);

        locationSpinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        locationSpinner.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        status = (String) bundle.get("status");
        longitude = (double) bundle.get("longitude");
        latitude = (double) bundle.get("latitude");
        token = (String) bundle.getString("token");

    }

    public void submitButton (View view) {
        Context context = getApplicationContext();
        String location = locationSpinner.getSelectedItem().toString();

        String ans = "";
        switch (location) {
            case "GKU Barat":
                ans = "gku_barat";
                break;
            case "GKU Timur":
                ans = "gku_timur";
                break;
            case "Intel":
                ans = "intel";
                break;
            case "CC Barat":
                ans = "cc_barat";
                break;
            case "CC Timur":
                ans = "cc_timur";
                break;
            case "DPR":
                ans = "dpr";
                break;
            case "Oktagon":
                ans = "oktagon";
                break;
            case "Perpustakaan":
                ans = "perpustakaan";
                break;
            case "PAU":
                ans = "pau";
                break;
            case "Kubus":
                ans = "kubus";
                break;
        }

        JSONObject answer = new JSONObject();
        try {
            answer.put("com", "answer");
            answer.put("nim", "13513022");
            answer.put("answer", ans);
            answer.put("longitude", latitude);
            answer.put("latitude", longitude);
            answer.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Answer sent: " + answer.toString());

        Client client = new Client(this, answer);
        client.execute();

    }

    @Override
    public void requestDone(JSONObject res) {
        String t_status = "";
        try {
            t_status = (String) res.get("status");
            token = (String) res.get("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (t_status.equals("ok")) {
            Toast.makeText(getApplicationContext(), "Your answer is correct!", Toast.LENGTH_SHORT).show();

            try {
                latitude = (double) res.get("latitude");
                longitude = (double) res.get("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent (this, MainActivity.class);
            intent.putExtra("status", t_status);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("token", token);
            startActivity(intent);

        } else if (t_status.equals("wrong_answer")) {
            Toast.makeText(getApplicationContext(), "Sorry, your answer is wrong", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent (this, MainActivity.class);
            intent.putExtra("status", status);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("token", token);
            startActivity(intent);

        } else if (t_status.equals("finish")) {
            Toast.makeText(getApplicationContext(), "Congratulation, You've completed all the quest!", Toast.LENGTH_SHORT).show();
        }
    }

}
