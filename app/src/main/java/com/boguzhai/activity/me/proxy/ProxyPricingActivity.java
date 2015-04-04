package com.boguzhai.activity.me.proxy;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class ProxyPricingActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_proxy);
        title.setText("代理出价");
        initView();
	}

	protected void initView(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
