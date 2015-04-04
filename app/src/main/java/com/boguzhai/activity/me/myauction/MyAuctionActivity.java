package com.boguzhai.activity.me.myauction;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class MyAuctionActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_myauction);
        title.setText("我的拍卖会");
        init();
	}

	protected void init(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
