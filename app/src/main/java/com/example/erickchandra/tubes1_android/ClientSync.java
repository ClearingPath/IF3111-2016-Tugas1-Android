package com.example.erickchandra.tubes1_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by erickchandra on 3/26/16.
 */

public class ClientSync extends AsyncTask <Void, Void, Void> {
    public AsyncResponse delegate = null;

    public static String hostname = "167.205.34.132";
    public static int hostport = 3111;
    String msgSend, msgRecv;
    Socket socket;
    private ProgressDialog progressDialog;

    ClientSync(Activity parentActivity, String msgSend) {
        progressDialog = new ProgressDialog(parentActivity);
        this.msgSend = msgSend;
    }

    public void SendAndThenRecvMessage() {
        // Opening socket
        try {
            socket = new Socket(hostname, hostport);
            // Sending Message
            PrintWriter output = null;
            try {
                output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            output.print(msgSend + "\n");
            output.flush();
            System.out.println("Sent message: " + msgSend);
            Log.d(this.getClass().toString(), "Send Message: " + msgSend);

            // Receiving Message
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                msgRecv = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Received message: " + msgRecv);
            Log.d(this.getClass().toString(), "Received Message: " + msgRecv);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    String getRecvMsg() {
        return msgRecv;
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog.setMessage("Connecting Server");
        this.progressDialog.show();
        Log.d(this.getClass().toString(), "Progress Dialog shown.");
    }

    @Override
    protected Void doInBackground(Void... params) {
        SendAndThenRecvMessage();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
            Log.d(this.getClass().toString(), "Progress Dialog dismissed.");
        }
        Log.d(this.getClass().toString(), "Received Message (ClientSync): " + msgRecv);
        delegate.processFinish(msgRecv);
    }
}
