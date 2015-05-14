package com.boguzhai.activity.items;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
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
import com.boguzhai.logic.thread.Tasks;

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

    public LotListAdapter(Context context, ArrayList<Lot> list, boolean isMain) {
        this(context,list);
        this.isMain = isMain;
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

        // 下载并加载左侧拍品信息
        if(list.get(2 * position).isLoaded == false){
            Tasks.showImage(list.get(2 * position).imageUrl, holder.leftImage);
            holder.leftLotName.setText(list.get(2*position).name);
            holder.leftLotID.setText("拍品号: "+list.get(2*position).id);
            holder.leftLotApprisal.setText("预估价: ￥"+list.get(2*position).appraisal1+" - ￥"+list.get(2*position).appraisal2);
            holder.leftLotStartPrice.setText("起拍价: ￥"+list.get(2 * position).startPrice);
            holder.leftLot.setOnClickListener(new MyOnClickListener(2 * position));

            list.get(2 * position).isLoaded = true;
        }


        holder.rightLot.setVisibility(View.VISIBLE);
        if(position*2+2 > list.size()){
            holder.rightLot.setVisibility(View.INVISIBLE);
        } else {
            // 下载并加载右侧拍品信息
            if(list.get(2*position+1).isLoaded == false){
                Tasks.showImage(list.get(2*position+1).imageUrl, holder.rightImage);
                holder.rightLot.setOnClickListener(new MyOnClickListener(2 * position + 1));
                holder.rightLotName.setText(list.get(2*position+1).name);
                holder.rightLotID.setText("拍品号: "+list.get(2*position+1).id);
                holder.rightLotApprisal.setText("预估价: ￥"+list.get(2*position+1).appraisal1+" - ￥"+list.get(2*position+1).appraisal2);
                holder.rightLotStartPrice.setText("起拍价: ￥"+list.get(2*position+1).startPrice);

                list.get(2*position+1).isLoaded = true;
            }

        }

        if(isMain){
            ViewGroup.LayoutParams params = holder.leftLotName.getLayoutParams();
            params.height = 40;
            holder.leftLotName.setLayoutParams(params);
            holder.rightLotName.setLayoutParams(params);

            holder.leftLotName.setGravity(Gravity.CENTER);
            holder.rightLotName.setGravity(Gravity.CENTER);
            holder.leftLotID.setVisibility(View.GONE);
            holder.rightLotID.setVisibility(View.GONE);
            holder.leftLotApprisal.setVisibility(View.GONE);
            holder.rightLotApprisal.setVisibility(View.GONE);
            holder.leftLotStartPrice.setVisibility(View.GONE);
            holder.rightLotStartPrice.setVisibility(View.GONE);
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
            Variable.currentLot = list.get(position);
            context.startActivity(new Intent(context, LotInfoActivity.class));
		}
    }


}  