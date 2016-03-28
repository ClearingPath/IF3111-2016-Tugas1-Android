package me.ibrohim.orange;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by ibrohim on 3/26/16.
 */
public class Transmission {

    private final String dstAddress = "167.205.34.132";
    private final int dstPort = 3111;

    private Socket socket;
    private OutputStream os;
    private InputStream is;

    public Transmission() {

        try {
            socket = new Socket(dstAddress, dstPort);

            os = socket.getOutputStream();
            is = socket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String read(){
        int bytesRead;
        byte[] buffer = new byte[1024];
        String response = "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);

        try {

            while ((bytesRead = is.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public void write(String s){

        try {

            byte[] buffer = s.getBytes();

            os = socket.getOutputStream();
            os.write(buffer, 0, buffer.length);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {

        try {

            socket.shutdownInput();
            socket.shutdownOutput();

            socket.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static class LocationRequestTask extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject;

        public interface LocationRequestListener {
            void onLocationAcquired(double longitude, double latitude, String token);
        }

        public void setListener(LocationRequestListener l) {
            delegate = l;
        }

        LocationRequestListener delegate;

        @Override
        protected Void doInBackground(Void... arg) {

            Log.i("transmission", "try to connecting");

            Transmission t = new Transmission();

            t.write("{\"com\":\"req_loc\",\"nim\":\"13513090\"}\n");
            String response = t.read();

            t.close();

            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            double latitude;
            double longitude;
            String token;

            try {

                longitude = jsonObject.getDouble("longitude");
                latitude = jsonObject.getDouble("latitude");
                token = jsonObject.getString("token");

                Log.i("transmission", "response: longitude: "+ longitude + " latitude: " + latitude);

                delegate.onLocationAcquired(longitude, latitude, token);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SendAnswerTask extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;

        public interface SendAnswerListener {
            void onAnswerSent(String result);
        }

        public void setListener(SendAnswerListener l) {
            delegate = l;
        }

        SendAnswerListener delegate;

        @Override
        protected Void doInBackground(String... arg) {

            Log.i("transmission", "try to connecting");

            Transmission t = new Transmission();

            String answer = "{\"com\":\"answer\", \"nim\":\"13513090\",\"answer\":\""+arg[0]+"\", \"longitude\":\""+arg[1]+"\",\"latitude\":\""+arg[2]+"\",\"token\":\""+arg[3]+"\"}\n";

            Log.i("transmission", "answer: "+answer);

            t.write(answer);
            String response = t.read();

            t.close();

            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            String status;

            try {

                status = jsonObject.getString("status");

                Log.i("transmission", "response: "+ status);

                delegate.onAnswerSent(status);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
