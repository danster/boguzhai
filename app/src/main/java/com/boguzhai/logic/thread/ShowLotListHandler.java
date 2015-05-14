package com.boguzhai.logic.thread;

import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.utils.JsonApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowLotListHandler extends HttpJsonHandler {
    private ArrayList<Lot> list;
    private LotListAdapter adapter;
    private MyInt order;

    public ShowLotListHandler(ArrayList<Lot> list, LotListAdapter adapter, MyInt order){
        this.list = list;
        this.adapter = adapter;
        this.order = order;
    }

    @Override
    public void handlerData(int code, JSONObject data){
        switch (code){
            case 0:
                if(order.value == -1) {    break;}
                if(order.value == 1)  {    this.list.clear();}

                try {
                    int count = Integer.parseInt(data.getString("count"));
                    int size = Integer.parseInt(data.getString("size"));

                    if ( (order.value-1)*size == count ) {   order.value = -1;  break;
                    } else if ( order.value*size > count ) { order.value = -1;
                    } else { order.value ++;}

                    this.list.addAll(JsonApi.getLotList(data));
                    this.adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}

