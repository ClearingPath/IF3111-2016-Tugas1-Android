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
public class Communicator extends AsyncTask<JSONObject, JSONObject, JSONObject> {
    final String TAG = Communicator.class.getSimpleName();
    private static String dstAddress = "167.205.34.132";
    private static int dstPort = 3111;
    private JSONObject message = null;
    private static double latitude = -6.891323;
    private static double longitude = 107.610445;
    private static String status = "not_start";
    private static int nim = 13513073;
    private static String token = "";
    private callerAsync caller = null;

    public Communicator(callerAsync call) {
        caller = call;
    }

    public static String getToken() {
        return token;
    }

    public String getStat() {
        return status;
    }
    public static double getLatitude() {
        return latitude;
    }
    public static double getLongitude() {
        return longitude;
    }
    public void req_loc() {
        message = new JSONObject();
        try {
            message.put("com", "req_loc");
            message.put("nim", String.valueOf(nim));
            Log.d(TAG, "req_loc: "+message);
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
            Log.d(TAG, "doinba ex : ");
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
    protected JSONObject doInBackground(JSONObject... params) {
        JSONObject response = null;
        Log.d(TAG, "doInBackground: ");
        Socket sock = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Log.d(TAG, "doInBac 1: "+message.toString());
        try {
            sock = new Socket(dstAddress, dstPort);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            Log.d(TAG, "doInBac flush:" + message.toString());
            if(message == null)
                message = new JSONObject();
            out.println(message.toString());
            out.flush();
            Log.d(TAG, "doInBac flush:" + message.toString());
            int c;
            StringBuilder sb = new StringBuilder();
            while((c = in.read()) != -1)
                sb.append((char)c);
//            String resLine = in.readLine();
            Log.d(TAG, "doInBackground:"+sb.toString());
            response = new JSONObject(sb.toString());
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
        if(response == null)
            Log.d(TAG, "doInBackground: null");
        return response;
    }

    @Override
    protected void onPostExecute(JSONObject result) {

        Log.d(TAG, "onPostExecute: ");
        try {
            String status = (String)result.get("status");
            String token = (String) result.get("token");
            if(status.equals("ok")) {
                longitude = (double) result.get("longitude");
                latitude = (double) result.get("latitude");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                caller.processJSON(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                super.onPostExecute(result);
            }
        }
    }
}
