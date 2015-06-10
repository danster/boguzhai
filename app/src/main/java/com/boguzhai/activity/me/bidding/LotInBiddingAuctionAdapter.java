package com.boguzhai.activity.me.bidding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.ImageDetailsActivity;
import com.boguzhai.logic.dao.BiddingLot;
import com.boguzhai.logic.thread.LoadImageTask;
import com.boguzhai.logic.utils.Utility;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by bobo on 15/4/8.
 */
public class LotInBiddingAuctionAdapter extends BaseAdapter {


    private Context mContext;
    private List<BiddingLot> mLotList;
    private LayoutInflater inflater;
    private boolean isShowing = false;
    private String[] isLeader = {"出局", "领先"};
    public LotInBiddingAuctionAdapter(Context context, List<BiddingLot> lotList){
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
        ImageView my_bidding_lot_image;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            holder.my_bidding_lot_image = (ImageView) view.findViewById(R.id.my_bidding_lot_image);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.my_bidding_lot_name.setText(mLotList.get(position).name);
        holder.my_bidding_lot_isleader.setText(mLotList.get(position).isLeader);
        holder.my_bidding_lot_no.setText(mLotList.get(position).no);
        holder.my_bidding_lot_pricecount.setText(String.valueOf(mLotList.get(position).biddingCount));
        holder.my_bidding_lot_appraisal.setText("￥" + mLotList.get(position).appraisal1 + "- ￥" + mLotList.get(position).appraisal2);
        holder.my_bidding_lot_startprice.setText("￥" + String.valueOf(mLotList.get(position).startPrice));
        holder.my_bidding_lot_nowprice.setText("￥" + String.valueOf(mLotList.get(position).currentPrice));
        holder.my_bidding_lot_topprice.setText("￥" + mLotList.get(position).topPrice);

        new LoadImageTask(holder.my_bidding_lot_image, 4).execute(mLotList.get(position).imageUrl);
//        holder.my_bidding_lot_image.setImageBitmap(mLotList.get(position).image);


        holder.my_bidding_lot_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isShowing) {
                    isShowing = true;
                    new AsyncTask<Void, Void, Void>() {
                        Bitmap bmp = null;
                        int imageRatio = 1;
                        String imageUrl = mLotList.get(position).imageUrl;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected void onCancelled() {
                            super.onCancelled();
                            isShowing = false;
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            if (imageUrl.equals("")) {
                                return null;
                            }
                            try {
                                Log.i("AsyncTask", "image get: " + imageUrl);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = imageRatio; // height, width 变为原来的ratio分之一
                                InputStream inputStream = new URL(imageUrl).openStream();
                                bmp = BitmapFactory.decodeStream(inputStream, null, options);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            if (bmp != null) {
                                Log.i("AsyncTask", "image get: succeed !");

                                Variable.currentBitmap = bmp;
                                Utility.gotoActivity(ImageDetailsActivity.class);
                                isShowing = false;
//                                LayoutInflater inflater = LayoutInflater.from(Variable.currentActivity);
//                                View imgEntryView = inflater.inflate(R.layout.dialog_big_photo, null); // 加载自定义的布局文件
//                                ((ImageView) imgEntryView.findViewById(R.id.large_image)).setImageBitmap(bmp); // 设置图片
//                                final AlertDialog dialog = new AlertDialog.Builder(Variable.currentActivity).create();
//                                dialog.setView(imgEntryView); // 自定义dialog
//                                dialog.show();
//
//                                // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
//                                imgEntryView.setOnClickListener(new View.OnClickListener() {
//                                    public void onClick(View paramView) {
//                                        dialog.cancel();
//                                        isShowing = false;
//                                    }
//                                });
                            } else {
                                Log.i("AsyncTask", "image get: failed !");
                            }
                        }
                    }.execute();
                }



            }
        });

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
