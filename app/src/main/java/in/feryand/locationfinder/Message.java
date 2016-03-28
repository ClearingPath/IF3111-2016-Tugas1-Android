package in.feryand.locationfinder;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Asus on 10/03/2016.
 */
public class Message extends Application {

    private SocketHandler sock;

    private String token;
    private String nim = "13513042";
    private double lat;
    private double lng;
    private boolean started = false;

    public ArrayList<String> log = new ArrayList<>();

    private static Message instance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        sock = new SocketHandler();
        Thread netThread = new Thread(sock);
        netThread.start();

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

    public double getLat() { return lat; }
    public double getLng() {
        return lng;
    }
    public void setLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public boolean getStarted() { return started; }
    public void setStarted(boolean started) {
        this.started = started;
    }

    public SocketHandler getSock() {
        sock.reconnect();
        return sock;
    }

    public void addLog(String s) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        String ts = day + "/" + month + "/" + year + " " + hour + ":" + minute + ":" + sec;

        log.add("[" + ts + "] " + s);
    }
}
