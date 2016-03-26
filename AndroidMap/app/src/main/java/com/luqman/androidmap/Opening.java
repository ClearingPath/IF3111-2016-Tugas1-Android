package com.luqman.androidmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import org.json.simple.JSONObject;

public class Opening extends AppCompatActivity implements AsyncResponse {
    private Boolean done = false;
    private JSONObject response;

    public static final String TAG = Opening.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("Loading app...");

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.opening);
        layout.addView(textView);

        sendCredential();
    }

    private void sendCredential() {
        JSONObject message = new JSONObject();
        message.put("nim", "13513024");
        message.put("com", "req_loc");

        Log.d(TAG, "berhasil buat json object");
        Log.d(TAG, "JSONObject to send: " + message.toString());

        SocketHub socketHub = new SocketHub(this, message);
        socketHub.execute();
    }

    @Override
    public void processFinish(JSONObject response){
        String status = (String) response.get("status");
        Log.d(TAG, "status " + status);
        double longitude = (double) response.get("longitude");
        double latitude = (double) response.get("latitude");
        String token = (String) response.get("token");

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("token", token);
        startActivity(intent);
    }
}
