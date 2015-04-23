package com.boguzhai.activity.me.proxy;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.gaobo.MyAuction;
import com.boguzhai.logic.thread.HttpPostHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpRequestApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetProxyPricingActivity extends BaseActivity {

    private final String TAG = "SetProxyPricingActivity";
    private CheckBox cb_enable_proxy;
    private EditText et_set_proxy_price;
    private ImageView iv_set_proxy_clear;
    private Button btn_set_proxy_ok;
    private String auctionId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_proxy_setting);
        title.setText("设置代理出价");
        auctionId = getIntent().getExtras().getString("auctionId");
        if(TextUtils.isEmpty(auctionId)) {
            Log.i(TAG, "传入的auctionId为空！finish()");
            finish();
        }
        Log.i(TAG, "传入的auctionId为:" + auctionId);
        init();
	}

	protected void init(){
        cb_enable_proxy = (CheckBox) findViewById(R.id.cb_enable_proxy);
        et_set_proxy_price = (EditText) findViewById(R.id.et_set_proxy_price);
        iv_set_proxy_clear = (ImageView) findViewById(R.id.iv_set_proxy_clear);
        btn_set_proxy_ok = (Button) findViewById(R.id.btn_set_proxy_ok);

        iv_set_proxy_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_set_proxy_price.setText("");
            }
        });

        btn_set_proxy_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proxyPrice = et_set_proxy_price.getText().toString().trim();
                if(cb_enable_proxy.isChecked()) {
                    if(TextUtils.isEmpty(proxyPrice)) {
                        Toast.makeText(SetProxyPricingActivity.this, "您启用了代理，请设置代理价格", Toast.LENGTH_SHORT).show();
                    }else {
                        //网络请求，设置代理价格
                        Log.i(TAG, "网络请求，设置---" + auctionId +  "---的代理价格为:" +proxyPrice);
//                        setProxy("1", proxyPrice);
                    }
                }else {
                    //网络请求，取消代理
                    Log.i(TAG, "网络请求，取消---" + auctionId +  "---的代理");
//                    setProxy("0", "");
                }
            }
        });

	}

    /**
     *
     * @param useProxy
     * @param proxyPrice
     */
    private void setProxy(String useProxy, String proxyPrice) {
        HttpRequestApi conn = new HttpRequestApi();
        conn.addParam("sessionid", "");
        conn.addParam("auctionId", auctionId);
        conn.addParam("useProxy", useProxy);
        conn.addParam("proxyPrice", proxyPrice);
        conn.setUrl("url");
        new Thread(new HttpPostRunnable(conn, new MySetProxyHandler(SetProxyPricingActivity.this))).start();
    }


    private class MySetProxyHandler extends HttpPostHandler {

        public MySetProxyHandler(Context context) {
            super(context);
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(context, "网络异常，设置代理失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(context, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "设置代理成功");
                    break;
            }
        }
    }

}
