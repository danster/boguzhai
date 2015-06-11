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

public class PayDepositResultActivity extends BaseActivity {

    private TextView result_tips;
    private ImageView result_icon;
    private LinearLayout result_info;

    private int time;
    private TextView count;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.pay_deposit_result);
        title.setText("支付结果");
        title_left.setVisibility(View.INVISIBLE);
        init();
    }

    public void init(){
        result_info = (LinearLayout)findViewById(R.id.result_info);
        result_tips = (TextView)findViewById(R.id.result_tips);
        result_icon = (ImageView)findViewById(R.id.result_icon);

        String result = getIntent().getStringExtra("result");
        String tips = getIntent().getStringExtra("tips");

        switch ( result ){
            case "1" :
                result_info.setVisibility(View.VISIBLE);
                result_tips.setText(tips);
                ((TextView)findViewById(R.id.info)).setText(getIntent().getStringExtra("info"));
                ((TextView)findViewById(R.id.number)).setText(getIntent().getStringExtra("biddingNO"));
                result_icon.setBackgroundResource(R.drawable.pay_succeed);
                break;
            case "0" :
                result_info.setVisibility(View.GONE);
                result_tips.setText(tips);
                result_icon.setBackgroundResource(R.drawable.pay_failed);
                break;
            default:
                result_info.setVisibility(View.GONE);
                result_tips.setText(tips);
                result_icon.setBackgroundResource(R.drawable.pay_failed);
                break;
        }

        // time 秒后跳转
        time = 10;
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
                            Utility.gotoMainpage(1);
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
