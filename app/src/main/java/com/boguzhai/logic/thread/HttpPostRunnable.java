package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.NetworkApi;

public class HttpPostRunnable implements Runnable{
    private HttpClient conn;
    private Handler handler;

    public HttpPostRunnable(HttpClient conn, Handler handler){
        this.conn = conn;
        this.handler = handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        if(!NetworkApi.isNetConnected(Variable.app_context)){
            handler.obtainMessage(1,"没有检测到网络, 请检查您的网络").sendToTarget();
            return;
        }
        conn.post();
        if( conn.getResponseEntity() != null)
            handler.obtainMessage(0, conn.responseToString()).sendToTarget();
        else
            handler.obtainMessage(9,"网络连接出错").sendToTarget();

    }
}

