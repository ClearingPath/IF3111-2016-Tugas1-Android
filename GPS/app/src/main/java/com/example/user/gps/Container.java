package com.example.user.gps;

public class Container {
    private static String value;


    public static String getValue(){
        return value;
    }

    public static void setValue(String newVal){
        value = newVal;
    }
}
