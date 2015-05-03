package com.boguzhai.logic.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.HttpClient;

public abstract class HttpBaseHandler extends Handler {
    public Context context = null; //需要被子类继承使用
    public HttpBaseHandler(){super();}
    public HttpBaseHandler(Context context){ this(); this.context = context;}
    public void handlerData(HttpClient conn){};

    @Override
    public void handleMessage(Message msg) {
        if(msg == null) return;
        switch (msg.what){
            // OK
            case 0:
                handlerData((HttpClient)msg.obj);
                break;

            // 没有检测到网络
            case 1:
                Toast.makeText(Variable.app_context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                // 这里是直接打开设置页面，但应该首先得到用户确认，否则不友好，须改正
                /*
                String sdkVersion = android.os.Build.VERSION.SDK;
                Intent intent;
                if(Integer.valueOf(sdkVersion) > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(comp);
                    intent.setAction("android.intent.action.VIEW");
                }
                mContext.startActivity(intent);
                */
                break;

            // 网络连接出错, 连接状态不是200
            case 9:
                break;
            default:
                break;
        }
    }
}
