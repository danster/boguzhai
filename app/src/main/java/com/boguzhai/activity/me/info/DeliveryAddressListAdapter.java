package com.boguzhai.activity.me.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;

import java.util.ArrayList;

public class DeliveryAddressListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<DeliveryAddress> list;

	public DeliveryAddressListAdapter(Context context, ArrayList<DeliveryAddress> list) {
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
            view = inflater.inflate(R.layout.me_myinfo_addr_item, null);
            holder.info = (TextView) view.findViewById(R.id.info);
            view.setTag(holder);
        } else {    
            holder = (ViewHolder) view.getTag();
        }

        holder.info.setText(list.get(position).toString());
        view.setOnClickListener(new MyOnClickListener(position));

        return view;
    }  

    public final class ViewHolder {
        public TextView info;
    }
    
    protected class MyOnClickListener implements View.OnClickListener{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
            Intent intent = new Intent(context, DeliveryAddressEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", list.get(position));
            intent.putExtras(bundle);

            // 1 代表去往编辑收货地址页面
            ((DeliveryAddressManageActivity)context).startActivityForResult(intent,1);
		}
    }
}  