package com.boguzhai.activity.me.info;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.me.mylot.EditOrderActivity;

import java.util.ArrayList;

public class DeliveryAddressListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private ArrayList<DeliveryAddress> list;
    private boolean isChoose=false;
	public DeliveryAddressListAdapter(Activity activity, ArrayList<DeliveryAddress> list) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.list = list;
    }

    public DeliveryAddressListAdapter(Activity activity, ArrayList<DeliveryAddress> list, boolean isChoose) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.list = list;
        this.isChoose = isChoose;
    }

    @Override
    public int getCount() { return list.size(); }
    @Override
    public Object getItem(int position) { return list.get(position); }
    @Override
    public long getItemId(int position) { return position; }

    @Override    
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("Adapter", "position="+position+", view is "+(view==null?"null":"not null"));

        ViewHolder holder = null;    
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.me_myinfo_addr_item, null);
            holder.info = (TextView) view.findViewById(R.id.info);
            holder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(holder);
        } else {    
            holder = (ViewHolder) view.getTag();
        }

        holder.info.setText(list.get(position).toString());

        if (isChoose) {
            holder.image.setImageResource(R.drawable.choose_no);
            view.setOnClickListener(new ChooseAddressListener(position));
        } else {
            holder.image.setImageResource(R.drawable.base_arrow_right_small);
            view.setOnClickListener(new EditAddressListener(position));
        }

        return view;
    }  

    final class ViewHolder {
        public TextView info;
        public ImageView image;
    }
    
    protected class EditAddressListener implements View.OnClickListener{
		private int position;
		public EditAddressListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
            Variable.currentDeliveryAddress = list.get(position);
            activity.startActivity(new Intent(activity, DeliveryAddressEditActivity.class));
		}
    }

    protected class ChooseAddressListener implements View.OnClickListener{
        private int position;
        public ChooseAddressListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            EditOrderActivity.addressInfo = list.get(position).toString();
            EditOrderActivity.addressId = list.get(position).id;
            ((ImageView)v.findViewById(R.id.image)).setImageResource(R.drawable.choose_yes);
            activity.finish();
        }
    }
}  