package com.boguzhai.activity.me.info;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.thread.BaseHttpHandler;

import org.json.JSONObject;

/**
 * Created by danster on 4/9/15.
 */
class UpdateInfoHandler extends BaseHttpHandler {
    public TextView tv;
    public EditText input;

    public UpdateInfoHandler(Context c, EditText input, TextView tv) {
        super(c);
        this.input = input;
        this.tv = tv;
    }

    @Override
    public void handleResult(int code, JSONObject data){
        switch(code){
            case 0:
                this.tv.setText(this.input.getText().toString());
                break;
            case 1:
                tips.setMessage("服务器出错, 获取信息失败").create().show();
                break;
            case -1:
                this.c.startActivity(new Intent(this.c, LoginActivity.class));
                break;
            default:
                break;
        }
    }
}
