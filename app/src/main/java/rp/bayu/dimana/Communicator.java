package rp.bayu.dimana;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
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

/**
 * Created by User on 3/27/2016.
 */
public class Communicator extends AsyncTask<Void, Void, Void> {
    private String response = "";
    private String command = null;
    private String nim = "13513046";
    private Socket socket = null;
    private Context context;
    SharedPreferences sharedPref;

    Communicator(String response, String command, Context context) {
        this.response = response;
        this.command = command;
        this.context = context;
        sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        String dstAddress = sharedPref.getString("address", "167.205.24.132");
        int dstPort = sharedPref.getInt("port", 8080);
        nim = sharedPref.getString("nim", "13513046");
        JSONObject json, temp;

        if (command.equals("first")) {
            json = requestFirst();
            Log.d("dimana", "first");
        } else {
            json = requestLocation(command);
            Log.d("dimana", command);
        }

        Log.d("dimana", "sent" + json.toString());

        try {
            socket = new Socket(dstAddress, dstPort);
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(json);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            response = in.readLine();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    temp = new JSONObject(response);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    if (temp.getString("status").equals("ok")) {
                        prefEditor.putFloat("longitude", (float) temp.getDouble("longitude"));
                        prefEditor.putFloat("latitude", (float) temp.getDouble("latitude"));
                        prefEditor.putString("token", temp.getString("token"));
                        prefEditor.putString("status", "Correct");
                        prefEditor.apply();
                    } else if(temp.getString("status").equals("finish")) {
                        prefEditor.putString("token", temp.getString("token"));
                        prefEditor.putString("status", "Finish!");
                        prefEditor.apply();
                    } else {
                        prefEditor.putString("token", temp.getString("token"));
                        prefEditor.putString("status", "Wrong");
                        prefEditor.apply();
                    }
                    Log.d("dimana", "response " + temp.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private JSONObject requestLocation(String answer) {
        JSONObject json = new JSONObject();
        try {
            json.put("com", "answer");
            json.put("nim", nim);
            json.put("answer", answer);
            json.put("longitude", sharedPref.getFloat("longitude", 0));
            json.put("latitude", sharedPref.getFloat("latitude", 0));
            json.put("token", sharedPref.getString("token", "0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private JSONObject requestFirst() {
        JSONObject json = new JSONObject();
        try {
            json.put("com", "req_loc");
            json.put("nim", nim);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
