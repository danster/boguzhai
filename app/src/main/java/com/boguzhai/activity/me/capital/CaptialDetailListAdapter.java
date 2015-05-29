package com.boguzhai.activity.me.capital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;

import java.util.ArrayList;

public class CaptialDetailListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<BalanceDetail> balanceList;
    private ArrayList<BailDetail> bailList;
    private String type="";

    public CaptialDetailListAdapter(Context context, ArrayList<BalanceDetail> list1, ArrayList<BailDetail> list2, String type) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.balanceList = list1;
        this.bailList = list2;
        this.type = type;
    }

    @Override
    public int getCount() {
        if(type.equals("balance")){
            return balanceList.size();
        } else {
            return bailList.size();
        }
    }
    @Override
    public Object getItem(int position) {
        if(type.equals("balance")){
            return balanceList.get(position);
        } else {
            return bailList.get(position);
        }
    }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if(type.equals("balance")) {
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.me_capital_detail_balance, null);
                holder.info_1 = (TextView)view.findViewById(R.id.info_1);
                holder.info_2 = (TextView)view.findViewById(R.id.info_2);
                holder.info_3 = (TextView)view.findViewById(R.id.info_3);
                holder.info_4 = (TextView)view.findViewById(R.id.info_4);
                holder.info_5 = (TextView)view.findViewById(R.id.info_5);
                holder.info_6 = (TextView)view.findViewById(R.id.info_6);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            BalanceDetail detail = balanceList.get(position);
            holder.info_1.setText(detail.id);
            holder.info_2.setText(detail.type);
            holder.info_3.setText(detail.time);
            holder.info_4.setText("收入:"+detail.in);
            holder.info_5.setText("支出:"+detail.out);
            holder.info_6.setText("余额:￥"+detail.balance);
        }
        if(type.equals("bail")){
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.me_capital_detail_bail, null);
                holder.info_1 = (TextView)view.findViewById(R.id.info_1);
                holder.info_2 = (TextView)view.findViewById(R.id.info_2);
                holder.info_3 = (TextView)view.findViewById(R.id.info_3);
                holder.info_4 = (TextView)view.findViewById(R.id.info_4);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            BailDetail detail = bailList.get(position);
            holder.info_1.setText(detail.auctionMainName);
            holder.info_2.setText(detail.time);
            holder.info_3.setText("资金来源:"+detail.from);
            holder.info_4.setText("金额:￥"+detail.money);
        }
        return view;
    }

    final class ViewHolder {public TextView info_1,info_2,info_3,info_4,info_5,info_6;}

}