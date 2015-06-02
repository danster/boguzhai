package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class BoGuNewsActivity extends BaseActivity {


    private RadioGroup rg;
    private RadioButton rb;
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.settings_bo_gu_news);
        setLinearView(R.layout.settings_bo_gu_news);
        title.setText("博古资讯");
        init();
    }

    private void init() {
        rg = (RadioGroup) findViewById(R.id.rg_bo_gu_news);
        rb = (RadioButton) findViewById(R.id.rb_bo_gu_news_1);
        wv = (WebView) findViewById(R.id.wv_bo_gu_news);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.loadUrl("http://test.shbgz.com/informationaction!initInformationList.htm");
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        rb.setChecked(true);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_bo_gu_news_1:
                        wv.loadUrl("http://60.191.203.80/informationaction!initInformationList.htm");
                        break;
                    case R.id.rb_bo_gu_news_2:
                        wv.loadUrl("http://60.191.203.80/informationaction!initInformationList.htm");
                        break;
                    case R.id.rb_bo_gu_news_3:
                        wv.loadUrl("http://60.191.203.80/informationaction!initInformationList.htm");
                        break;
                }
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wv.canGoBack()) {
                wv.goBack();//返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
