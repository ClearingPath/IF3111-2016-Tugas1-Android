package ivanandrianto.com.tubes1;

/**
 * Created by Ivan on 3/23/2016.
 */
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import org.json.JSONObject;

public class SocketClient extends AsyncTask<Void, Void, String> {

    private String address;
    int port;
    private String serverResponse = "";
    private String response;
    private JSONObject json;

    SocketClient(String addr, int port,JSONObject msg) {
        this.address = addr;
        this.port = port;
        json = msg;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        Socket socket = null;
        try {
            socket = new Socket(address, port);
            try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                out.write(json.toString());
                out.newLine();
                out.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = socket.getInputStream();
            while ((bytes = is.read(buffer)) != -1) {
                byteOutput.write(buffer, 0, bytes);
                serverResponse += byteOutput.toString("UTF-8");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverResponse = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            serverResponse = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return serverResponse;
    }
    @Override
    protected void onPostExecute(String result) {
        result = serverResponse;
        super.onPostExecute(result);
    }
}