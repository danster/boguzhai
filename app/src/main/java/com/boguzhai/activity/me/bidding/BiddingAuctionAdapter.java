package com.boguzhai.activity.me.bidding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.BiddingLot;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobo on 15/4/7.
 */
public class BiddingAuctionAdapter extends BaseAdapter {

    private Context mContext;
    private List<BiddingAuction> biddingAuctionList;
    private LayoutInflater inflater;

    private String[] type = {"现场", "同步", "网络"};

    private final class ViewHolder {
        TextView auction_type;
        TextView auction_name;
        TextView bidding_count;
        ListViewForScrollView lotList;

    }

    public BiddingAuctionAdapter(Context context, List<BiddingAuction> list) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        biddingAuctionList = list;

    }

    ;

    @Override
    public int getCount() {
        return biddingAuctionList.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.me_bidding_item_list, null);
            holder = new ViewHolder();
            holder.auction_type = (TextView) view.findViewById(R.id.my_bidding_auction_type);
            holder.auction_name = (TextView) view.findViewById(R.id.my_bidding_auction_name);
            holder.bidding_count = (TextView) view.findViewById(R.id.my_bidding_count);
            holder.lotList = (ListViewForScrollView) view.findViewById(R.id.my_bidding_lot_list);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.auction_type.setText(biddingAuctionList.get(position).auction.type);
        holder.auction_name.setText(biddingAuctionList.get(position).auction.name);
        holder.bidding_count.setText(String.valueOf(biddingAuctionList.get(position).auction.dealCount));



        LotInBiddingAuctionAdapter adapter = new LotInBiddingAuctionAdapter(mContext, biddingAuctionList.get(position).lotList);
        holder.lotList.setAdapter(adapter);
        return view;
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
