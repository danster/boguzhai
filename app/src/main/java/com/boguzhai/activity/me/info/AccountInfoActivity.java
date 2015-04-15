package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.activity.me.capital.MyCapitalActivity;

public class AccountInfoActivity extends BaseActivity {
	protected TextView name, nickname, zone, email, mobile, more_contact, verify, capital, delivery_address;
    protected ImageView photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo);
        title.setText("个人信息");
        title_right.setText("编辑");
        title_right.setVisibility(View.VISIBLE);

        name = (TextView)findViewById(R.id.name);
        nickname = (TextView)findViewById(R.id.nickname);
        zone = (TextView)findViewById(R.id.zone);
        email = (TextView)findViewById(R.id.email);
        mobile = (TextView)findViewById(R.id.mobile);

        more_contact = (TextView)findViewById(R.id.more_contact);
        verify = (TextView)findViewById(R.id.verify);
        capital = (TextView)findViewById(R.id.capital);
        delivery_address = (TextView)findViewById(R.id.delivery_address);

        setBaseEnv();
	}

	protected void setBaseEnv(){

        int[] ids = { R.id.logout, R.id.my_more_contact, R.id.my_delivery_address,
                      R.id.my_capital, R.id.my_verify};
        this.listen(ids);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
        // 账户登出
		case R.id.logout:
			App.isLogin = false;
			App.mainTabIndex = R.id.rb_1;
			startActivity(new Intent(this, MainActivity.class));
            break;

        case R.id.my_more_contact:
            startActivity(new Intent(this, AccountInfoMoreActivity.class));
            break;

        case R.id.my_delivery_address:
            startActivity(new Intent(this, DeliveryAddressManageActivity.class));
            break;

        case R.id.my_capital:
            startActivity(new Intent(this, MyCapitalActivity.class));
            break;

        case R.id.my_verify:
            startActivity(new Intent(this, IdentityVerifyActivity.class));
            break;

        case R.id.title_right:
            startActivity(new Intent(this, AccountInfoEditActivity.class));
            break;

        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

}


