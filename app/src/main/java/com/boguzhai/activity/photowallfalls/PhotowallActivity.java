package com.boguzhai.activity.photowallfalls;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.boguzhai.R;

public class PhotowallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.item_image_wall);
	}

}
