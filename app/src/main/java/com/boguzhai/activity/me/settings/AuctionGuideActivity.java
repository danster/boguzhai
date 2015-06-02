package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.utils.Utility;

public class AuctionGuideActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_auction_guide);
        title.setText("拍卖指南");
        init();
    }

    private void init() {
        int ids[] = {R.id.about_1, R.id.about_2, R.id.about_3, R.id.about_4, R.id.about_5};
        listen(ids);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.about_1:
                Utility.openUrl("http://www.shbgz.com/otherAction!sellor.htm?target=3_0_8");
                break;
            case R.id.about_2:
                Utility.openUrl("http://www.shbgz.com/otherAction!buyer.htm?target=3_1_8");
                break;
            case R.id.about_3:
                Utility.openUrl("http://www.shbgz.com/otherAction!orderbook.htm?target=3_2_8");
                break;
            case R.id.about_4:
                Utility.openUrl("http://www.shbgz.com/otherAction!autionrule.htm?target=3_3_8");
                break;
            case R.id.about_5:
                Utility.openUrl("http://www.shbgz.com/otherAction!layer.htm?target=3_4_8");
                break;
        }
    }

}
