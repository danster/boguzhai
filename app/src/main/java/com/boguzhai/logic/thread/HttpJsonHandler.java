package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
                    JSONObject data = null;
                    if(result.has("data")){
                        data = result.getJSONObject("data");
                    }
                    handlerData(result.getInt("code"), data);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                break;
            default:
                if(Variable.app_context==null) { break;}
                if(msg.obj==null){ break;}

                Utility.toastMessage((String)msg.obj);
                break;
        }
    }
}
