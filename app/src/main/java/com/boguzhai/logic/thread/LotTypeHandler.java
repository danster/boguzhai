package com.boguzhai.logic.thread;

import android.os.Message;
import android.util.Log;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.SharedKeys;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danster on 5/18/15.
 */
public class LotTypeHandler  extends HttpJsonHandler{
    @Override
    public void handlerData(int code, JSONObject data){}

    @Override
    public void handleMessage(Message msg) {
        if(msg == null) return;
        switch (msg.what){
            case 0:
                Log.i("JSON", (String) msg.obj);
                try {
                    JSONObject result = new JSONObject((String)msg.obj);
                    int code =  result.getInt("code");
                    if(code==0) {
                        Variable.settings_editor.putString(SharedKeys.lotType, (String)msg.obj);
                        Variable.settings_editor.commit();
                        Tasks.getMapLottype();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                break;
            default:
                if(Variable.app_context == null) {  break;}
                if(msg.obj == null){                break;}
                break;
        }
    }
}
