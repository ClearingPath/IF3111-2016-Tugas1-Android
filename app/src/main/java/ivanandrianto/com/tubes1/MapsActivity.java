package ivanandrianto.com.tubes1;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap map;
    String serverResponse;
    final String address = "167.205.34.132";
    final int port = 3111;
    String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ImageButton btnCamera = (ImageButton)findViewById(R.id.button1);
        btnCamera.setImageResource(R.drawable.camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Camera.class);
                startActivity(intent);
            }
        });

        ImageButton btnMessage = (ImageButton)findViewById(R.id.button2);
        btnMessage.setImageResource(R.drawable.message);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                startActivity(intent);
            }
        });

        JSONObject json = new JSONObject();
        try{
            json.put("com", "req_loc");
            json.put("nim", "13513039");

            //SocketClient myClient =
            //myClient.execute();
            String response = new SocketClient(address,port,json).execute().get();
            Toast.makeText(getApplicationContext(), "zzz" + response, Toast.LENGTH_LONG).show();
        } catch (JSONException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
        map = supportmapfragment.getMap();
        supportmapfragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng itb = new LatLng(-6.8914906,107.6084704);
        map.addMarker(new MarkerOptions().position(itb).title("Marker in Indonesia"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(itb).zoom(15).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }
}
