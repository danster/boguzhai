package com.boguzhai.logic.thread;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.boguzhai.activity.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danster on 2/9/15.
 */

public class BaseHttpHandler extends Handler {
    protected Context c;
    public AlertDialog.Builder tips;

    public  BaseHttpHandler(Context c){
        this.c = c;
        tips = new AlertDialog.Builder(c);
        tips.setTitle("提示").setPositiveButton("确定", null);
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == 1){
            try {
                JSONObject result = new JSONObject((String)msg.obj);
                int code = 1;
                if(result.has("code")){ code = Integer.parseInt(result.getString("code"));}
                JSONObject data = null;
                if(result.has("data")){ data = result.getJSONObject("data");}

                handleResult(code, data);

            } catch (JSONException ex) {
                tips.setMessage("服务器出错").create().show();
            }
        }else if(msg.what == 0 ){
            tips.setMessage("网络连接失败").create().show();
        }
    }

    public void handleResult(int code, JSONObject data){
        switch(code){
            case 0:
                handleData(data);
                break;
            case 1:
                tips.setMessage("服务器出错, 获取信息失败").create().show();
                break;
            case -1:
                c.startActivity(new Intent(c, LoginActivity.class));
                break;
            default:
                break;
        }
    }

    public void handleData(JSONObject data){}
}