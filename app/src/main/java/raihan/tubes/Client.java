package raihan.tubes;

/**
 * Created by Raihan on 3/27/2016.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Client extends AsyncTask<Void, Void, Void> {

    public static final String TAG = Client.class.getSimpleName();

    private String host = "167.205.34.132";
    private int port = 3111;
    private String responseLine;

    private Response response;
    private JSONObject message = null;

    Client(Response res, JSONObject msg) {
        response = res;
        message = msg;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(host, port);
            Log.d(TAG, "Connected to host");

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Log.d(TAG, "Sending message: " + message.toString());
            out.println(message.toString());
            out.flush();
            Log.d(TAG, "Message sent");

            responseLine = in.readLine();
            Log.d(TAG, "Response received: " + responseLine);

        } catch(UnknownHostException e) {

        } catch(IOException e) {

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        try {
            JSONObject resJson = (JSONObject) new JSONObject(responseLine);
            response.requestDone(resJson);
        } catch (JSONException ex) {
            Log.d(TAG, ex.toString());
        }
        super.onPostExecute(result);
    }
}