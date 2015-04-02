package com.boguzhai.activity.mainpage;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.search.SearchResultActivity;

public class SearchFragment extends Fragment {
    private static String TAG = "SearchFragment";
    private View view;
    private MainActivity context;
    private MyOnClickListener listener;

    private String[] firstLotTypes = {"全部","玉兰香苑", "张江地铁站", "金科路", "张江路", "紫薇路", "香楠小区" };
    private String[][] secondLotTypes = {{"全部"},{"全部"},{"全部"}};
    String lotType1, lotType2, lotType3;

    int auctions_status=1, bid_type=1, lot_status=1, deal_type=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_search, null);
        context = (MainActivity)getActivity(); //getApplicationContext()
        listener = new MyOnClickListener();

        TextView title = (TextView)view.findViewById(R.id.title_center);
        title.setText("查询");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);

        this.listen(R.id.choose_lot_type_1);
        this.listen(R.id.choose_lot_type_2);
        this.listen(R.id.choose_lot_type_3);
        this.listen(R.id.choose_auction);
        this.listen(R.id.choose_session);
        this.listen(R.id.search);

        this.listenRadioGroup(R.id.radioGroup_auction_status);
        this.listenRadioGroup(R.id.radioGroup_bid_type);
        this.listenRadioGroup(R.id.radioGroup_deal_type);
        this.listenRadioGroup(R.id.radioGroup_lot_status);

        return view;
    }

    // 监听一个 ViewId
    public void listen( int id){
        View v = view.findViewById(id);
        if ( v != null ){
            v.setOnClickListener(listener);
        }
    }

    public void listenRadioGroup(int id){
        RadioGroup rg = (RadioGroup) view.findViewById(id);
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
        }
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.choose_lot_type_1:
                    new AlertDialog.Builder(context).setSingleChoiceItems(firstLotTypes, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                ((TextView) view.findViewById(R.id.lot_type_1)).setText(firstLotTypes[index]);
                                lotType1 = firstLotTypes[index];
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
                    break;
                case R.id.search:
                    context.startActivity(new Intent( context,  SearchResultActivity.class));
                    break;
                default:
                    break;
            }
        }
    }


}