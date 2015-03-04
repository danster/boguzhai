package com.boguzhai.logic.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
        Log.i("HttpPostRunnable","before post");
        conn.post();
        Log.i("HttpPostRunnable","after post");
        Message msg = new Message();
        if( conn.getResponseEntity() != null){
            Log.i("HttpPostRunnable","getResponseEntity is not null");
            msg.obj = conn.responseToString(HTTP.UTF_8);
            msg.what = 1;
            handler.sendMessage(msg);
        } else {
            Log.i("HttpPostRunnable","getResponseEntity is null");
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }
}