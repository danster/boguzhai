package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class HttpJsonHandler extends Handler {
    public HttpJsonHandler(){super();}
    public void handlerData(int code, JSONObject data){
        // sessionid 错误
        if(code == -1){
            Utility.gotoLogin();
            return;
        }
    }

    @Override
    public void handleMessage(Message msg) {
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
                    ex.printStackTrace();
                }
                break;
            default:
                if(Variable.app_context == null) {
                    Log.i("TAG", "Variable.app_context is null");
                    break;
                }

                if(msg.obj == null){
                    Log.i("TAG", "msg.obj is null");
                    break;
                }

                Toast.makeText(Variable.app_context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
