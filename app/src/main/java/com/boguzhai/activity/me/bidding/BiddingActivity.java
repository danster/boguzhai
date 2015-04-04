package com.boguzhai.activity.me.bidding;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class BiddingActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_bidding);
        title.setText("正在竞价");
        init();
	}

	protected void init(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
