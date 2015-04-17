package com.boguzhai.activity.me.capital;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class CapitalBindbankActivity extends BaseActivity {

    private static final String[] list_bank={"中国工商银行","中国银行","中国建设银行"};
    private StringBuffer bank_name=new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_capital_bind);
        title.setText("绑定银行卡");
        init();
	}

	protected void init(){
        bank_name.replace(0, bank_name.length(), "中国工商银行");
        utility.setSpinner(this, R.id.bank_name, list_bank, bank_name);
        ((TextView)findViewById(R.id.name)).setText("张三");

        listen(R.id.get_check_code);
        listen(R.id.ok);
        listen(R.id.bank_number_clear);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
            case R.id.bank_number_clear:
                ((EditText)findViewById(R.id.bank_number)).setText("");
                break;
            case R.id.get_check_code:
                break;
            case R.id.ok:
                String bank_number = ((EditText)findViewById(R.id.bank_number)).getText().toString();
                String bank = bank_name.toString();
                String check_code = ((EditText)findViewById(R.id.check_code)).getText().toString();

                break;
            default: break;
		};
	}

}


