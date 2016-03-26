package com.example.user.gps;

public class Container {
    private static String answer;
    private static String token;
    private static double ltd;
    private static double lng;
    private static String status = "no_status";
    private static boolean isFirst = true;
    private static int check = 0;
    private static String serverIP = "167.205.34.132";
    private static int port = 3111;

    public static String getAnswer(){
        return answer;
    }
    public static String getToken(){
        return token;
    }
    public static double getLtd(){
        return ltd;
    }
    public static double getLng(){
        return lng;
    }
    public static String getStatus(){
        return status;
    }
    public static int getCheck(){
        return check;
    }
    public static boolean getisFirst(){
        return isFirst;
    }
    public static String getServerIP(){
        return serverIP;
    }
    public static int getPort(){
        return port;
    }
    public static void setAnswer(String newVal){
        answer = newVal;
    }
    public static void setToken(String newVal){
        token = newVal;
    }
    public static void setLtd(double newVal){
        ltd = newVal;
    }
    public static void setLng(double newVal){
        lng = newVal;
    }
    public static void setStatus(String newVal){
        status = newVal;
    }
    public static void setCheck(int newVal){
        check = newVal;
    }
    public static void setisFirst(boolean newVal){
        isFirst = newVal;
    }
    public static void setServerIP(String newVal){
        serverIP = newVal;
    }
    public static void setPort(int newVal){
        port = newVal;
    }
}
