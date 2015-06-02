package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private TextView check_advice_title, check_advice_content, check_advice_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.settings_check_advice);
        title.setText("查看投诉建议");
        init();
    }

    private void init() {
        check_advice_title = (TextView) findViewById(R.id.check_advice_title);
        check_advice_content = (TextView) findViewById(R.id.check_advice_content);
        check_advice_result = (TextView) findViewById(R.id.check_advice_result);
        adviceId = getIntent().getStringExtra("adviceId");
        Log.i(TAG, "adviceid:" + adviceId);
        if (!TextUtils.isEmpty(adviceId)) {
            checkAdvice();
        } else {
            Utility.alertMessage("获取数据失败");
        }
    }


    private void checkAdvice() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pProposeAction!lookAdviceById.htm");
        conn.setParam("id", adviceId);
        new Thread(new HttpPostRunnable(conn, new CheckAdvicesHandler())).start();
    }


    private class CheckAdvicesHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            super.handlerData(code, data);
            switch (code) {
                case 0:
                    //获取数据//显示投诉/建议详情
                    try {
                        String title = data.getString("title");
                        String info = data.getString("info");
                        String status = data.getString("status");
                        check_advice_title.setText(title);
                        check_advice_content.setText(info);
                        check_advice_result.setText(status);
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
}
