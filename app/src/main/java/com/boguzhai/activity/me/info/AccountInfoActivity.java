package com.boguzhai.activity.me.info;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.thread.BaseHttpHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpRequestApi;

import org.json.JSONObject;

public class AccountInfoActivity extends BaseActivity {
    protected static final String TAG = "AccountInfoActivity";
	protected TextView name, nickname, zone, email, mobile, more_contact, verify, capital, delivery_address;
    protected ImageView photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo);
        title.setText("个人信息");

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
        name.setText("张三");
		int[] ids = { R.id.logout, R.id.my_name, R.id.my_nickname, R.id.my_zone,
                      R.id.my_email, R.id.my_mobile, R.id.my_more_contact,
                      R.id.my_verify, R.id.my_capital, R.id.my_delivery_address};
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
            startActivity(new Intent(this, MoreContactActivity.class));
            break;

        case R.id.my_verify:
            startActivity(new Intent(this, IdentityVerifyActivity.class));
            break;

        case R.id.my_name:
            updateTextViewInfo(context, name);
            break;

        case R.id.my_nickname:
            updateTextViewInfo(context, nickname);
            break;

        case R.id.my_email:
            updateTextViewInfo(context, email);
            break;

        case R.id.my_mobile:
            updateTextViewInfo(context, mobile);
            break;

        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateTextViewInfo(Context context, TextView tv, String title){
        EditText et = new EditText(context);
        et.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        et.setGravity(Gravity.CENTER | Gravity.LEFT);
        et.setText(tv.getText().toString());
        // .setIcon( android.R.drawable.ic_dialog_info)
        new AlertDialog.Builder(context).setTitle(title).setView(et).
                setPositiveButton("确定", new MyDialogListener(context, et, tv)).
                setNegativeButton("取消", null).show();
        et.selectAll();
    }

    public void updateTextViewInfo(Context context, TextView tv){
        updateTextViewInfo(context, tv, "请输入");
    }
}


class MyDialogListener implements DialogInterface.OnClickListener {
    private Context context;
    private EditText input;
    private TextView info_tv;

    public MyDialogListener(Context context, EditText input, TextView info_tv) {
        this.context = context;
        this.input = input;
        this.info_tv = info_tv;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        HttpRequestApi conn = new HttpRequestApi();
        conn.addParam("password", this.input.getText().toString());
        conn.setUrl("http://www.88yuding.com/api.jhtml?m=login");

        UpdateInfoHandler handler = new UpdateInfoHandler(this.context, this.input, this.info_tv);
        new Thread(new HttpPostRunnable(conn,handler)).start();
        this.info_tv.setText(this.input.getText().toString());
    }
}

class UpdateInfoHandler extends BaseHttpHandler {
    public TextView tv;
    public EditText input;

    public UpdateInfoHandler(Context c, EditText input, TextView tv) {
        super(c);
        this.input = input;
        this.tv = tv;
    }

    @Override
    public void handleResult(int code, JSONObject data){
        switch(code){
            case 0:
                this.tv.setText(this.input.getText().toString());
                break;
            case 1:
                tips.setMessage("服务器出错, 获取信息失败").create().show();
                break;
            case -1:
                this.c.startActivity(new Intent(this.c, LoginActivity.class));
                break;
            default:
                break;
        }
    }
}