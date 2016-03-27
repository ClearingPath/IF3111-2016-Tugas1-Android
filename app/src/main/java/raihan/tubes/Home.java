package raihan.tubes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity implements  Response{

    private static final String TAG = Home.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void requestLocation(View view) {
        JSONObject message = new JSONObject();

        try {
            message.put("com", "req_loc");
            message.put("nim", "13513022");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Message to send: " + message.toString());

        Client client = new Client(this, message);
        client.execute();
    }

    @Override
    public void requestDone(JSONObject res) {

        String status = "";
        double latitude = Double.NaN;
        double longitude = Double.NaN;
        String token = "";
        try {
            status = (String) res.get("status");
            Log.d(TAG, "Status: " + status);
            latitude = (double) res.get("longitude");
            Log.d(TAG, "Latitude: " + latitude);
            longitude = (double) res.get("latitude");
            Log.d(TAG, "Longitude: " + longitude);
            token = (String) res.get("token");
            Log.d(TAG, "Token: " + token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("status", status);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("token", token);
        startActivity(intent);
    }

}
