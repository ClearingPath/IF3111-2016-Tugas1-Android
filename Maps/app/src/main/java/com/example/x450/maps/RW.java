package com.example.x450.maps;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

/**
 * Created by X450 on 27/03/2016.
 */
public class RW {
    private String filename;
    private String myDir;
    private Calendar cal;

    RW(){
        filename = "Log.txt";
        cal = Calendar.getInstance();
//        myDir = "com.example.x450.maps/texts/";
    }

    public void generateNoteOnSD(Context context,String sBody) {
        try {

            Log.d("state",Environment.getExternalStorageState());
            Log.d("dir",Environment.getExternalStorageDirectory().toString());
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, filename);

            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append("\n");
            writer.append("\n"+cal.getTime());
            writer.append("\n"+sBody);
            writer.flush();
            writer.close();
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
