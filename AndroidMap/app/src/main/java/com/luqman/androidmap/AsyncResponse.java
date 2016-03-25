package com.luqman.androidmap;

import org.json.simple.JSONObject;

/**
 * Created by Luqman A. Siswanto on 26/03/2016.
 */
public interface AsyncResponse {
    void processFinish(JSONObject response);
}
