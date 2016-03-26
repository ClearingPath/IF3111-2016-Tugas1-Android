package com.example.erickchandra.tubes1_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by erickchandra on 3/26/16.
 */

public class ClientSync {
    public static String hostname = "api.nitho.me";
    public static int hostport = 8080;
    String msgSend, msgRecv;
    Socket socket;

    ClientSync(String msgSend) {
        this.msgSend = msgSend;
        try {
            socket = new Socket(hostname, hostport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendAndThenRecvMessage() {
        // Sending Message
        PrintWriter output = null;
        try {
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        output.print(msgSend + "\n");
        output.flush();
        System.out.println("Sent message: " + msgSend);

        // Receiving Message
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msgRecv = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Received message: " + msgRecv);
    }

    String getRecvMsg() {
        return msgRecv;
    }
}
