package com.boguzhai.activity.me.info;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class AccountInfoEditActivity extends BaseActivity {

    private static final String[] list_addr_1={"不限","北京","上海","江苏","浙江","其他"};
    private static final String[] list_addr_2={"不限","南京","镇江","无锡","苏州","其他"};
    private static final String[] list_addr_3={"不限","玄武","鼓楼","江宁","雨花","其他"};

    private StringBuffer addr_1=new StringBuffer();
    private StringBuffer addr_2=new StringBuffer();
    private StringBuffer addr_3=new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_edit);
        title.setText("编辑个人信息");

        init();
	}

	protected void init(){

        addr_1.replace(0,addr_1.length(),"不限");
        addr_2.replace(0,addr_2.length(),"不限");
        addr_3.replace(0,addr_3.length(),"不限");

        utility.setSpinner(this, R.id.addr_1, list_addr_1, addr_1);
        utility.setSpinner(this, R.id.addr_2, list_addr_2, addr_2);
        utility.setSpinner(this, R.id.addr_3, list_addr_3, addr_3);

		int[] ids = { R.id.my_email, R.id.my_mobile, R.id.ok, R.id.name_clear, R.id.nickname_clear,
                      R.id.tele_clear, R.id.fax_clear, R.id.QQ_clear, R.id.address_clear};
		this.listen(ids);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
        // 账户登出
		case R.id.ok:
            String name = ((EditText)findViewById(R.id.name)).getText().toString();
            String nickname = ((EditText)findViewById(R.id.nickname)).getText().toString();
            String tele = ((EditText)findViewById(R.id.tele)).getText().toString();
            String fax = ((EditText)findViewById(R.id.fax)).getText().toString();
            String QQ = ((EditText)findViewById(R.id.QQ)).getText().toString();
            String address = ((EditText)findViewById(R.id.address)).getText().toString();

            tips.setMessage("name:"+name+" nickname:"+nickname+" tele:"+tele+" fax"+fax+" QQ:"+QQ+" address:"+address).show();
			//startActivity(new Intent(this, AccountInfoActivity.class));
            break;

        case R.id.my_email:
            break;

        case R.id.my_mobile:
            break;

        case R.id.name_clear:     ((EditText)findViewById(R.id.name)).setText("");     break;
        case R.id.nickname_clear: ((EditText)findViewById(R.id.nickname)).setText(""); break;
        case R.id.tele_clear:     ((EditText)findViewById(R.id.tele)).setText("");     break;
        case R.id.fax_clear:      ((EditText)findViewById(R.id.fax)).setText("");      break;
        case R.id.QQ_clear:       ((EditText)findViewById(R.id.QQ)).setText("");       break;
        case R.id.address_clear:  ((EditText)findViewById(R.id.address)).setText("");  break;

        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void clearEditText(View v){

    }

}


