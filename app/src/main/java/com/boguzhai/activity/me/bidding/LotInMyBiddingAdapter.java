package com.boguzhai.activity.me.bidding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.me.myauction.BaseFragment;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.BiddingLot;

import java.util.ArrayList;

/**
 * Created by bobo on 15/4/8.
 */
public class LotInMyBiddingAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<BiddingLot> mLotList;
    private LayoutInflater inflater;
    private String[] isLeader = {"出局", "领先"};
    public LotInMyBiddingAdapter(Context context, ArrayList<BiddingLot> lotList){
        inflater = LayoutInflater.from(context);
        mContext = context;
        mLotList = lotList;
    }

    private final class ViewHolder{
        TextView my_bidding_lot_name,
                 my_bidding_lot_isleader,
                 my_bidding_lot_no,
                 my_bidding_lot_pricecount,
                 my_bidding_lot_appraisal,
                 my_bidding_lot_startprice,
                 my_bidding_lot_nowprice,
                 my_bidding_lot_topprice;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = inflater.inflate(R.layout.my_bidding_item_lot, null);
            holder = new ViewHolder();
            holder.my_bidding_lot_name = (TextView) view.findViewById(R.id.my_bidding_lot_name);
            holder.my_bidding_lot_isleader = (TextView) view.findViewById(R.id.my_bidding_lot_isleader);
            holder.my_bidding_lot_no = (TextView) view.findViewById(R.id.my_bidding_lot_no);
            holder.my_bidding_lot_pricecount = (TextView) view.findViewById(R.id.my_bidding_lot_pricecount);
            holder.my_bidding_lot_appraisal = (TextView) view.findViewById(R.id.my_bidding_lot_appraisal);
            holder.my_bidding_lot_startprice = (TextView) view.findViewById(R.id.my_bidding_lot_startprice);
            holder.my_bidding_lot_nowprice = (TextView) view.findViewById(R.id.my_bidding_lot_nowprice);
            holder.my_bidding_lot_topprice = (TextView) view.findViewById(R.id.my_bidding_lot_topprice);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.my_bidding_lot_name.setText(mLotList.get(position).name);
        holder.my_bidding_lot_isleader.setText(isLeader[mLotList.get(position).isLeader]);
        holder.my_bidding_lot_no.setText(String.valueOf(mLotList.get(position).No));
        holder.my_bidding_lot_pricecount.setText(String.valueOf(mLotList.get(position).priceCount));
        holder.my_bidding_lot_appraisal.setText(mLotList.get(position).apprisal1 + "-" + mLotList.get(position).apprisal2);
        holder.my_bidding_lot_startprice.setText(String.valueOf(mLotList.get(position).startPrice));
        holder.my_bidding_lot_nowprice.setText(String.valueOf(mLotList.get(position).nowPrice));
        holder.my_bidding_lot_topprice.setText(String.valueOf(mLotList.get(position).topPrice));


        return view;
    }

    @Override
    public int getCount() {
        return mLotList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
