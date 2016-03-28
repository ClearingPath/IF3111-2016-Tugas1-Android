package com.mfikria.itblocator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.widget.TextView;

public class Client extends AsyncTask<String, Void, String> implements AsyncResponse {

    private static final String SERVER_ADDRESS = "167.205.34.132";
    private static final int SERVER_PORT = 3111;
    private static Socket socket;
    String dstAddress;
    int dstPort;
    String response = "";


    public AsyncResponse delegate = null;

    @Override
    public void processFinish(String output) {

    }


    Client() {
        dstAddress = SERVER_ADDRESS;
        dstPort = SERVER_PORT;
    }

    @Override
    protected String doInBackground(String... args) {
        String str = args[0];
        String response = null;
            try {
                socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                out.println(str);
                out.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String strIn;
                while ((strIn = in.readLine()) != null)
                {
                    sb.append(strIn + "\n");
                }

                // close the reader, and return the results as a String
                in.close();
                response = sb.toString();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
    //    super.onPostExecute(result);
        delegate.processFinish(result);


    }
}