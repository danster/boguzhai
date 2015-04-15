package com.boguzhai.activity.me.info;

import android.os.Bundle;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class AccountInfoMoreActivity extends BaseActivity {
	protected TextView tele, fax, QQ, address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_info_more);
        title.setText("更多个人信息");

        tele = (TextView)findViewById(R.id.tele);
        fax = (TextView)findViewById(R.id.fax);
        QQ = (TextView)findViewById(R.id.QQ);
        address = (TextView)findViewById(R.id.address);

	}

}
