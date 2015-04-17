package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Message;

import com.boguzhai.logic.utils.HttpRequestApi;

import org.apache.http.protocol.HTTP;

public class HttpPostRunnable implements Runnable{
    private HttpRequestApi conn;
    private Handler handler;

    public HttpPostRunnable(HttpRequestApi conn, Handler handler){
        this.conn = conn;
        this.handler = handler;
    }

    @Override
    public void run() {
        conn.post();
        Message msg = new Message();
        if( conn.getResponseEntity() != null){
            msg.obj = conn.responseToString(HTTP.UTF_8);
            msg.what = 0;
            handler.sendMessage(msg);
        } else {
            msg.obj = "网络连接出错";
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }
}

