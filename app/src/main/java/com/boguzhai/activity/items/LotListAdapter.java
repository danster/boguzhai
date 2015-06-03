package com.boguzhai.activity.items;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.auction.LotInfoActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Lot;

import java.util.ArrayList;

public class LotListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Lot> list;
    private boolean isMain = false;

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
    public long getItemId(int position) { return position; }
    
    @Override    
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("Adapter", "position=" + position + ", view is " + (view == null ? "null" : "not null"));
        ViewHolder holder = null;    
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_list_lot, null);
            holder.leftLot = (LinearLayout) view.findViewById(R.id.leftLot);
            holder.leftImage = (ImageView) view.findViewById(R.id.leftImage);
            holder.leftLotName = (TextView) view.findViewById(R.id.leftLotName);
            holder.leftLotID = (TextView) view.findViewById(R.id.leftLotID);
            holder.leftLotApprisal = (TextView) view.findViewById(R.id.leftLotApprisal);
            holder.leftLotStartPrice = (TextView) view.findViewById(R.id.leftLotStartPrice);
            holder.rightLot = (LinearLayout) view.findViewById(R.id.rightLot);
            holder.rightImage = (ImageView) view.findViewById(R.id.rightImage);
            holder.rightLotName = (TextView) view.findViewById(R.id.rightLotName);
            holder.rightLotID = (TextView) view.findViewById(R.id.rightLotID);
            holder.rightLotApprisal = (TextView) view.findViewById(R.id.rightLotApprisal);
            holder.rightLotStartPrice = (TextView)view.findViewById(R.id.rightLotStartPrice);
            view.setTag(holder);
        } else {    
            holder = (ViewHolder) view.getTag();
        }

        // 下载并加载左侧拍品信息
        holder.leftImage.setImageBitmap(list.get(2*position).image);
        holder.leftLotName.setText(list.get(2*position).name);
        holder.leftLotID.setText("图录号: "+list.get(2*position).no);
        holder.leftLotApprisal.setText("预估价: ￥"+list.get(2*position).appraisal1+" - ￥"+list.get(2*position).appraisal2);
        holder.leftLotStartPrice.setText("起拍价: ￥" + list.get(2 * position).startPrice);
        holder.leftLot.setOnClickListener(new MyOnClickListener(2 * position));

        holder.rightLot.setVisibility(View.VISIBLE);
        if(position*2+2 > list.size()){
            holder.rightLot.setVisibility(View.INVISIBLE);
        } else {
            // 下载并加载右侧拍品信息
            holder.rightImage.setImageBitmap(list.get(2*position+1).image);
            holder.rightLot.setOnClickListener(new MyOnClickListener(2*position + 1));
            holder.rightLotName.setText(list.get(2*position+1).name);
            holder.rightLotID.setText("图录号: "+list.get(2*position+1).no);
            holder.rightLotApprisal.setText("预估价: ￥"+list.get(2*position+1).appraisal1+" - ￥"+list.get(2*position+1).appraisal2);
            holder.rightLotStartPrice.setText("起拍价: ￥"+list.get(2*position+1).startPrice);
        }
        return view;
    }


    public final class ViewHolder {
        public LinearLayout leftLot, rightLot;
        public TextView leftLotName, rightLotName, leftLotID, rightLotID,
                leftLotApprisal, rightLotApprisal, leftLotStartPrice, rightLotStartPrice;
        public ImageView leftImage, rightImage;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
        int position;
		public MyOnClickListener(int position){
            this.position = position;
		}
		@Override
		public void onClick(View v) {
            Variable.currentLot = list.get(position);
            context.startActivity(new Intent(context, LotInfoActivity.class));
		}
    }


}  