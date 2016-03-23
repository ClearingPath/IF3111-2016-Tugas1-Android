package ivanandrianto.com.tubes1;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AnswerActivity extends AppCompatActivity {
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        spinner = (Spinner) findViewById(R.id.spinner1);
        //spinner.setOnTouchListener(Spinner_OnTouch);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        /*spinner.setOnClickListener(new Spinner.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AnswerActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    /*private View.OnTouchListener Spinner_OnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                String item = spinner.getSelectedItem().toString();
                Toast.makeText(AnswerActivity.this, item, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };*/


}
