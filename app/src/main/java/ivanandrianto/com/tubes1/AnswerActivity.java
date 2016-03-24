package ivanandrianto.com.tubes1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AnswerActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button btnSubmit;
    private String selected;
    private String address;
    private int port;
    private String nim;
    private String latitude;
    private String longitude;
    private String token;
    private String status;
    private int check=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        address = getResources().getString(R.string.address);
        port = Integer.parseInt(getResources().getString(R.string.port));
        nim = getResources().getString(R.string.nim);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null) {
            latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
            token = bundle.getString("token");
        }
        TextView tv = (TextView)findViewById(R.id.tv);
        tv.setText(latitude + " " +  longitude + " " +  token);

        spinner = (Spinner) findViewById(R.id.spinner1);
        List<String> locations = new ArrayList<String>();
        locations.add("gku_barat");
        locations.add("gku_timur");
        locations.add("intel");
        locations.add("cc_barat");
        locations.add("cc_timur");
        locations.add("dpr");
        locations.add("sunken");
        locations.add("perpustakaan");
        locations.add("pau");
        locations.add("kubus");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String item = spinner.getSelectedItem().toString();
                Toast.makeText(AnswerActivity.this, item, Toast.LENGTH_SHORT).show();
                selected = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        btnSubmit = (Button)findViewById(R.id.button_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try{
                    json.put("com", "answer");
                    json.put("nim", "13513039");
                    json.put("answer", selected);
                    json.put("latitude", latitude);
                    json.put("longitude", longitude);
                    json.put("token", token);
                    String response = new SocketClient(address,port,json).execute().get();
                    Toast.makeText(getApplicationContext(), "zzz" + response, Toast.LENGTH_LONG).show();
                    JSONObject jsonObject = new JSONObject(response);

                    status = jsonObject.optString("status").toString();
                    token = jsonObject.optString("token").toString();
                    Intent resultIntent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putString("token", token);
                    if(status.equals("ok")){
                        latitude = jsonObject.optString("longitude").toString();
                        longitude = jsonObject.optString("latitude").toString();
                        extras.putString("latitude", latitude);
                        extras.putString("longitude", longitude);
                    } else if(status.equals("wrong_answer")){

                    } else if(status.equals("finish")){
                        check = 1;
                    }
                    resultIntent.putExtras(extras);
                    setResult(Activity.RESULT_OK, resultIntent);
                } catch (JSONException e){
                    e.printStackTrace();
                } catch (ExecutionException e){
                    e.printStackTrace();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                /*Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                finish();
            }
        });
    }
}
