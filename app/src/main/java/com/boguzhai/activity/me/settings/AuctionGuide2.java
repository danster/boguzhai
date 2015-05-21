package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.webkit.WebView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class AuctionGuide2 extends BaseActivity {

    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.settings_auction_guide_item_content);
        title.setText("卖家指南");
        wv = (WebView) findViewById(R.id.wv_auction_guide_item_content);
        wv.loadUrl("http://60.191.203.80/otherAction!sellor.htm");
    }


}
