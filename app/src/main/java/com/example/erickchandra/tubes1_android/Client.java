package com.example.erickchandra.tubes1_android;

/**
 * Created by erickchandra on 3/25/16.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    Activity parentActivity;
    String messageIn, messageOut;

    private ProgressDialog progressDialog;

    Client(String addr, int port, String messageIn, String messageOut) {
        // this.parentActivity = parentActivity;
        dstAddress = addr;
        dstPort = port;
        progressDialog = new ProgressDialog(parentActivity);

        this.messageIn = messageIn;
        this.messageOut = messageOut;
    }

    @Override
    protected void onPreExecute() {
        this.progressDialog.setMessage("Connecting Server");
        this.progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        try {
            socket = new Socket(dstAddress, dstPort);
            System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");

//            SendMessageThread sendMessage = new SendMessageThread(socket, messageOut);
//            Thread smt = new Thread(sendMessage).start();

//            final Socket finalSocket = socket;
//            Thread sendMsgThread = new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        finalSocket.getOutputStream().write(messageOut.getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            sendMsgThread.start();

//            socket.getOutputStream().write(messageOut.getBytes());
            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            output.print(messageOut + "\n");
            output.flush();

            System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
//                    1024);
//            byte[] buffer = new byte[1024];
//
//            int bytesRead;
//            InputStream inputStream = socket.getInputStream();

         /*
          * notice: inputStream.read() will block if no data return
          */
            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//                System.out.println("######################################");
//            }

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            messageIn = input.readLine();

            System.out.println("Response: " + messageIn);

            System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
                System.out.println("**************************************");
            }

            messageIn = response;

            // Toast.makeText(parentActivity, "Server response:\n" + response, Toast.LENGTH_LONG);
            System.out.println("\n\n***\nSERVER RESPONSE: " + messageIn + "\n***\n\n");

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
