package com.boguzhai.activity.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.auction.apply.ApplyForAuctionActivity;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Record;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AuctionActiveActivity extends BaseActivity {

    private ArrayList<Record> list;
    private ListViewForScrollView listview;
    private AuctionRecordAdapter adapter;

    private boolean isBothBid = true;

    TextView auction_name, auction_type;
    LinearLayout lot_info_layout_1, lot_info_layout_2;
    ImageView lot_info_image_1, lot_info_image_2;

    TextView lot_info_special_1, lot_info_special_2, lot_info_bail_1, lot_info_bail_2,
            lot_info_name_1, lot_info_name_2, lot_info_apprisal_1, lot_info_apprisal_2,
            lot_info_start_price_1, lot_info_start_price_2, lot_info_type_1, lot_info_type_2,
            lot_info_rule_1, lot_info_rule_2;
    TextView bid_info_seconds, bid_info_now_price, bid_info_add_money, bid_info_next_money,
            bid_info_min_money, bid_info_proxy;
    EditText bid_info_input_money, input_proxy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScrollView(R.layout.auction_active);
        title.setText("同步拍卖");
        title_right.setText("申请参拍");
        title_right.setVisibility(View.VISIBLE);

        init();
    }

    public void init(){
        input_proxy = new EditText(this);
        input_proxy.setBackgroundResource(R.color.transparent);

        lot_info_layout_1 = (LinearLayout)findViewById(R.id.lot_info_layout_1);
        lot_info_layout_2 = (LinearLayout)findViewById(R.id.lot_info_layout_2);
        lot_info_image_1 = (ImageView)findViewById(R.id.lot_info_image_1);
        lot_info_image_2 = (ImageView)findViewById(R.id.lot_info_image_2);
        bid_info_input_money = (EditText)findViewById(R.id.bid_info_input_money);


        auction_name = (TextView)findViewById(R.id.auction_name);
        auction_type = (TextView)findViewById(R.id.auction_type);

        lot_info_special_1 = (TextView)findViewById(R.id.lot_info_special_1);
        lot_info_special_2 = (TextView)findViewById(R.id.lot_info_special_2);
        lot_info_bail_1 = (TextView)findViewById(R.id.lot_info_bail_1);
        lot_info_bail_2 = (TextView)findViewById(R.id.lot_info_bail_2);
        lot_info_name_1 = (TextView)findViewById(R.id.lot_info_name_1);
        lot_info_name_2 = (TextView)findViewById(R.id.lot_info_name_2);
        lot_info_apprisal_1 = (TextView)findViewById(R.id.lot_info_apprisal_1);
        lot_info_apprisal_2 = (TextView)findViewById(R.id.lot_info_apprisal_2);
        lot_info_start_price_1 = (TextView)findViewById(R.id.lot_info_start_price_1);
        lot_info_start_price_2 = (TextView)findViewById(R.id.lot_info_start_price_2);
        lot_info_type_1 = (TextView)findViewById(R.id.lot_info_type_1);
        lot_info_type_2 = (TextView)findViewById(R.id.lot_info_type_2);
        lot_info_rule_1 = (TextView)findViewById(R.id.lot_info_rule_1);
        lot_info_rule_2 = (TextView)findViewById(R.id.lot_info_rule_2);


        bid_info_seconds = (TextView)findViewById(R.id.bid_info_seconds);
        bid_info_now_price = (TextView)findViewById(R.id.bid_info_now_price);
        bid_info_add_money = (TextView)findViewById(R.id.bid_info_add_money);
        bid_info_next_money = (TextView)findViewById(R.id.bid_info_next_money);
        bid_info_min_money = (TextView)findViewById(R.id.bid_info_min_money);
        bid_info_proxy = (TextView)findViewById(R.id.bid_info_proxy);


        int[] ids = {R.id.lot_info_moreinfo_1, R.id.lot_info_moreinfo_2,
                     R.id.bid_info_next_money, R.id.bid_info_enter_money,
                     R.id.bid_info_set_proxy};



        listen(ids);
        showListView();
        makeMyScrollSmart();
    }

    private void makeMyScrollSmart() {
        ScrollView parentScrollView = (ScrollView)findViewById(R.id.parent_scroll);
        ScrollView childScrollView = (ScrollView)findViewById(R.id.child_scroll);

        parentScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, "PARENT TOUCH");
                findViewById(R.id.child_scroll).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        childScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, "CHILD TOUCH");
                // Disallow the touch request for parent scroll on touch of  child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        parentScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View __v, MotionEvent __event) {
                if (__event.getAction() == MotionEvent.ACTION_DOWN) {
                    //  Disallow the touch request for parent scroll on touch of child view
                    requestDisallowParentInterceptTouchEvent(__v, true);
                } else if (__event.getAction() == MotionEvent.ACTION_UP || __event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Re-allows parent events
                    requestDisallowParentInterceptTouchEvent(__v, false);
                }
                return false;
            }
        });
        childScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View __v, MotionEvent __event) {
                if (__event.getAction() == MotionEvent.ACTION_DOWN) {
                    //  Disallow the touch request for parent scroll on touch of child view
                    requestDisallowParentInterceptTouchEvent(__v, true);
                } else if (__event.getAction() == MotionEvent.ACTION_UP || __event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Re-allows parent events
                    requestDisallowParentInterceptTouchEvent(__v, false);
                }
                return false;
            }
        });
    }

    private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept) {
        while (__v.getParent() != null && __v.getParent() instanceof View) {
            if (__v.getParent() instanceof ScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }
    }


    // 展示出价记录
    public void showListView(){
        listview = (ListViewForScrollView) findViewById(R.id.record_list);
        list = new ArrayList<Record>();

        for(int i=0; i<9; i++){
            Record record = new Record();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date now = new Date();
            record.id = i;
            record.time = sdf.format(now);
            record.no = "N00"+(i+1);
            record.type = Math.random()>0.5 ? "网络" : "";
            record.price = 2000+500*i;
            list.add(record);
        }

        adapter = new AuctionRecordAdapter(this, list);
        listview.setAdapter(adapter);
    }


    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                startActivity(new Intent(this, ApplyForAuctionActivity.class));
                break;
            case R.id.bid_info_set_proxy:
                input_proxy.setText(bid_info_proxy.getText().toString());
                AlertDialog.Builder tips = new AlertDialog.Builder(Variable.currentActivity);
                tips.setTitle("请输入").setIcon( android.R.drawable.ic_dialog_info).setView(input_proxy)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            bid_info_proxy.setText(input_proxy.getText().toString());
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
                break;
            case  R.id.lot_info_moreinfo_1:
            case  R.id.lot_info_moreinfo_2:
                startActivity(new Intent(this, LotInfoActivity.class));
                break;
            default:
            break;
        }
    }
}
