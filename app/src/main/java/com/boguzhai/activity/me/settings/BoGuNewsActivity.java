package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.webkit.WebView;
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
        setContentView(R.layout.settings_bo_gu_news);
        title.setText("博古资讯");
        init();
    }

    private void init() {
        rg = (RadioGroup) findViewById(R.id.rg_bo_gu_news);
        rb = (RadioButton) findViewById(R.id.rb_bo_gu_news_1);
        wv = (WebView) findViewById(R.id.wv_bo_gu_news);
        rb.setChecked(true);
        wv.loadUrl("http://test.shbgz.com/informationaction!initInformationList.htm");
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

}
