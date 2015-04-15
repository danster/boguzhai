package com.boguzhai.activity.me.capital;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.activity.base.BaseActivity;

public class MyCapitalActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        title.setText("资金账户");
        init();
	}

	protected void init(){
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

}


