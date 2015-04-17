package com.boguzhai.logic.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.boguzhai.activity.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danster on 4/15/15.
 */
public abstract class HttpPostHandler extends Handler {
    public BaseActivity context;

    public HttpPostHandler(Context context){
        super();
        this.context = (BaseActivity)context;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg == null) return;
        switch (msg.what){
            case 0:
                try {
                    JSONObject result = new JSONObject((String)msg.obj);
                    int code = -9;
                    JSONObject data = null;
                    code = result.getInt("code");
                    if(result.has("data")){  data = result.getJSONObject("data");}

                    handlerData(code, data);
                } catch (JSONException ex) {
                    context.alertMessage("抱歉, 数据解析报错");
                }
                break;
            default: context.alertMessage((String)msg.obj); break;
        }
    }

    public abstract void handlerData(int code, JSONObject data);
}
