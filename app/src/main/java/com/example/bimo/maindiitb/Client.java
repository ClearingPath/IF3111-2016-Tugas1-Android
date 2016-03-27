package com.example.bimo.maindiitb;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by Bimo on 25-Mar-16.
 */
public class Client extends AsyncTask<Void, Void, String> {
    String dest;
    int destport;
    String response = "";
    String textResponse;
    String TAG;
    JSONObject json;

    Client(String addr, int port, String textResponse, JSONObject message){
        dest = addr;
        destport = port;
        json = message;
        this.textResponse = textResponse;
    }

    @Override
    protected String doInBackground(Void... arg0){

        Socket socket = null;

        try {
            socket = new Socket(dest, destport);

            try{
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(json.toString());
                Log.d(TAG, "Dikirim: " + json.toString());
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            //InputStream inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int c;
            // inputStream.read() will block if no data return

            while ((c = in.read()) != -1){
                response += (char) c;
            }

        }catch(UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
            response = "IOException" + e.toString();
        }finally{
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
    @Override
    protected void onPostExecute(String result) {
//        textResponse.setText(response);
        textResponse = response;
        super.onPostExecute(result);
    }
}
