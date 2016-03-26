package nizami_13512501.tubes1_android;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.internal.LargeAssetQueueStateChangeParcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;

/**
 * Created by nim_13512501 on 26/03/16.
 */
public class ServerAsistenClient implements ServerAsistenClientAsyncTaskCallbackTarget, ServerAsistenClientAsyncTaskSocketStore {

    String dstAddress;
    int dstPort;
    String response = "";

    MapsActivity mapsActivity;

    String token = null;

    static final String JSON_LAT_NAME_STR = "latitude";
    static final String JSON_LNG_NAME_STR = "longitude";

    public ServerAsistenClient(String dstAddress, int dstPort, MapsActivity mapsActivity){
        this.dstAddress=dstAddress;
        this.dstPort=dstPort;
        this.mapsActivity=mapsActivity;
        socket = null;
    }

    public void doFirstRequest(String nim){
        last_op = LAST_OP_FIRST_REQUEST;
        last_nim=nim;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("com","req_loc");
            jsonObject.put("nim",nim);
            if (token!=null)
                jsonObject.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerAsistenClientAsyncTask serverAsistenClientAsyncTask = new ServerAsistenClientAsyncTask(dstAddress,dstPort,jsonObject.toString(),
                this,this);
        serverAsistenClientAsyncTask.execute();
    }

    public void submitAnswer(String nim, String answer, LatLng latLng){
        last_op = LAST_OP_SUBMIT_ANSWER;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("com","answer");
            jsonObject.put("nim",nim);
            jsonObject.put("answer",answer);
            jsonObject.put(JSON_LAT_NAME_STR,latLng.latitude);
            jsonObject.put(JSON_LNG_NAME_STR,latLng.longitude);
            if (token!=null)
                jsonObject.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServerAsistenClientAsyncTask serverAsistenClientAsyncTask = new ServerAsistenClientAsyncTask(dstAddress,dstPort,jsonObject.toString(),this,this);
        serverAsistenClientAsyncTask.execute();
    }

    Socket socket;

    final static int LAST_OP_FIRST_REQUEST = 1;
    final static int LAST_OP_SUBMIT_ANSWER = 2;
    int last_op;
    String last_nim;

    @Override
    public void onCallback(String response) {

        if (response.contains("IOException")){
            mapsActivity.notifyUser("error connectiong. retrying... if you were trying to submit, please try submitting again", Toast.LENGTH_SHORT);
            doFirstRequest(last_nim);
            if (last_op==LAST_OP_SUBMIT_ANSWER) {
                mapsActivity.notifyUser("please try submitting again", Toast.LENGTH_SHORT);
            }

            return;
        }

        String usernotiftext = "response: " + response;

        try {
            JSONObject responseJson = new JSONObject(response);
            String status = responseJson.getString("status");

            token = responseJson.getString("token");

            if (status.equals("ok")) {
                String latStr = responseJson.optString("latitude");
                String lngStr = responseJson.optString("longitude");

                double latDbl;
                double lngDbl;
                if (latStr.isEmpty())
                    latDbl = responseJson.getDouble("latitude");
                else
                    latDbl = Double.parseDouble(latStr);
                if (lngStr.isEmpty())
                    lngDbl = responseJson.getDouble("longitude");
                else
                    lngDbl = Double.parseDouble(lngStr);

                LatLng latLng = new LatLng(latDbl, lngDbl);
                mapsActivity.updateTargetLatLng(latLng);
            }else{
                usernotiftext += " status: " + status;
            }

        } catch (JSONException e) {
            usernotiftext+="error in response format";
            e.printStackTrace();
        } catch(NumberFormatException e){
            usernotiftext+="error in lat/long format";
        }
        mapsActivity.notifyUser(usernotiftext, Toast.LENGTH_LONG);
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSocket(Socket s) {
        socket = s;
    }
}
