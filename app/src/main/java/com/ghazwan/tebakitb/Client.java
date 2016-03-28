package com.ghazwan.tebakitb;

/**
 * Created by Ghazwan on 3/27/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client extends AsyncTask<Void, Void, Void> {

    private String destAddress = "167.205.34.132";
    private int destPort = 3111;

    private Context context;
    private String status = "";
    private String responseString = "";


    private JSONObject json;
    private double latitude;
    private double longitude;

    Client(JSONObject json, Context context) {
        String command;

        this.context = context.getApplicationContext();
        this.json = json;
        try {
            command = json.getString("com");
            if(command.equals("answer")){
                this.latitude = json.getDouble("latitude");
                this.longitude = json.getDouble("longitude");
            }
        }catch(org.json.JSONException e) {
            // nothing
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        String response = "";
        Socket socket = null;
        PrintWriter out;
        BufferedReader in;
        try {
            socket = new Socket(destAddress, destPort);
            Log.i("Client", "Connecting...");
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(json.toString());
            Log.i("Client", "Request: " + json.toString());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int c;
            responseString = "";
            while((c=in.read())!=-1){
                responseString+=(char)c;
            }
            json = new JSONObject(responseString);
            status = json.getString("status");

            if(status.equals("ok")){ //request location + correct answer
                Log.i("Client", "Request Success!");
                Log.i("Client", "Status: " + status);
                Log.i("Client", "Response: " + responseString);
            }else if (status.equals("wrong_answer")){ //wrong answer
                Log.i("Client", "Request Success!");
                Log.i("Client", "Status: " + status);
                Log.i("Client", "Response: " + responseString);
            }else if (status.equals("finish")){ //finished
                Log.i("Client", "Request Success!");
                Log.i("Client", "Status: " + status);
                Log.i("Client", "Response: " + responseString);
            }else if (status.equals("err")){ //error
                Log.i("Client", "Request Success!");
                Log.i("Client", "Status: " + status);
                Log.i("Client", "Response: " + responseString);
            }else{ //failed
                Log.i("Client", "Request Failed!");
            }
        }catch(org.json.JSONException e){
        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if(status.equals("ok")){ //request location + correct answer
            Intent intent = new Intent(context, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("response", responseString);
            context.startActivity(intent);
        }else if (status.equals("wrong_answer")){ //wrong answer
            Intent intent = new Intent(context, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("response", responseString);
            context.startActivity(intent);
        }else if (status.equals("finish")){ //finished
            Intent intent = new Intent(context, ChallengeFinish.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("response", responseString);
            context.startActivity(intent);
        }else if (status.equals("err")){ //error
            //nothing
        }else{ //failed
            //nothing
        }
    }

}
