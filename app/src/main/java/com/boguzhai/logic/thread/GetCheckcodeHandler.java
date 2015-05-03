package com.boguzhai.logic.thread;

import android.widget.Toast;

import com.boguzhai.activity.base.Variable;

import org.json.JSONObject;

public class GetCheckcodeHandler extends HttpJsonHandler {

    public GetCheckcodeHandler(){

    }

    @Override
    public void handlerData(int code, JSONObject data){
        switch(code){
            case 0:
                Toast.makeText(Variable.app_context,"发送验证码成功，请注意查收", Toast.LENGTH_SHORT).show();

                break;
            default:
                Toast.makeText(Variable.app_context,"发送验证码失败，请重新获取", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}