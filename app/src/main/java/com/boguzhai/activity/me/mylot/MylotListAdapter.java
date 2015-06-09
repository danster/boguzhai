package com.boguzhai.activity.me.mylot;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.thread.LoadImageTask;
import com.boguzhai.logic.utils.Utility;

import java.util.ArrayList;

public class MylotListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<MylotItem> list;
    private boolean isOrder = false;

	public MylotListAdapter(Context context, ArrayList<MylotItem> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    public MylotListAdapter(Context context, ArrayList<MylotItem> list, boolean isOrder) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.isOrder = isOrder;
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
            view = inflater.inflate(R.layout.item_list_pay_lot, null);
            holder.name = (TextView) view.findViewById(R.id.lot_name);
            holder.number = (TextView) view.findViewById(R.id.lot_number);
            holder.dealprice = (TextView) view.findViewById(R.id.dealprice);
            holder.commission = (TextView) view.findViewById(R.id.commission);
            holder.currCommission = (TextView) view.findViewById(R.id.currCommission);
            holder.sum = (TextView) view.findViewById(R.id.sum);
            holder.lot_image = (ImageView) view.findViewById(R.id.lot_image);
            holder.choose = (ImageView) view.findViewById(R.id.choose);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(list.get(position).name);
        holder.number.setText(list.get(position).number);
        holder.dealprice.setText(list.get(position).dealPrice);
        holder.commission.setText(list.get(position).originalCommission);
        holder.commission.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        holder.currCommission.setText(list.get(position).currentCommission);
        holder.sum.setText(list.get(position).sum);
        new LoadImageTask(holder.lot_image, 5).execute(list.get(position).image); // 显示缩略图

        if(!isOrder){
            MylotItem lot = list.get(position);
            boolean isContains = false;

            for(MylotItem mylot: MyLotActivity.mylots){
                if(lot.id.equals(mylot.id)){
                    isContains = true;
                    break;
                }
            }

            if(isContains){
                holder.choose.setImageResource(R.drawable.choose_yes);
            }else{
                holder.choose.setImageResource(R.drawable.choose_no);
            }
            view.setOnClickListener(new MyOnClickListener(position));
        }


        return view;
    }  

    final class ViewHolder {
        public TextView name,number,dealprice,commission,currCommission,sum;
        public ImageView lot_image, choose;
    }

    private class MyOnClickListener implements View.OnClickListener{
        private int position;
        public MyOnClickListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            ImageView choose= (ImageView)v.findViewById(R.id.choose);
            MylotItem lot = list.get(position);
            boolean isContains = false;

            for(MylotItem mylot: MyLotActivity.mylots){
                if(lot.id.equals(mylot.id)){
                    isContains = true;
                    MyLotActivity.mylots.remove(mylot);
                    break;
                }
            }

            if(isContains){
                choose.setImageResource(R.drawable.choose_no);
            }else{
                String auctionId = lot.auctionId;
                for(MylotItem mylot: MyLotActivity.mylots){
                    if(!auctionId.equals(mylot.auctionId)){
                        Utility.alertDialog("不同拍卖会的拍品不能一起结算", null);
                        return;
                    }
                }
                MyLotActivity.mylots.add(lot);
                choose.setImageResource(R.drawable.choose_yes);
            }

        }
    }

}  