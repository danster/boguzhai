package com.boguzhai.activity.search;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

import java.util.Calendar;

public class SearchLotActivity extends BaseActivity {

    private String[] firstLotTypes = {"全部","玉兰香苑", "张江地铁站", "金科路", "张江路", "紫薇路", "香楠小区" };
    private String[][] secondLotTypes = {{"全部"},{"全部"},{"全部"}};
    String lotType1, lotType2, lotType3;

    private TextView startTime1, startTime2;

    int auctions_status=1, bid_type=1, lot_status=1, deal_type=1;
    int clickedViewId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.search_lot);
        title.setText("拍品查询");

        this.listen(R.id.chooset_lot_type_1);
        this.listen(R.id.chooset_lot_type_2);
        this.listen(R.id.chooset_lot_type_3);
        this.listen(R.id.chooset_auction);
        this.listen(R.id.chooset_session);
        this.listen(R.id.auction_start_time_1);
        this.listen(R.id.auction_start_time_2);

        this.listenRadioGroup(R.id.radioGroup_auction_status);
        this.listenRadioGroup(R.id.radioGroup_bid_type);
        this.listenRadioGroup(R.id.radioGroup_deal_type);
        this.listenRadioGroup(R.id.radioGroup_lot_status);

        startTime1 = (TextView)findViewById(R.id.auction_start_time_1);
        startTime2 = (TextView)findViewById(R.id.auction_start_time_2);

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH)+1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        startTime1.setText(year+"."+month+"."+day);
        startTime2.setText(year+"."+month+"."+day);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        clickedViewId = v.getId();
        switch (v.getId()) {
        case R.id.chooset_lot_type_1:
            new AlertDialog.Builder(this).setSingleChoiceItems(firstLotTypes, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        ((TextView) findViewById(R.id.lot_type_1)).setText(firstLotTypes[index]);
                        lotType1 = firstLotTypes[index];
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).show();
            break;
        case R.id.auction_start_time_1:
        case R.id.auction_start_time_2:
            chooseStartTime();
        break;
        default:
        break;
        }
    }

    public void chooseStartTime(){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if( clickedViewId == R.id.auction_start_time_1) {
                    startTime1.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                }else if( clickedViewId == R.id.auction_start_time_2) {
                    startTime2.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void listenRadioGroup(int id){
        RadioGroup rg = (RadioGroup) findViewById(id);
        rg.setOnCheckedChangeListener(new MyRadioGroupListener());
    }

    class MyRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.auction_all:     auctions_status = 1; break;
                case R.id.auction_preview: auctions_status = 2; break;
                case R.id.auction_bid:     auctions_status = 3; break;

                case R.id.bid_all : bid_type = 1; break;
                case R.id.bid_live: bid_type = 2; break;
                case R.id.bid_both: bid_type = 3; break;
                case R.id.bid_net : bid_type = 4; break;

                case R.id.lot_all : lot_status = 1; break;
                case R.id.lot_deal: lot_status = 2; break;
                case R.id.lot_pass: lot_status = 3; break;

                case R.id.deal_all : deal_type = 1; break;
                case R.id.deal_live: deal_type = 2; break;
                case R.id.deal_net : deal_type = 3; break;

                default: break;
            }

            Toast.makeText(getApplicationContext(), "bid_type:" + bid_type + " lot_status:" +
                    lot_status+" deal_type:"+deal_type, Toast.LENGTH_SHORT).show();
        }
    }


}
