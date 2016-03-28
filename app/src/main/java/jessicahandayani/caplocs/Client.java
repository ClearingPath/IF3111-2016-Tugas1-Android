package jessicahandayani.caplocs;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by jessica
 */
public class Client extends AsyncTask<Void, Void, Void> {
    String serverIP ="167.205.34.132";
    int port = 3111;

    private String dstAddress;
    private int dstPort;
    private String strResponse = "";
    private JSONObject request = new JSONObject();
    private JSONObject response;
    private boolean responseStatus = false;

    Client(){
        setDstAddress(serverIP);
        setDstPort(port);
    }

    Client(String addr, int port) {
        setDstAddress(addr);
        setDstPort(port);
    }

    public void setRequestLocation(){
        try {
            request.put("com","req_loc");
            request.put("nim", "13513069");
            //System.out.println("Set request: " + request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setAnswer(String answer, String longitude, String latitude, String token){
        try {
            request = new JSONObject();
            request.put("com","answer");
            request.put("nim", "13513069");
            request.put("answer", answer);
            request.put("longitude",longitude);
            request.put("latitude",latitude);
            request.put("token",token);

            //System.out.println("Set answer: " + request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;

        try {
            socket = new Socket(getDstAddress(), getDstPort());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(request.toString());
            System.out.println("Sent request: " + request.toString());
            String line;

            while ((line=in.readLine()) != null){
                //System.out.println(line);
                setStrResponse(strResponse + line);
            }
            //System.out.println("strResponse: "+ strResponse);
            if (!strResponse.equals("")){
                try {
                    setResponse(new JSONObject(strResponse));
                    System.out.println("Server Response: " + response.toString());
                    setResponseStatus(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            setStrResponse("UnknownHostException: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            setStrResponse("IOException: " + e.toString());
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
    }


    public String getDstAddress() {
        return dstAddress;
    }

    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public String getStrResponse() {
        return strResponse;
    }

    public void setStrResponse(String strResponse) {
        this.strResponse = strResponse;
    }

    public JSONObject getRequest() {
        return request;
    }

    public void setRequest(JSONObject request) {
        this.request = request;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public boolean isResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(boolean responseStatus) {
        this.responseStatus = responseStatus;
    }
}