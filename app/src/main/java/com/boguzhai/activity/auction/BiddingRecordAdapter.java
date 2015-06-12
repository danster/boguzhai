package com.boguzhai.activity.auction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;

import java.util.ArrayList;

public class BiddingRecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<BiddingRecord> list;

    public BiddingRecordAdapter(Context context, ArrayList<BiddingRecord> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.auction_active_record, null);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.no = (TextView) convertView.findViewById(R.id.number);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BiddingRecord record = list.get(position);
        holder.time.setText(record.time);
        holder.no.setText(record.no);
        holder.type.setText(record.type);
        holder.price.setText(record.price+"");

        return convertView;
    }

    public final class ViewHolder {
        public TextView time, no, type, price;
    }
}

