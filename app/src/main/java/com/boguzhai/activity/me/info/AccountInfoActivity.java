package com.boguzhai.activity.me.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.activity.me.capital.CapitalShowActivity;
import com.boguzhai.logic.thread.HttpBaseHandler;
import com.boguzhai.logic.thread.HttpGetRunnable;
import com.boguzhai.logic.utils.HttpClient;

public class AccountInfoActivity extends BaseActivity {
    private TextView verify, capital, delivery_address;
    private HttpClient conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_myinfo);
        title.setText("个人信息");
        title_right.setText("编辑");
        title_right.setVisibility(View.VISIBLE);

        verify = (TextView)findViewById(R.id.verify);
        capital = (TextView)findViewById(R.id.capital);
        delivery_address = (TextView)findViewById(R.id.delivery_address);

        init();
	}

	protected void init(){
        ((TextView)findViewById(R.id.name)).setText(Variable.account.name);
        ((TextView)findViewById(R.id.nickname)).setText(Variable.account.nickname);
        ((TextView)findViewById(R.id.zone)).setText(Variable.account.address_1+" "+
                                                    Variable.account.address_2+" "+
                                                    Variable.account.address_3);
        ((TextView)findViewById(R.id.email)).setText(Variable.account.email);
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);


        int[] ids = { R.id.logout, R.id.my_more_contact, R.id.my_delivery_address,
                      R.id.my_capital, R.id.my_verify};
        this.listen(ids);

        conn = new HttpClient();
        conn.setUrl(Variable.account.imageUrl);
        new Thread(new HttpGetRunnable(conn, new ShowImageHandler(this))).start();
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            // 账户登出
            case R.id.logout:
                Variable.isLogin = false;
                Variable.mainTabIndex = R.id.rb_1;
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.my_more_contact:
                startActivity(new Intent(this, AccountInfoMoreActivity.class));
                break;

            case R.id.my_delivery_address:
                startActivity(new Intent(this, DeliveryAddressManageActivity.class));
                break;

            case R.id.my_capital:
                startActivity(new Intent(this, CapitalShowActivity.class));
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

    class ShowImageHandler extends HttpBaseHandler {
        public ShowImageHandler(Context context){ super(context);}
        @Override
        public void handlerData(HttpClient conn){
            if(conn.responseToBitmap() != null){
                Variable.account.image = conn.responseToBitmap();
                ((ImageView) findViewById(R.id.image)).setImageBitmap(Variable.account.image);
            } else {
                Toast.makeText(context, "无法得到网络头像", Toast.LENGTH_LONG).show();
            }
        }
    }

}


