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
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SocketHub extends AsyncTask<Void, Void, Void> {
    private String host = "167.205.34.132";
    private int port = 3111;

    private JSONObject message = null;
    private String responseLine;

    public static final String TAG = SocketHub.class.getSimpleName();

    public AsyncResponse delegate = null;

    public SocketHub(AsyncResponse delegate, JSONObject msg) {
        this.delegate = delegate;
        this.message = msg;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(host, port);

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Log.d(TAG, "SENT JSON OBJECT: " + message.toString());
            out.println(message.toString());
            out.flush();
            Log.d(TAG, "UDAH KEKIRIM LHOOO");
            responseLine = in.readLine();
            Log.d(TAG, "berhasil menerima pesan " + responseLine);

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
        JSONObject response = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(responseLine);
            response = (JSONObject) obj;
        } catch (ParseException ex) {
            Log.d(TAG, ex.toString());
        }
        delegate.processFinish(response);
        super.onPostExecute(result);
    }


}
