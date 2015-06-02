package com.boguzhai.activity.me.settings;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.utils.Utility;

public class AboutBoGuZhaiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_about_bo_gu_zhai);
        title.setText("关于博古斋");
        init();
    }

    private void init() {
        int ids[] = {R.id.about_1, R.id.about_2, R.id.about_3, R.id.about_4};
        listen(ids);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.about_1:
                Utility.openUrl("http://www.shbgz.com/otherAction!about.htm?target=2_0_8");
                break;
            case R.id.about_2:
                Utility.openUrl("http://www.shbgz.com/otherAction!contactus.htm?target=2_1_8");
                break;
            case R.id.about_3:
                Utility.openUrl("http://www.shbgz.com/otherAction!business.htm?target=2_2_8");
                break;
            case R.id.about_4:
                Utility.openUrl("http://www.shbgz.com/otherAction!recruitment.htm?target=2_3_8");
                break;
        }
    }

}
