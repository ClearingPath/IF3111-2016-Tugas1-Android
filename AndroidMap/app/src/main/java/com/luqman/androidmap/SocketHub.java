package com.luqman.androidmap;
/**
 * Created by Luqman A. Siswanto on 25/03/2016.
 */

import android.util.Log;
import android.os.AsyncTask;
import org.json.simple.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SocketHub extends AsyncTask<Void, Void, Void> {
    private String host = "167.205.34.132";
    private int port = 3111;

    private JSONObject message = null;
    private JSONObject response = null;
    private Boolean done = false;

    public static final String TAG = SocketHub.class.getSimpleName();

    public SocketHub(JSONObject msg, JSONObject res, Boolean done) {
        this.message = msg;
        this.response = res;
        this.done = done;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;

        try {
            socket = new Socket(host, port);
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(message.toString());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            Log.d(TAG, "socket: " + (socket != null));
            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            // notice: inputStream.read() will block if no data return
            String responseLine = "";

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                responseLine += byteArrayOutputStream.toString("UTF-8");
            }
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(responseLine);
                response = (JSONObject) obj;
            } catch (ParseException ex) {
                Log.d(TAG, ex.toString());
            }
            done = true;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "UnknownHostException: " + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "IOException: " + e.toString());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}
