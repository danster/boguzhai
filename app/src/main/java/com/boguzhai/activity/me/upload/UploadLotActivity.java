package com.boguzhai.activity.me.upload;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class UploadLotActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_upload);
        title.setText("上传拍品");
        initView();
	}

	protected void initView(){

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
