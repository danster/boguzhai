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

import java.util.ArrayList;

public class MylotListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<MylotItem> list;
	public MylotListAdapter(Context context, ArrayList<MylotItem> list) {
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

        return view;
    }  

    final class ViewHolder {
        public TextView name,number,dealprice,commission,currCommission,sum;
        public ImageView lot_image, choose;
    }

}  