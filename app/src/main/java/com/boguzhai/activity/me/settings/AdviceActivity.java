package com.boguzhai.activity.me.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.view.XListView;

import org.json.JSONObject;

public class AdviceActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener {

    public final String TAG = "AdviceActivity";

    private HttpClient conn;
    private int number = 1;//分页序号
    private int totalCount = 0;//结果总数
    private int size = 0;//
    private int currentCount = 0;//当前总数
    private Button btn_advice_add;
    private XListView lv;
    private SwipeRefreshLayout swipe_layout_advices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_advice);
        init();
    }

    private void init() {
        lv = (XListView) findViewById(R.id.lv_advices);
        swipe_layout_advices = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_advices);
        btn_advice_add = (Button) findViewById(R.id.btn_advice_add);
        listen(btn_advice_add);

        swipe_layout_advices.setColorSchemeColors(R.color.gold);
        swipe_layout_advices.setOnRefreshListener(this);

        lv.setPullLoadEnable(true);
        lv.setXListViewListener(this);


    }

    /**
     * 请求网络数据
     */
    public void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientInfoAction!getAdviceList.htm");
        conn.setParam("number", String.valueOf(number));
        new Thread(new HttpPostRunnable(conn, new AdvicesHandler())).start();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_advice_add:
                startActivity(new Intent(AdviceActivity.this, AddAdviceActivity.class));
            break;
        }
    }



    @Override
    public void onRefresh() {
        Log.i(TAG, "刷新");
        number = 0;
        requestData();
    }

    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        number++;
        requestData();
    }

    private class AdvicesHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(AdviceActivity.this, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(AdviceActivity.this, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdviceActivity.this, LoginActivity.class));
                    break;
                case 0:
                    break;
            }
        }
    }
}
