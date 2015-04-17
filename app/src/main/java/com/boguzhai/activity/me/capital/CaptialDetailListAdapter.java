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
    private ArrayList<String> list;
    private String type="";

    public CaptialDetailListAdapter(Context context, ArrayList<String> list, String type) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public int getCount() { return list.size(); }
    @Override
    public Object getItem(int position) { return list.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder = null;
        String[] info = list.get(position).split(",");

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
            holder.info_1.setText(info[0]);
            holder.info_2.setText(info[1]);
            holder.info_3.setText(info[2]);
            holder.info_4.setText("收入:"+info[3]);
            holder.info_5.setText("支出:"+info[4]);
            holder.info_6.setText("余额:￥"+info[5]);
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
            holder.info_1.setText(info[0]);
            holder.info_2.setText(info[1]);
            holder.info_3.setText("资金来源:"+info[2]);
            holder.info_4.setText("金额:￥"+info[3]);
        }
        return view;
    }

    final class ViewHolder {public TextView info_1,info_2,info_3,info_4,info_5,info_6;}

}