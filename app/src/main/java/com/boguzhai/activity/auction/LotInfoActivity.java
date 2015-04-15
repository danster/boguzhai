package com.boguzhai.activity.auction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.auction.apply.ApplyForAuctionActivity;
import com.boguzhai.activity.base.BaseActivity;

public class LotInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_lot_info);
        title.setText("拍品信息");

        title_right.setText("申请参拍");
        title_right.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                startActivity(new Intent(this, ApplyForAuctionActivity.class));
                break;

            default:
            break;
        }
    }

}
