package com.candy.myapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Created by Candy Olivia Mawalim on 27/03/2016.
 */
public class LogPage extends FragmentActivity{
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_activity);

        Bundle bundle = getIntent().getExtras();
        logView = (TextView) findViewById(R.id.client_server_log);
        logView.setText(bundle.getString("log"));

    }
}
