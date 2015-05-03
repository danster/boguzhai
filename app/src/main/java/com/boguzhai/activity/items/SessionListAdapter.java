package com.boguzhai.activity.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.thread.HttpGetRunnable;
import com.boguzhai.logic.thread.ShowImageHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import java.util.ArrayList;

public class SessionListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private Auction auction;
    private ArrayList<Session> list;

	public SessionListAdapter(Context context, ArrayList<Session> list, Auction auction) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.auction = auction;
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
            holder.image = (ImageView)convertView.findViewById(R.id.session_image);
            holder.name = (TextView) convertView.findViewById(R.id.session_name);
            holder.previewTime = (TextView) convertView.findViewById(R.id.session_pretime);
            holder.previewLocation = (TextView) convertView.findViewById(R.id.session_prelocation);
            holder.auctionTime = (TextView) convertView.findViewById(R.id.session_time);
            holder.auctionLocation = (TextView) convertView.findViewById(R.id.session_location);
            convertView.setTag(holder); 
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }
        Session session = list.get(position);
        holder.image.setBackgroundResource(R.drawable.default_image);
        holder.name.setText(session.name);
        holder.previewTime.setText("预展:" + session.previewTime);
        holder.previewLocation.setText("地点:" + session.previewLocation);
        holder.auctionTime.setText("拍卖:" + session.auctionTime);
        holder.auctionLocation.setText("地点:" + session.auctionLocation);

        HttpClient conn = new HttpClient();
        conn.setUrl( session.imageUrl );
        new Thread(new HttpGetRunnable(conn, new ShowImageHandler(holder.image))).start();

        convertView.setOnClickListener(new MyOnClickListener(position));
        return convertView;    
    }  

    public final class ViewHolder {  
        public TextView name, previewTime, previewLocation, auctionTime, auctionLocation;
        public ImageView image;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
            Variable.currentAuction = auction;
            Variable.currentSession = list.get(position);
            Utility.gotoAuction(context, list.get(position).status);
		}
    }


}  