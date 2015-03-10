package com.boguzhai.activity.items;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    public int getCount() { return (list.size()+1)/2; }
    @Override    
    public Object getItem(int position) { return null; }
    @Override    
    public long getItemId(int position) { return position/2; }
    
    @Override    
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;    
        if (convertView == null) { 
            holder = new ViewHolder();    
            convertView = inflater.inflate(R.layout.item_list_lot, null);
            holder.leftLot = (LinearLayout) convertView.findViewById(R.id.leftLot);
            holder.leftImage = (ImageView) convertView.findViewById(R.id.leftImage);
            holder.leftLotName = (TextView) convertView.findViewById(R.id.leftLotName);
            holder.leftLotID = (TextView) convertView.findViewById(R.id.leftLotID);
            holder.leftLotApprisal = (TextView) convertView.findViewById(R.id.leftLotApprisal);
            holder.leftLotStartPrice = (TextView) convertView.findViewById(R.id.leftLotStartPrice);
            holder.rightLot = (LinearLayout) convertView.findViewById(R.id.rightLot);
            holder.rightImage = (ImageView) convertView.findViewById(R.id.rightImage);
            holder.rightLotName = (TextView) convertView.findViewById(R.id.rightLotName);
            holder.rightLotID = (TextView) convertView.findViewById(R.id.rightLotID);
            holder.rightLotApprisal = (TextView) convertView.findViewById(R.id.rightLotApprisal);
            holder.rightLotStartPrice = (TextView)convertView.findViewById(R.id.rightLotStartPrice);
            convertView.setTag(holder); 
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }

        System.out.println("lot list position " + position);
        holder.leftImage.setBackgroundResource(R.drawable.image);
        holder.leftLotName.setText("博古斋展示拍品 " + 2*position);
        holder.leftLot.setOnClickListener(new MyOnClickListener(2 * position));

        holder.rightLot.setVisibility(View.VISIBLE);
        if(position*2+2 > list.size()){
            holder.rightLot.setVisibility(View.INVISIBLE);
        } else {
            holder.rightLot.setOnClickListener(new MyOnClickListener(2*position+1));
            holder.rightImage.setBackgroundResource(R.drawable.image);
            holder.rightLotName.setText("博古斋展示拍品 "+(2*position+1));
        }

        return convertView;    
    }  

    public final class ViewHolder {
        public LinearLayout leftLot, rightLot;
        public TextView leftLotName, rightLotName, leftLotID, rightLotID,
                leftLotApprisal, rightLotApprisal, leftLotStartPrice, rightLotStartPrice;
        public ImageView leftImage, rightImage;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
            Intent intent = new Intent( context,  LotInfoActivity.class);
            intent.putExtra("lotId", list.get(position).id);
            context.startActivity(intent);
		}
    }
}  