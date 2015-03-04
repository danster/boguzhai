package com.boguzhai.activity.mainpage;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Lot;

import java.util.ArrayList;

public class LotListAdapter extends BaseAdapter {
    private final String TAG = "LotListAdapter";
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Lot> list;

	public LotListAdapter(Context context, ArrayList<Lot> list) {
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
            convertView = inflater.inflate(R.layout.main_fg_home_lotlist, null);
            holder.leftImage = (ImageView) convertView.findViewById(R.id.leftImage);
            holder.leftName = (TextView) convertView.findViewById(R.id.leftText);
            holder.rightImage = (ImageView) convertView.findViewById(R.id.rightImage);
            holder.rightName = (TextView) convertView.findViewById(R.id.rightText);
            convertView.setTag(holder); 
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }
        holder.leftImage.setBackgroundResource(R.drawable.image);
        holder.rightImage.setBackgroundResource(R.drawable.image);
        holder.leftName.setText("博古斋展示拍品 "+(int)(Math.random()*100));
        holder.rightName.setText("博古斋展示拍品 "+(int)(Math.random()*100));
        convertView.setOnClickListener(new MyOnClickListener(position));
        return convertView;    
    }  

    public final class ViewHolder {  
        public TextView leftName, rightName;
        public ImageView leftImage, rightImage;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
//			Intent intent = new Intent( context,  MtInfoShowActivity.class);
//			intent.putExtra("meetingId", list.get(position).id);
//			context.startActivity(intent);
		}
    }


}  