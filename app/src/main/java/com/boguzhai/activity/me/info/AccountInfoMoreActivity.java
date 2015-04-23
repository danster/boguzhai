package com.boguzhai.activity.me.info;

import android.os.Bundle;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;

public class AccountInfoMoreActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_more);
        title.setText("更多个人信息");

        ((TextView)findViewById(R.id.telephone)).setText(Variable.account.telephone);
        ((TextView)findViewById(R.id.fax)).setText(Variable.account.fax);
        ((TextView)findViewById(R.id.qq)).setText(Variable.account.qq);
        ((TextView)findViewById(R.id.address)).setText(Variable.account.address);
	}

}
