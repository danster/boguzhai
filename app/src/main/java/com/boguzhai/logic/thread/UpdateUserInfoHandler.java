package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danster on 2/9/15.
 */

public class UpdateUserInfoHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        if(msg.what == 1){
            try {
                JSONObject result = new JSONObject((String)msg.obj).getJSONObject("result");
                int code = Integer.parseInt(result.getString("code"));
                if(code==0){
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}