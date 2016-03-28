package jessicahandayani.caplocs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jessica
 */
public class SubmitFormActivity extends AppCompatActivity {

    private JSONObject message;
    private JSONObject response = new JSONObject();

    public final static String EXTRA_MESSAGE = "jessicahandayani.CapLocs.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_submit_form);

        Intent intent = getIntent();
        try {
            message = new JSONObject(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.location_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    public void submitAnswer(View view){
        //submit answer to server
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String answer = setLocationAnswer(spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString());
        Client client = new Client();
        client.setAnswer(answer, message.optString("longitude"), message.optString("latitude"), message.optString("token"));
        client.execute();
        while (!client.isResponseStatus()){

        }
        response = client.getResponse();
        checkResponse();
    }

    private String setLocationAnswer(String selectedAnswer){
        switch (selectedAnswer){
            case "GKU Barat" :{
                return "gku_barat";
            }
            case "GKU Timur" :{
                return "gku_timur";
            }
            case "Intel" : {
                return "intel";
            }
            case "CC Barat" : {
                return "cc_barat";
            }
            case "CC Timur" : {
                return "cc_timur";
            }
            case "DPR" : {
                return "dpr";
            }
            case "Oktagon" : {
                return "oktagon";
            }
            case "Perpustakaan" : {
                return "perpustakaan";
            }
            case "PAU" : {
                return "pau";
            }
            case "Kubus" : {
                return "kubus";
            }
            default:{
                return selectedAnswer;
            }
        }
    }

    private void checkResponse(){
        String status = response.optString("status");
        System.out.println("Status: " + status);
        switch (status){
            case "ok": {
                //System.out.println("masuk ke ok");
                Intent intent = new Intent();
                intent.putExtra(EXTRA_MESSAGE, response.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case "wrong_answer": {
                //System.out.println("masuk ke wrong");
                Toast.makeText(this, "Wrong answer! Try again.", Toast.LENGTH_LONG).show();
                break;
            }
            case "finish": {
                //System.out.println("masuk ke finish");
                Toast.makeText(this, "You've finished the quests!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_MESSAGE, response.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
        }
    }

}
