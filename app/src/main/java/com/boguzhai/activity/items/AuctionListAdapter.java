package com.boguzhai.activity.items;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;

public class AuctionListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Auction> list;

	public AuctionListAdapter(Context context, ArrayList<Auction> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;    
        if (convertView == null) { 
            holder = new ViewHolder();    
            convertView = inflater.inflate(R.layout.item_list_auction, null);
            holder.auctionType = (TextView) convertView.findViewById(R.id.auction_type);
            holder.auctionName = (TextView) convertView.findViewById(R.id.auction_name);
            holder.sessionList = (ListViewForScrollView)convertView.findViewById(R.id.session_list);
            convertView.setTag(holder); 
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }
        holder.auctionType.setText(list.get(position).type);
        holder.auctionName.setText(list.get(position).name);

        ArrayList<Session> sessionList = list.get(position).sessionList;
        SessionListAdapter adapter = new SessionListAdapter(context,sessionList,list.get(position));
        holder.sessionList.setAdapter(adapter);

        return convertView;    
    }  

    public final class ViewHolder {  
        public TextView auctionType, auctionName;
        public ListViewForScrollView sessionList;
    }

}  