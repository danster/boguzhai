package com.boguzhai.activity.me.mylot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;

public class MylotAuctionListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<MylotAuction> list;
	public MylotAuctionListAdapter(Context context, ArrayList<MylotAuction> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
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
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_list_pay_auction, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.bail = (TextView) view.findViewById(R.id.bail);
            holder.endtime = (TextView) view.findViewById(R.id.endtime);
            holder.info = (TextView) view.findViewById(R.id.info);
            holder.lotlist = (ListViewForScrollView)view.findViewById(R.id.list);
            view.setTag(holder);
        } else {    
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(list.get(position).name);
        holder.bail.setText("已交保证金:" + list.get(position).bail);
        holder.endtime.setText("付款截止日:"+list.get(position).endtime);
        holder.info.setText("优 惠 说 明:"+list.get(position).info);

        MylotListAdapter adapter = new MylotListAdapter(context,list.get(position).lotlist);
        holder.lotlist.setAdapter(adapter);

        return view;
    }  

    final class ViewHolder {
        public TextView name,bail,endtime,info;
        public ListViewForScrollView lotlist;

    }

}  