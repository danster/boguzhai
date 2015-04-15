package com.boguzhai.activity.auction.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class PayBailActivity extends BaseActivity {

    private TextView pay_info, pay_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_apply_paybail);
        title.setText("支付保证金");

        init();
    }

    public void init(){
        pay_info = (TextView)findViewById(R.id.info);
        pay_count = (TextView)findViewById(R.id.money);

        listen(R.id.alipay);
        listen(R.id.bank_card);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.alipay:
                startActivity(new Intent(this, PayBailResultActivity.class ));
                break;
            case R.id.bank_card:
                startActivity(new Intent(this, PayBailResultActivity.class ));
                break;
            default:
                break;
        }
    }

}
