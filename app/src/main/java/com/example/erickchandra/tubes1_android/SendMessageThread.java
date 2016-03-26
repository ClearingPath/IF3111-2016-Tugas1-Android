package com.example.erickchandra.tubes1_android;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by erickchandra on 3/25/16.
 */
public class SendMessageThread implements Runnable {
    Socket sharedSocket;
    String messageOut;

    SendMessageThread(Socket sharedSocket, String messageOut) {
        this.sharedSocket = sharedSocket;
        this.messageOut = messageOut;
    }

    @Override
    public void run() {
        try {
            sharedSocket.getOutputStream().write(messageOut.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
