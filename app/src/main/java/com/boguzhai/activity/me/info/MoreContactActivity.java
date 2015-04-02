package com.boguzhai.activity.me.info;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class MoreContactActivity extends BaseActivity {
    protected static final String TAG = "AccountInfoActivity";
	protected TextView tele, fax, QQ, address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_more_contact);
        title.setText("个人信息");

        tele = (TextView)findViewById(R.id.tele);
        fax = (TextView)findViewById(R.id.fax);
        QQ = (TextView)findViewById(R.id.QQ);
        address = (TextView)findViewById(R.id.address);

        int[] ids = { R.id.my_tele, R.id.my_fax, R.id.my_QQ, R.id.my_address};
        this.listen(ids);

        setBaseEnv();
	}

	protected void setBaseEnv(){
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
        case R.id.my_tele:
            updateTextViewInfo(context, tele);
            break;
        case R.id.my_fax:
            updateTextViewInfo(context, fax);
            break;
        case R.id.my_QQ:
            updateTextViewInfo(context, QQ);
            break;
        case R.id.my_address:
            updateTextViewInfo(context, address);
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

    public void updateTextViewInfo(Context context, TextView tv) {
        updateTextViewInfo(context, tv, "请输入");
    }
}
