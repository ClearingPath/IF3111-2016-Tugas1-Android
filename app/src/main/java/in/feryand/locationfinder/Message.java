package in.feryand.locationfinder;

import android.app.Application;

/**
 * Created by Asus on 10/03/2016.
 */
public class Message extends Application {

    private String token;
    private String nim = "13513042";
    private String lat;
    private String lng;
    private boolean started = false;

    private static Message instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Message getInstance() {
        return instance;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getNim() {
        return nim;
    }
    public void setNIM(String nim) {
        this.nim = nim;
    }

    public String getLat() { return lat; }
    public String getLng() {
        return lng;
    }
    public void setLatLng(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public boolean getStarted() { return started; };
    public void setStarted(boolean started) {
        this.started = started;
    }


}
