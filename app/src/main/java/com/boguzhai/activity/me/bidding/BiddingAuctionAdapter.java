package com.boguzhai.activity.me.bidding;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.auction.AuctionActiveActivity;
import com.boguzhai.activity.auction.LotInfoActivity;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.List;

/**
 * Created by bobo on 15/4/7.
 */
public class BiddingAuctionAdapter extends BaseAdapter {

    private Context mContext;
    private List<BiddingAuction> biddingAuctionList;
    private LayoutInflater inflater;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        holder.auction_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AuctionActiveActivity.class);
                intent.putExtra("auctionId", biddingAuctionList.get(position).auction.id);
                mContext.startActivity(intent);
            }
        });
        holder.bidding_count.setText(String.valueOf(biddingAuctionList.get(position).auction.dealNum));

        LotInBiddingAuctionAdapter adapter = new LotInBiddingAuctionAdapter(mContext, biddingAuctionList.get(position).lotList);
        holder.lotList.setAdapter(adapter);
        holder.lotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position2, long id) {
                Intent intent = new Intent(mContext, LotInfoActivity.class);
                intent.putExtra("auctionId", biddingAuctionList.get(position).lotList.get(position2).name);
                mContext.startActivity(intent);
            }
        });
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
