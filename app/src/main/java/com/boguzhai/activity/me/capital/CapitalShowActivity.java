package com.boguzhai.activity.me.capital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;

public class CapitalShowActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_capital_account);
        title.setText("我的资金账户");
        init();
	}

	protected void init(){
        ((TextView)findViewById(R.id.bank_name)).setText(Variable.account.capitalInfo.bankName);
        String info = Variable.account.capitalInfo.status.equals("0")?"绑定银行卡":"重新绑定银行卡";
        ((TextView) findViewById(R.id.bind_bank)).setText(info);
        ((TextView)findViewById(R.id.bank_number)).setText(Variable.account.capitalInfo.bankNumber);
        ((TextView)findViewById(R.id.name)).setText(Variable.account.capitalInfo.name);
        ((TextView)findViewById(R.id.balance)).setText("￥"+Variable.account.capitalInfo.balance);
        ((TextView)findViewById(R.id.bail)).setText("￥"+Variable.account.capitalInfo.bail);


        this.listen(R.id.bind_bank);
        this.listen(R.id.charge);
        this.listen(R.id.withdrawal);
        this.listen(R.id.my_balance);
        this.listen(R.id.my_bail);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()){
            case R.id.bind_bank:
                startActivity(new Intent(this, CapitalBindbankActivity.class));
                break;
            case R.id.my_balance:
                Intent intent1 = new Intent(this, CapitalDetailActivity.class);
                intent1.putExtra("type","balance");
                startActivity(intent1);
                break;
            case R.id.my_bail:
                Intent intent2 = new Intent(this, CapitalDetailActivity.class);
                intent2.putExtra("type","bail");
                startActivity(intent2);
                break;
            case R.id.charge:
                startActivity(new Intent(this, CapitalChargeActivity.class));
                break;
            case R.id.withdrawal:
                startActivity(new Intent(this, CapitalWithdrawalActivity.class));
                break;
            default: break;
		};
	}

}


