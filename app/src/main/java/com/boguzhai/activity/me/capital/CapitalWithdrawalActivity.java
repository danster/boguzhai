package com.boguzhai.activity.me.capital;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class CapitalWithdrawalActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setLinearView(R.layout.me_capital_withdrawal);
		title.setText("提现");
		init();
	}

	protected void init(){
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()){
			default: break;
		};
	}

}


