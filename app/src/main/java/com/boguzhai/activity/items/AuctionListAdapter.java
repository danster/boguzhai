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
import com.boguzhai.logic.utils.ListViewForScrollView;

import java.util.ArrayList;

public class AuctionListAdapter extends BaseAdapter {
    private final String TAG = "LotListAdapter";
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
        //Log.i(TAG,"now position" + position);
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
        holder.auctionType.setText("同步");
        holder.auctionName.setText("2015博古斋新年大拍");

        ArrayList<Session> session_list = new ArrayList<Session>();
        for(int i=0; i<2; i++){
            Session session = new Session();
            session_list.add(session);
        }

        SessionListAdapter adapter = new SessionListAdapter(context, session_list);
        holder.sessionList.setAdapter(adapter);

        return convertView;    
    }  

    public final class ViewHolder {  
        public TextView auctionType, auctionName;
        public ListViewForScrollView sessionList;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {

		}
    }


}  