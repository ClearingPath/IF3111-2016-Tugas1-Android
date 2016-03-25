package com.example.x450.maps;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by X450 on 24/03/2016.
 */
class AsyncAction extends AsyncTask<String, Void, String> {
    private Socket socket;
    public PrintWriter out;
    public BufferedReader in ;
    private Context context;
    private ResponseHandlerInterface handler;

    AsyncAction(Context context, ResponseHandlerInterface handler){
        this.context = context;
        this.handler = handler;
    }

    protected String doInBackground(String... args) {
        String result = "";
        try {
            String serverAddr = context.getString(R.string.servAdd);
            socket = new Socket(serverAddr, Integer.parseInt(context.getString(R.string.port)));
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            pw.println(args[0]);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            result= br.readLine();
            socket.close();
        } catch (Exception e) {
            Log.d("erroru ",e.getMessage());
        }
        Log.d("do in background temp", result);

        return result;//returns what you want to pass to the onPostExecute()
    }

    @Override
    protected void onPostExecute(String result) {
        //result is the data returned from doInbackground
        final String showTemp = result;
        Log.d("onPostExe","result");

        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("e",e.getMessage());
        }
        handler.onResponse(json);
    }
}
