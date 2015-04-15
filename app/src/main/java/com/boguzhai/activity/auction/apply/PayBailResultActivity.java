package com.boguzhai.activity.auction.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.mainpage.MainActivity;

public class PayBailResultActivity extends BaseActivity {

    private TextView auction_info, auction_no, result_tips;
    private ImageView result_icon;
    private LinearLayout result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_apply_paybail_result);
        title.setText("支付结果");
        title_right.setText("完成");
        title_right.setVisibility(View.VISIBLE);

        init();
    }

    public void init(){
        result_info = (LinearLayout)findViewById(R.id.result_info);
        result_tips = (TextView)findViewById(R.id.result_tips);
        result_icon = (ImageView)findViewById(R.id.result_icon);
        auction_info = (TextView)findViewById(R.id.info);
        auction_no = (TextView)findViewById(R.id.number);

        boolean succeed = Math.random() > 0.5 ? true : false;
        if(succeed){
            result_info.setVisibility(View.VISIBLE);
            result_tips.setText("恭喜您，支付保证金成功！");
            result_icon.setBackgroundResource(R.drawable.pay_succeed);
        }else {
            result_info.setVisibility(View.GONE);
            result_tips.setText("抱歉，支付保证金失败！");
            result_icon.setBackgroundResource(R.drawable.pay_failed);
        }
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                App.mainTabIndex = 1;
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
            break;
        }
    }

}
