package in.feryand.locationfinder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Message msg = Message.getInstance();
    private String answer = "app_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Submit Answer");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView logList =(ListView)findViewById(R.id.listViewLog);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, msg.log);
        logList.setAdapter(arrayAdapter);

        Button submit = (Button) findViewById(R.id.button);
        submit.setEnabled(true);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.setEnabled(false);
                try {

                    msg.addLog("{\"com\":\"answer\",\"nim\":\"" + msg.getNim() + "\",\"answer\":\"" + answer + "\",\"longitude\":" + msg.getLng() + ",\"latitude\":" + msg.getLat() + ",\"token\":\"" + msg.getToken() + "\"}");
                    JSONObject receivedProblem = new JSONObject(
                            (msg.getSock()).Send("{\"com\":\"answer\",\"nim\":\"" + msg.getNim() + "\",\"answer\":\"" + answer + "\",\"longitude\":" + msg.getLng() + ",\"latitude\":" + msg.getLat() + ",\"token\":\"" + msg.getToken() + "\"}"));
                    msg.addLog(receivedProblem.toString());

                    String toToast = receivedProblem.toString();

                    if ((receivedProblem.optString("status")).equals("wrong_answer")) {
                        msg.setStarted(false);
                        toToast = "Wrong Answer, Please Start Over.";
                    } else if ((receivedProblem.optString("status")).equals("finish")) {
                        msg.setStarted(false);
                        toToast = "Congratulations You've Finished All!";
                    } else if ((receivedProblem.optString("status")).equals("err")) {
                        msg.setStarted(false);
                        toToast = "Server says error.";
                    } else if ((receivedProblem.optString("status")).equals("ok")) {
                        toToast = "Correct! Now Back to Map.";
                        msg.setLatLng(receivedProblem.optDouble("longitude"), receivedProblem.optDouble("latitude"));
                    }

                    Toast toast = Toast.makeText(getApplicationContext(), toToast, Toast.LENGTH_SHORT);
                    toast.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.locations_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                answer = "gku_barat";
                break;
            case 1:
                answer = "gku_timur";
                break;
            case 2:
                answer = "intel";
                break;
            case 3:
                answer = "cc_barat";
                break;
            case 4:
                answer = "cc_timur";
                break;
            case 5:
                answer = "dpr";
                break;
            case 6:
                answer = "sunken";
                break;
            case 7:
                answer = "perpustakaan";
                break;
            case 8:
                answer = "pau";
                break;
            case 9:
                answer = "kubus";
                break;
            case 10:
                answer = "oktagon";
                /*  Changing specification near deadline isn't a good idea,
                    this thing is never tested, hope for the best   */
                break;
            default:
                answer = "select_error";
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /* Well, what should I do? */
    }
}
