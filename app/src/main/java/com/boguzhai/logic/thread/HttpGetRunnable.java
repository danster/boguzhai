package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Looper;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.ServiceApi;

public class HttpGetRunnable implements Runnable{
    private HttpClient conn;
    private Handler handler;

    public HttpGetRunnable(HttpClient conn, Handler handler){
        this.conn = conn;
        this.handler = handler;
    }

    @Override
    public void run() {
        Looper.prepare();

        /**
         * handler.obtainMessage(what, obj).sendToTarget() 与 handler.dispatchMessage(msg) 相比，
         * 不会造成 "Only the original thread that created a view hierarchy can touch its views"
         * 的UI操作问题，与handler.sendMessage(msg)相比，不会造成 be killed by signal 9 .
         * And 先handler.sendMessage 再conn.get or conn.post， 操作之间是异步操作
         */

        if(!ServiceApi.isNetConnected(Variable.app_context)){
            handler.obtainMessage(1,"没有检测到网络, 请检查您的网络").sendToTarget();
            return;
        }

        conn.get();
        if( conn.getResponseEntity() != null)
            handler.obtainMessage(0,conn).sendToTarget();
        else
            handler.obtainMessage(9,"网络连接出错").sendToTarget();
    }
}

