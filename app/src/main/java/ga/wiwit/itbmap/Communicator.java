package ga.wiwit.itbmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Wiwit Rifa'i on 27/03/2016.
 */
public class Communicator extends AsyncTask<Void, Void, Void> {
    final String TAG = Communicator.class.getSimpleName();
    private static Communicator instance = null;
    private String dstAddress;
    private int dstPort;
    private JSONObject response = null;
    private JSONObject message = null;
    private double latitude = -6.891323;
    private double longitude = 107.610445;
    private String status = "not_start";
    private int nim = 13513073;
    private String token = "";
    private GoogleMap map = null;
    private Context context = null;

    private Communicator(String add, int port, int tnim) {
        dstAddress = add;
        dstPort = port;
        nim = tnim;
    }
    public static Communicator getInstance() {
        if(instance == null)
            instance = new Communicator("167.205.34.132", 3111, 13513073);
        return instance;
    }
    public String getStat() {
        return status;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setMap(GoogleMap mmap) {
        map = mmap;
    }
    public void setContext(Context con) {
        context = con;
    }
    public void req_loc() {
        Log.d(TAG, "req_loc: ");
        message = new JSONObject();
        try {
            message.put("com", "req_loc");
            message.put("nim", String.valueOf(nim));
            this.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void answer(String option) {
        Log.d(TAG, "answer: " + option);
        message = new JSONObject();
        try {
            message.put("com", "answer");
            message.put("nim", String.valueOf(nim));
            message.put("answer", option);
            message.put("longitude", longitude);
            message.put("latitude", latitude);
            message.put("token", token);
            this.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "doInBackground: ");
        Socket sock = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            sock = new Socket(dstAddress, dstPort);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            if(message == null)
                message = new JSONObject();
            out.println(message.toString());
            out.flush();

            response = new JSONObject(in.readLine());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(sock != null)
                try {
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute: ");
        try {
            if(context != null)
                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG);
            status = (String)response.get("status");
            token = (String) response.get("token");
            if(status.equals("ok")) {
                longitude = (double) response.get("longitude");
                latitude = (double) response.get("latitude");
                if(map != null) {
                    map.clear();
                    LatLng pos = new LatLng(latitude, longitude);
                    map.addMarker(new MarkerOptions().position(pos).title("Find this location!"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
