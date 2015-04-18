package com.boguzhai.activity.me.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.BiddingLot;
import com.boguzhai.logic.utils.ListViewForScrollView;

import java.util.ArrayList;

/**
 * Created by bobo on 15/4/7.
 */
public class BiddingListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Auction> mAuctionList;
    private LayoutInflater inflater;

    private String[] type = {"现场", "同步", "网络"};

    private final class ViewHolder {
        TextView auction_type;
        TextView auction_name;
        TextView bidding_count;
        ListViewForScrollView lotList;

    }

    public BiddingListAdapter(Context context, ArrayList<Auction> auctionList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mAuctionList = auctionList;

    }

    ;

    @Override
    public int getCount() {


        return mAuctionList.size();
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
        holder.auction_type.setText(type[Integer.parseInt(mAuctionList.get(position).type) - 1]);
        holder.auction_name.setText(mAuctionList.get(position).name);
        holder.bidding_count.setText(String.valueOf(mAuctionList.get(position).dealCount));

        ArrayList<BiddingLot> lotList = new ArrayList<>();
        BiddingLot lot = new BiddingLot();
        lot.isLeader = 0;
        lot.No = 123;
        lot.name = "明代唐伯虎书法作品";
        lot.priceCount = 5;
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.nowPrice = 4000;
        lot.topPrice = 4000;
        lotList.add(lot);

        lot = new BiddingLot();
        lot.isLeader = 1;
        lot.No = 312;
        lot.name = "唐代瓷器";
        lot.priceCount = 5;
        lot.apprisal1 = 8000;
        lot.apprisal2 = 10000;
        lot.startPrice = 5000;
        lot.nowPrice = 9000;
        lot.topPrice = 8000;
        lotList.add(lot);

        LotInMyBiddingAdapter adapter = new LotInMyBiddingAdapter(mContext, lotList);
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
