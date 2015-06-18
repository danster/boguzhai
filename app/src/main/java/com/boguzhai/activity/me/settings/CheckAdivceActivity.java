package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckAdivceActivity extends BaseActivity {
    private String adviceId;
    private HttpClient conn;
    private TextView check_advice_title, check_advice_info, check_advice_status,
                     check_advice_orderId, check_advice_type ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.settings_check_advice);
        title.setText("投诉建议");
        init();
    }

    private void init() {
        check_advice_title = (TextView) findViewById(R.id.check_advice_title);
        check_advice_info = (TextView) findViewById(R.id.check_advice_info);
        check_advice_status = (TextView) findViewById(R.id.check_advice_status);
        check_advice_orderId = (TextView) findViewById(R.id.check_advice_orderId);
        check_advice_type = (TextView) findViewById(R.id.check_advice_type);


        listen(R.id.check_advice_delete);
        adviceId = getIntent().getStringExtra("adviceId");
        Log.i(TAG, "adviceid:" + adviceId);
        if (!TextUtils.isEmpty(adviceId)) {
            checkAdvice();
        } else {
            Utility.alertDialog("获取数据失败");
        }
    }


    private void deleteAdvice() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
        conn.setUrl(Constant.url + "pProposeAction!removeAdviceById.htm");
        conn.setParam("id", adviceId);
        new Thread(new HttpPostRunnable(conn, new DeleteAdvicesHandler())).start();


    }

    private void checkAdvice() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
        conn.setUrl(Constant.url + "pProposeAction!lookAdviceById.htm");
        conn.setParam("id", adviceId);
        new Thread(new HttpPostRunnable(conn, new CheckAdvicesHandler())).start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.check_advice_delete:
                deleteAdvice();
                break;
        }
    }

    private class CheckAdvicesHandler extends HttpJsonHandler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what != 0) {
                Utility.toastMessage("网络异常,请稍后重试");
            }
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            super.handlerData(code, data);
            switch (code) {
                case 0:
                    //获取数据//显示投诉/建议详情
                    try {
                        check_advice_title.setText(data.getString("title"));
                        check_advice_type.setText(data.getString("type"));
                        check_advice_orderId.setText(data.getString("orderId"));
                        check_advice_info.setText(data.getString("info"));
                        check_advice_status.setText(data.getString("status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    Utility.toastMessage("服务器出错，获取数据失败");
                    break;

            }
        }
    }


    private class DeleteAdvicesHandler extends HttpJsonHandler {


        @Override
        public void handlerData(int code, JSONObject data) {
            super.handlerData(code, data);
            switch (code) {
                case 0:
                    Utility.toastMessage("删除成功");
                    finish();
                    break;
                case 1:
                    Utility.toastMessage("服务器出错，删除失败");
                    break;

            }
        }
    }
}
