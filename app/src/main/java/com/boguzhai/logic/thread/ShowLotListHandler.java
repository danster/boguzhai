package com.boguzhai.logic.thread;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ShowLotListHandler extends HttpJsonHandler {
    private ArrayList<Lot> list;
    private LotListAdapter adapter;
    private MyInt order;
    private ArrayList<Lot> lots;

    public ShowLotListHandler(ArrayList<Lot> list, LotListAdapter adapter, MyInt order){
        this.list = list;
        this.adapter = adapter;
        this.order = order;
    }

    @Override
    public void handlerData(int code, JSONObject data){

        Variable.currentRefresh.setRefreshing(false);
        Variable.currentListview.stopLoadMore();

        switch (code){
            case 0:
                if(order.value == -1) {
                    Utility.toastMessage("已无更多信息");
                    break;
                }
                if(order.value == 1)  {
                    this.list.clear();
                }

                try {
                    int count = Integer.parseInt(data.getString("count"));
                    int size = Integer.parseInt(data.getString("size"));

                    if ((order.value-1)*size == count ) {
                        order.value = -1;
                        Utility.toastMessage("已无更多信息");
                        break;
                    } else if (order.value*size > count ) {
                        order.value = -1;
                    } else {
                        order.value ++;
                    }

                    lots = JsonApi.getLotList(data);
                    list.addAll(lots);
                    adapter.notifyDataSetChanged();

                    // 网络批量下载拍品图片
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                BitmapFactory.Options options=new BitmapFactory.Options();
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = 5; //width，hight设为原来的 .. 分之一

                                for(Lot lot: lots){
                                    InputStream in = new URL(lot.imageUrl).openStream();
                                    lot.image = BitmapFactory.decodeStream(in,null,options);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            adapter.notifyDataSetChanged();
                        }
                    }.execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}