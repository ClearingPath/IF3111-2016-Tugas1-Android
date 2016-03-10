package in.feryand.locationfinder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SocketHandler sock;
    private Message msg = Message.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Submit Answer");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.locations_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String location = parent.getItemAtPosition(position).toString();
        String answer = "app_error";

        if (location.equals("GKU Barat")) {
            answer = "gku_barat";
        } else if (location.equals("GKU Timur")) {
            answer = "gku_timur";
        } else if (location.equals("Intel")) {
            answer = "intel";
        } else if (location.equals("CC Barat")) {
            answer = "cc_barat";
        } else if (location.equals("CC Timur")) {
            answer = "cc_timur";
        } else if (location.equals("DPR")) {
            answer = "dpr";
        } else if (location.equals("Sunken")) {
            answer = "sunken";
        } else if (location.equals("Perpustakaan")) {
            answer = "perpustakaan";
        } else if (location.equals("PAU")) {
            answer = "pau";
        } else if (location.equals("Kubus")) {
            answer = "kubus";
        }

        try {
            JSONObject receivedProblem = new JSONObject(
                    sock.Send("{\"com\":\"answer\",\"nim\":\"" + msg.getNim() + "\",\"answer\":\"" + answer + "\",\"longitude\":\"" + msg.getLng() + "\",\"latitude\":\"" + msg.getLat() + "\",\"token\":\"" + msg.getToken() + "\"}"));

            String toToast = "Error Occurred.";

            if ( (receivedProblem.optString("status")).equals("wrong_answer") ) {
                msg.setStarted(false);
                toToast = "Wrong Answer, Please Start Over.";
            } else if ( (receivedProblem.optString("status")).equals("finish") ) {
                msg.setStarted(false);
                toToast = "Congratulations! You've 3 Right Answer in Row.";
            } else if ( (receivedProblem.optString("status")).equals("ok") ) {
                toToast = "Correct! Now Back to Map.";
                msg.setLatLng(receivedProblem.optString("longitude"), receivedProblem.optString("latitude"));
            }

            Toast toast = Toast.makeText(getApplicationContext(), toToast, Toast.LENGTH_SHORT);
            toast.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /* Well, what should I do? */
    }
}
