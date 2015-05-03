package com.boguzhai.logic.thread;

import android.widget.ImageView;

import com.boguzhai.logic.utils.HttpClient;

public class ShowImageHandler extends HttpBaseHandler {
    private ImageView imageView;
    public ShowImageHandler( ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    public void handlerData(HttpClient conn) {
        if (conn.responseToBitmap() != null) {
            imageView.setImageBitmap(conn.responseToBitmap());
        }
    }

}