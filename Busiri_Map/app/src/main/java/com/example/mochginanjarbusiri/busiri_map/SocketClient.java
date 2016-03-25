package com.example.mochginanjarbusiri.busiri_map;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Moch Ginanjar Busiri on 3/24/2016.
 */
public class SocketClient extends AsyncTask<String, String, String> {
    String address = "api.nitho.me";
    int port = 8080;
    String response = "";
    String message;
    private boolean success;
    GoogleMap mMap;

    SocketClient(String message, GoogleMap mMap)
    {
        this.message = message;
        this.mMap = mMap;
    }

  /*  public double getJSONLat() throws JSONException {
        return this.json.getDouble("latitude");
    }

    public double getJSONLong() throws JSONException {
        return this.json.getDouble("longitude");
    }*/

    protected String doInBackground(String... params) {
        Socket socket = null;
        BufferedReader in = null;
        DataOutputStream dataOutputStream = null;

        try {
            // Create a new Socket instance and connect to host
            socket = new Socket(address, port);

            //dataOutputStream = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // transfer JSONObject as String to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            /*dataOutputStream.writeUTF(message);*/
            out.println(message);

            // Thread will wait till server replies
            int c;
            while((c = in.read()) != -1)
            {
                response += (char) c;
            }
            Log.d("RESPONSE", response);

            if (response != null){
                publishProgress(response);
                success = true;
            } else {
                success = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {

            // close socket
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close input stream
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close output stream
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            JSONObject json = new JSONObject(result);
            double lat = json.getDouble("latitude");
            double longit = json.getDouble("longitude");
            LatLng itb = new LatLng(longit, lat);
            mMap.addMarker(new MarkerOptions().position(itb).title("Institut Teknologi Bandung"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(itb));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(itb, 20.0f));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


   /* public void receiveResponse(JSONObject json)
    {
        try {
            json = new JSONObject(this.response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
