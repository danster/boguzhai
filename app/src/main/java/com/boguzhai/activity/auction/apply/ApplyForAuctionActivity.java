package com.boguzhai.activity.auction.apply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class ApplyForAuctionActivity extends BaseActivity {

    private TextView name, normal_money, person_id, mobile, special_money;
    private ImageView special_money_switch;
    private Boolean add_special_money=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_apply_for_auction);
        title.setText("申请参拍");
        title_right.setText("下一步");
        title_right.setVisibility(View.VISIBLE);
        init();
    }

    public void init(){
        name = (TextView)findViewById(R.id.name);
        normal_money = (TextView)findViewById(R.id.normal_money);
        person_id = (TextView)findViewById(R.id.person_id);
        mobile = (TextView)findViewById(R.id.mobile);
        special_money = (TextView)findViewById(R.id.special_money);
        special_money_switch = (ImageView)findViewById(R.id.special_money_switch);

        // 设置显示值


        showSpecialMoney();
        int[] ids={R.id.special_money_switch, R.id.show_special_lots};
        this.listen(ids);
    }

    public void showSpecialMoney(){
        if(add_special_money){
            findViewById(R.id.special_money_switch).setBackgroundResource(R.drawable.base_choose_on);
            findViewById(R.id.special_money_bar).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.special_money_switch).setBackgroundResource(R.drawable.base_choose_off);
            findViewById(R.id.special_money_bar).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                startActivity(new Intent(this, PayBailActivity.class));
                break;
            case R.id.show_special_lots:
                startActivity(new Intent(this, ShowSpecialLotsActivity.class));
                break;
            case R.id.special_money_switch:
                add_special_money = add_special_money == true ? false : true;
                showSpecialMoney();
                break;
            default:
                break;
        }
    }

}
