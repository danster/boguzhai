package com.boguzhai.activity.pay;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.utils.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class PayResultActivity extends BaseActivity {

    private TextView result_tips, count_info;
    private ImageView result_icon;
    private LinearLayout result_info, info_1, info_2, info_3, ly_count;

    private int time;
    private TextView count;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.pay_order_result);
        title.setText("支付结果");
        title_left.setVisibility(View.INVISIBLE);
        init();
    }

    public void init(){
        result_info = (LinearLayout)findViewById(R.id.result_info);
        result_tips = (TextView)findViewById(R.id.result_tips);
        count_info = (TextView)findViewById(R.id.count_info);
        result_icon = (ImageView)findViewById(R.id.result_icon);
        info_1 = (LinearLayout)findViewById(R.id.info_1);
        info_2 = (LinearLayout)findViewById(R.id.info_2);
        info_3 = (LinearLayout)findViewById(R.id.info_3);
        ly_count = (LinearLayout)findViewById(R.id.ly_count);

        info_1.setVisibility(View.GONE);
        info_2.setVisibility(View.GONE);
        info_3.setVisibility(View.GONE);
        ly_count.setVisibility(View.GONE);


        String result = getIntent().getStringExtra("result");
        String tips = getIntent().getStringExtra("tips");

        switch ( result ){
            case "1" : // 成功
                result_info.setVisibility(View.VISIBLE);
                result_icon.setBackgroundResource(R.drawable.pay_succeed);
                result_tips.setText(tips);
                break;
            case "0" : // 失败
                result_info.setVisibility(View.GONE);
                result_icon.setBackgroundResource(R.drawable.pay_failed);
                result_tips.setText(tips);
                break;
        }

        // time 秒后跳转
        time = 5;
        count = (TextView)findViewById(R.id.count);
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        if (time <= 0) {
                            count.setText("0");
                            cancel();
                            Utility.gotoMainpage(3);
                        } else {
                            count.setText(""+time);
                        }
                        time--;
                    }
                });
            }
        };
        new Timer().schedule(task, 0, 1000); // 一秒后启动task
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                task.cancel();
                finish();
                break;
        }
        return true;
    }

}

