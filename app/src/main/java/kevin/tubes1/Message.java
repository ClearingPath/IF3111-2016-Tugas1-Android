package kevin.tubes1;


import android.app.Application;
import android.util.Log;


/**
 * Created by kevin on 25/03/2016.
 */
public class Message extends Application {

        private SocketHandler sock;

        private String token;
        private String nim = "13513036";
        private double lat;
        private double lng;
        private boolean started = false;

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
            sock.rc();
            return sock;
        }
}

