package com.example.erickchandra.tubes1_android;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity implements AsyncResponse {
    ClientSync cs;
    String msgRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Android Map ITB Project");
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        InputStream stream = null;
        try {
            stream = getAssets().open("google_logo_animation_small.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GifWebView gifWebView = new GifWebView(this, "file:///android_res/drawable/google_logo_animation_small.gif");
        gifWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 720));
        gifWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // setContentView(gifWebView);

        LinearLayout linearLayout_GifWebView = new LinearLayout(this);
        linearLayout_GifWebView.addView(gifWebView);
        linearLayout_GifWebView.setGravity(Gravity.BOTTOM);
        LinearLayout.LayoutParams lp_linearLayout_GifWebView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 720);
        linearLayout_GifWebView.setLayoutParams(lp_linearLayout_GifWebView);
//        setContentView(linearLayout_GifWebView);

        Button button_start = new Button(this);
        button_start.setText("Start Seeking");
        LinearLayout.LayoutParams lp_button_start = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button_start.setLayoutParams(lp_button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchComm();
            }
        });

        LinearLayout linearLayout_button_start = new LinearLayout(this);
        linearLayout_button_start.addView(button_start);
        linearLayout_button_start.setGravity(Gravity.BOTTOM);
        LinearLayout.LayoutParams lp_linearLayout_button_start = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout_button_start.setLayoutParams(lp_linearLayout_button_start);
//        setContentView(linearLayout_button_start);

        LinearLayout linearLayout_whole = new LinearLayout(this);
        linearLayout_whole.addView(linearLayout_GifWebView);
        linearLayout_whole.addView(linearLayout_button_start);
        linearLayout_whole.setOrientation(LinearLayout.VERTICAL);
        linearLayout_whole.setGravity(Gravity.BOTTOM);
        setContentView(linearLayout_whole);
    }

    public void launchComm() {
        MessageSendParser msp = new MessageSendParser(0, "13512999");
        cs = new ClientSync(this, msp.getJSONObjectStr());
//        cs.SendAndThenRecvMessage();
        cs.delegate = this;
        cs.execute();
    }

    public void launchMap() {
//        Client newComm = new Client("api.nitho.me", 8080, receivedMessage, "{\"com\":\"req_loc\",\"nim\":\"13512999\"}\n");
//        newComm.execute();

        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("Message", msgRecv);
        startActivity(mapIntent);
    }

    @Override
    public void processFinish(String output) {
        msgRecv = output;
        launchMap();
    }
}
