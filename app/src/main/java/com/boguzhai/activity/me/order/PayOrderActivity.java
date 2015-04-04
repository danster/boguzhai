package com.boguzhai.activity.me.order;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class PayOrderActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_payorder);
        title.setText("结算交割");
        initView();
	}

	protected void initView(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
