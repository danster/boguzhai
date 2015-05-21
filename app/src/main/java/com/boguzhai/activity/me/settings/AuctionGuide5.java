package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.webkit.WebView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class AuctionGuide5 extends BaseActivity {

    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.settings_auction_guide_item_content);
        title.setText("法律法规");
        wv = (WebView) findViewById(R.id.wv_auction_guide_item_content);
        wv.loadUrl("http://60.191.203.80/otherAction!sellor.htm");
    }


}
