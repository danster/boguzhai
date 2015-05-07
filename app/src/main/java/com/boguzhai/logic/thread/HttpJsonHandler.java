package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.boguzhai.activity.base.Variable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class HttpJsonHandler extends Handler {
    public HttpJsonHandler(){super();}
    public void handlerData(int code, JSONObject data){}

    @Override
    public void handleMessage(Message msg) {
        if(msg == null) return;
        switch (msg.what){
            case 0:
                Log.i("JSON",(String)msg.obj);
                try {
                    JSONObject result = new JSONObject((String)msg.obj);
                    int code = -9;
                    JSONObject data = null;
                    code = result.getInt("code");
                    if(result.has("data")){
                        data = result.getJSONObject("data");
                    }
                    handlerData(code, data);
                } catch (JSONException ex) {
                    Toast.makeText(Variable.app_context, "网络数据错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(Variable.app_context, (String) msg.obj, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
