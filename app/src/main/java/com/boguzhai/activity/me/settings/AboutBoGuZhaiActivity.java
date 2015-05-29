package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class AboutBoGuZhaiActivity extends BaseActivity {


    private RadioGroup rg;
    private RadioButton rb;
    private WebView wv;
//    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_about_bo_gu_zhai);
        title.setText("关于博古斋");
        init();
    }

    private void init() {
        rg = (RadioGroup) findViewById(R.id.rg_about_bo_gu_zhai);
        rb = (RadioButton) findViewById(R.id.rb_about_bo_gu_1);
        wv = (WebView) findViewById(R.id.wv_about_bo_gu_zhai);
//        tv = (TextView) findViewById(R.id.tv_about_bo_gu_zhai);
        rb.setChecked(true);

        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.loadUrl("http://test.shbgz.com/otherAction!about.htm?target=2_0_8");
//        tv.setText("公司简介");
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_about_bo_gu_1:
//                        tv.setText("公司简介");
                        wv.loadUrl("http://60.191.203.80/otherAction!about.htm");
                        break;
                    case R.id.rb_about_bo_gu_2:
//                        tv.setText("联系我们");
                        wv.loadUrl("http://60.191.203.80/otherAction!about.htm");
                        break;
                    case R.id.rb_about_bo_gu_3:
//                        tv.setText("业务范围");
                        wv.loadUrl("http://60.191.203.80/otherAction!about.htm");
                        break;
                    case R.id.rb_about_bo_gu_4:
//                        tv.setText("诚聘英才");
                        wv.loadUrl("http://60.191.203.80/otherAction!about.htm");
                        break;
                }
            }
        });

    }

}
