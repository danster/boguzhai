package com.boguzhai.activity.me.myauction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.SessionListAdapter;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyAuctionSessionActivity extends BaseActivity {

    private ListView listView;
    private String auctionId;
    private ArrayList<Session> sessions;
    private SessionListAdapter adapter;

    private HttpClient conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(TAG, "MyAuctionSessionActivity onCreate()");
        this.setLinearView(R.layout.me_myauctionsession);
        title.setText("拍卖会详情");

        //获取传来的拍卖会id
        auctionId = getIntent().getExtras().getString("auctionId");
        Log.i(TAG, "拍卖会id:" + auctionId);
        init();
	}

    private void init() {
        listView = (ListView) findViewById(R.id.me_myauctionsession);
        sessions = new ArrayList<>();
        requestData();
    }


    private void requestData() {
        conn = new HttpClient();
        Log.i(TAG, "sessionId" + Variable.getSessionId());
        conn.setUrl(Constant.url + "pMainAction!getAuctionMainById.htm");
        conn.setParam("auctionMainId", auctionId);
        new Thread(new HttpPostRunnable(conn, new MyAuctionSessionHandler())).start();
    }
    
    @Override
	public void onClick(View view) {
        super.onClick(view);
    }


    private class MyAuctionSessionHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case -1:
                    Utility.gotoLogin();
                break;
                case 1:
                    Toast.makeText(Variable.app_context, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    try {
                        JSONArray array = data.getJSONObject("auctionMain").getJSONArray("auctionSessionList");
                        for(int i = 0; i < array.length(); i++) {
                            sessions.add(Session.parseJson(array.getJSONObject(i)));
                        }
                        adapter = new SessionListAdapter(context, sessions, Variable.currentAuction);
                        listView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;

            }
        }
    }
}
