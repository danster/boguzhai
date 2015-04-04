package com.boguzhai.activity.me.collect;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class MyCollectionActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_mycollection);
        title.setText("我的收藏");
        initView();
	}

	protected void initView(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
