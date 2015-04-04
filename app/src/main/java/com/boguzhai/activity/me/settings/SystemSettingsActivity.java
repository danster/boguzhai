package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class SystemSettingsActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_settings);
        title.setText("系统设置");
        init();
	}

	protected void init(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
