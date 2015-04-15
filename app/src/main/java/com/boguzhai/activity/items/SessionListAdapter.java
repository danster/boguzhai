package com.boguzhai.activity.items;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.auction.AuctionActiveActivity;
import com.boguzhai.activity.auction.AuctionPreviewActivity;
import com.boguzhai.logic.dao.Session;

import java.util.ArrayList;

public class SessionListAdapter extends BaseAdapter {
    private final String TAG = "LotListAdapter";
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Session> list;

	public SessionListAdapter(Context context, ArrayList<Session> list) {
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
            convertView = inflater.inflate(R.layout.item_info_session, null);
            holder.sessionName = (TextView) convertView.findViewById(R.id.session_name);
            holder.sessionPretime = (TextView) convertView.findViewById(R.id.session_pretime);
            holder.sessionPrelocation = (TextView) convertView.findViewById(R.id.session_prelocation);
            holder.sessionTime = (TextView) convertView.findViewById(R.id.session_time);
            holder.sessionLocation = (TextView) convertView.findViewById(R.id.session_location);
            convertView.setTag(holder); 
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }

        convertView.setOnClickListener(new MyOnClickListener(position));
        return convertView;    
    }  

    public final class ViewHolder {  
        public TextView sessionName, sessionPretime, sessionPrelocation, sessionTime,
                sessionLocation;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
            if (list.get(position).status == "进行中" ){
                Intent intent = new Intent( context, AuctionActiveActivity.class);
                intent.putExtra("sessionId", list.get(position).id);
                context.startActivity(intent);
            }else{
                Intent intent = new Intent( context, AuctionPreviewActivity.class);
                intent.putExtra("sessionId", list.get(position).id);
                context.startActivity(intent);
            }

		}
    }


}  