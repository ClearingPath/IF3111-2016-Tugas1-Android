package com.example.user.gps;

public class Container {
    private static String answer;
    private static String token;
    private static double ltd;
    private static double lng;
    private static String status;
    private static boolean isFirst = true;

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
    public static boolean getisFirst(){
        return isFirst;
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
    public static void setisFirst(boolean newVal){
        isFirst = newVal;
    }
}
