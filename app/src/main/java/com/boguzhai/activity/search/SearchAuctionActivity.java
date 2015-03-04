package com.boguzhai.activity.search;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class SearchAuctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.search_auction);
        title.setText("拍卖会查询");
    }

    @Override
    public void onClick(View v){
        super.onClick(v);

    }



}
