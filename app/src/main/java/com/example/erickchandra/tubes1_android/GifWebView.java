package com.example.erickchandra.tubes1_android;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by erickchandra on 3/25/16.
 */
public class GifWebView extends WebView {

    public GifWebView(Context context, String path) {
        super(context);

        loadUrl(path);
    }
}
