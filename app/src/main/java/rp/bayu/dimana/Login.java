package rp.bayu.dimana;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefEditor;
    private EditText tNIM, tAddr, tPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        tNIM = (EditText) findViewById(R.id.nim);
        tNIM.setText("13513046");
        tAddr = (EditText) findViewById(R.id.address);
        tAddr.setText("167.205.24.132");
        tPort = (EditText) findViewById(R.id.port);
        tPort.setText("8080");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveNIM(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        String nim = tNIM.getText().toString();
        String response = null;
        String address = tAddr.getText().toString();
        int port = Integer.parseInt(tPort.getText().toString());
        prefEditor = myPrefs.edit();
        prefEditor.putString("nim", nim);
        prefEditor.putString("address", address);
        prefEditor.putInt("port", port);
        prefEditor.apply();
        Communicator comm = new Communicator(response, "first", this);
        comm.execute();
        startActivity(intent);
    }
}
