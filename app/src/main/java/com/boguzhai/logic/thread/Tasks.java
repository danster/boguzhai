package com.boguzhai.logic.thread;

import android.graphics.Bitmap;

import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.HttpClient;

/**
 * Created by danster on 5/6/15.
 * Tasks类主要包含一些网络连接的任务
 */
public class Tasks {

    public static void uploadImage(String type, Bitmap bitmap, HttpJsonHandler handler){
        HttpClient conn = new HttpClient();
        conn.setHeader("sessionid", Variable.account.sessionid);
        conn.setParam("type", type);
        conn.setParamBitmap("file", bitmap);
        conn.setUrl(Constant.url.replace("/phones/","/") + "fileUploadAction!uploadImage.htm");
        new Thread(new HttpPostRunnable(conn, handler)).start();
    }
}
