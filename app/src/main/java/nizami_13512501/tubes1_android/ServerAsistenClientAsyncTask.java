package nizami_13512501.tubes1_android;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by nim_13512501 on 26/03/16.
 */
public class ServerAsistenClientAsyncTask extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String request;
    String response = "";
    JSONObject responseJSONObject;
    ServerAsistenClientAsyncTaskCallbackTarget serverAsistenClientAsyncTaskCallbackTarget;
    ServerAsistenClientAsyncTaskSocketStore serverAsistenClientAsyncTaskSocketStore;

    ServerAsistenClientAsyncTask(String addr, int port, String request,
                                 ServerAsistenClientAsyncTaskCallbackTarget serverAsistenClientAsyncTaskCallbackTarget,
                                 ServerAsistenClientAsyncTaskSocketStore serverAsistenClientAsyncTaskSocketStore) {
        dstAddress = addr;
        dstPort = port;
        this.request = request;
        this.serverAsistenClientAsyncTaskCallbackTarget = serverAsistenClientAsyncTaskCallbackTarget;
        this.serverAsistenClientAsyncTaskSocketStore = serverAsistenClientAsyncTaskSocketStore;
    }

    Socket socket;

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            socket = serverAsistenClientAsyncTaskSocketStore.getSocket();
            if (socket==null) socket = new Socket(dstAddress, dstPort);
            else if (socket.isClosed() || !socket.isConnected())
                socket = new Socket(dstAddress, dstPort);

            OutputStream socketOutputStream = socket.getOutputStream();
            PrintStream socketPrintStream = new PrintStream(socketOutputStream);
            socketPrintStream.print(request);
            socketPrintStream.flush();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

         /*
          * notice: inputStream.read() will block if no data return
          */
            boolean continueReading = true;
            while (continueReading && (bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
                try{
                    responseJSONObject = new JSONObject(response);
                    continueReading = false;
                } catch (JSONException e) {
                }
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        serverAsistenClientAsyncTaskCallbackTarget.onCallback(response);
        serverAsistenClientAsyncTaskSocketStore.setSocket(socket);
    }

}
