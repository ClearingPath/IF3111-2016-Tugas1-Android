package ivanandrianto.com.tubes1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Ivan on 3/27/2016.
 */
public class CheckConnection {
    Context context;
    ConnectivityManager cm;

    public CheckConnection(Context context, ConnectivityManager cm){
        this.context = context;
        this.cm = cm;
    }

    public boolean checkNetworkConnection(){
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork!=null){
            // Check for network connections
            if ( activeNetwork.getState() == android.net.NetworkInfo.State.CONNECTED ||
                    activeNetwork.getState() == android.net.NetworkInfo.State.CONNECTING ) {
                return true;
            } else if ( activeNetwork.getState() == android.net.NetworkInfo.State.DISCONNECTED ) {
                Toast.makeText(context, " Not Connected ", Toast.LENGTH_LONG).show();
                return false;
            } else {
                return false;
            }
        } else {
            Toast.makeText(context, " No active network ", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
