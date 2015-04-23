package com.boguzhai.activity.auction;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.logic.widget.RangeSeekBar;

public class LotFilterActivity extends BaseActivity {

    private TextView low_price, high_price;
    private RangeSeekBar<Integer> rangeSeekBar;

    private static final String[] list_type1={"不限","北京","上海","江苏","浙江","其他"};
    private static final String[] list_type2={"不限","南京","镇江","无锡","苏州","其他"};
    private static final String[] list_type3={"不限","玄武","鼓楼","江宁","雨花","其他"};

    private StringBuffer type1=new StringBuffer();
    private StringBuffer type2=new StringBuffer();
    private StringBuffer type3=new StringBuffer();
    private StringBuffer status=new StringBuffer();
    private StringBuffer deal_type=new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_lot_filter);
        title.setText("筛选");
        title_right.setText("重置");
        title_right.setVisibility(View.VISIBLE);

        init();
    }

    public void init(){
        low_price = (TextView)findViewById(R.id.low_price);
        high_price = (TextView)findViewById(R.id.high_price);

        // Setup the new range seek bar
        rangeSeekBar = new RangeSeekBar<Integer>(this);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                low_price.setText(minValue+"");
                high_price.setText(maxValue+"");
            }
        });

        // Add to layout
        ((LinearLayout)findViewById(R.id.range)).addView(rangeSeekBar);

        setSpinners();
        setRangeSeekBar();
        listen(R.id.ok);
    }

    public void setSpinners(){
        type1.replace(0,type1.length(),"不限");
        type2.replace(0,type2.length(),"不限");
        type3.replace(0,type3.length(),"不限");
        status.replace(0,status.length(),"不限");
        deal_type.replace(0,deal_type.length(),"不限");

        utility.setSpinner(this, R.id.type1, list_type1, type1);
        utility.setSpinner(this, R.id.type2, list_type2, type2);
        utility.setSpinner(this, R.id.type3, list_type3, type3);
        utility.setSpinner(this, R.id.status, Constant.lot_status, status);
        utility.setSpinner(this, R.id.deal_type, Constant.lot_deal_type, deal_type);
    }

    public void setRangeSeekBar(){
        // Set the range
        rangeSeekBar.setRangeValues(0, 20000);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(2000);
        low_price.setText("0");
        high_price.setText("2000");
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                setSpinners();
                setRangeSeekBar();
                break;
            case R.id.ok:
                tips.setMessage("type1: "+type1+",type2: "+type2+",type3: "+type3+",status: "+status+",deal_type: "+deal_type);
                tips.create().show();
                break;
            default:
            break;
        }
    }

}
