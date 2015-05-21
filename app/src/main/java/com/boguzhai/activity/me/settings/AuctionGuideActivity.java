package com.boguzhai.activity.me.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class AuctionGuideActivity extends BaseActivity {


    private LinearLayout ll_aution_guide_1;
    private LinearLayout ll_aution_guide_2;
    private LinearLayout ll_aution_guide_3;
    private LinearLayout ll_aution_guide_4;
    private LinearLayout ll_aution_guide_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.settings_auction_guide);
        title.setText("拍卖指南");
        init();
    }

    private void init() {
        ll_aution_guide_1 = (LinearLayout) findViewById(R.id.ll_aution_guide_1);
        ll_aution_guide_2 = (LinearLayout) findViewById(R.id.ll_aution_guide_2);
        ll_aution_guide_3 = (LinearLayout) findViewById(R.id.ll_aution_guide_3);
        ll_aution_guide_4 = (LinearLayout) findViewById(R.id.ll_aution_guide_4);
        ll_aution_guide_5 = (LinearLayout) findViewById(R.id.ll_aution_guide_5);

        listen(ll_aution_guide_1);
        listen(ll_aution_guide_2);
        listen(ll_aution_guide_3);
        listen(ll_aution_guide_4);
        listen(ll_aution_guide_5);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_aution_guide_1:
                startActivity(new Intent(AuctionGuideActivity.this, AuctionGuide1.class));
                break;
            case R.id.ll_aution_guide_2:
                startActivity(new Intent(AuctionGuideActivity.this, AuctionGuide2.class));
                break;
            case R.id.ll_aution_guide_3:
                startActivity(new Intent(AuctionGuideActivity.this, AuctionGuide3.class));
                break;
            case R.id.ll_aution_guide_4:
                startActivity(new Intent(AuctionGuideActivity.this, AuctionGuide4.class));
                break;
            case R.id.ll_aution_guide_5:
                startActivity(new Intent(AuctionGuideActivity.this, AuctionGuide5.class));
                break;
        }

        super.onClick(v);
    }
}
