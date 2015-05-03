package com.boguzhai.logic.thread;

import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.utils.JsonApi;

import org.json.JSONObject;

import java.util.ArrayList;

public class ShowLotListHandler extends HttpJsonHandler {
    private ArrayList<Lot> list;
    private LotListAdapter adapter;

    public ShowLotListHandler(ArrayList<Lot> list, LotListAdapter adapter){
        this.list = list;
        this.adapter = adapter;
    }

    @Override
    public void handlerData(int code, JSONObject data){
        switch (code){
            case 0:
                this.list.clear();
                this.list.addAll(JsonApi.getLotList(data));
                if (this.list.size() <= 0)
                    break;
                else {
                    this.adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
}

