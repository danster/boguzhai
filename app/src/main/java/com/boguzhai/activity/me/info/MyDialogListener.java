package com.boguzhai.activity.me.info;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpRequestApi;

/**
 * Created by danster on 4/9/15.
 */
class MyDialogListener implements DialogInterface.OnClickListener {
    private Context context;
    private EditText input;
    private TextView info_tv;

    public MyDialogListener(Context context, EditText input, TextView info_tv) {
        this.context = context;
        this.input = input;
        this.info_tv = info_tv;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        HttpRequestApi conn = new HttpRequestApi();
        conn.addParam("password", this.input.getText().toString());
        conn.setUrl("http://www.88yuding.com/api.jhtml?m=login");

        UpdateInfoHandler handler = new UpdateInfoHandler(this.context, this.input, this.info_tv);
        new Thread(new HttpPostRunnable(conn,handler)).start();
        this.info_tv.setText(this.input.getText().toString());
    }
}
