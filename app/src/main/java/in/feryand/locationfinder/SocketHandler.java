package in.feryand.locationfinder;

import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Created by feryandi on 09/03/2016.
 */
public class SocketHandler implements Runnable {
    private Socket socket;
    private String ServerIP = "54.169.132.235";
    private static final int ServerPort = 12345;

    @Override
    public void run() {
        try {
            socket = new Socket(ServerIP, ServerPort);
        } catch(Exception e) {
            // Die Bitch Die
        }
    }

    public String Send(String s) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream()));

            output.print(s + "\n");
            output.flush();

            return input.readLine();
        } catch (UnknownHostException e) {
            System.out.print(e.toString());
        } catch (IOException e) {
            System.out.print(e.toString());
        } catch (Exception e) {
            System.out.print(e.toString());
        }
        return "{}";
    }
}
