package com.boguzhai.activity.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.auction.apply.PayBailActivity;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.Record;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionActiveActivity extends BaseActivity {

    private ArrayList<Record> list;
    private XListView listview;
    private AuctionRecordAdapter adapter;

    // 参拍信息: 姓名 身份证 保证金 手机 号牌 支付状态
    private String name="", identityNumber="", money="", mobile="", biddingNo="", status="";
    // 账户是否认证  是否可以出价
    private boolean authStatus = false, biddingStatus = false;
    // 支付信息: 提示，金额，暂存款
    private String payInfo="", payMoney="", payBalance="";

    private TimerTask task;

    LinearLayout main_layout, lot_info_layout_1, lot_info_layout_2;
    ImageView lot_info_image_1, lot_info_image_2;

    TextView tips, lot_info_name_1, lot_info_name_2, lot_info_no_1, lot_info_no_2,
             lot_info_apprisal_1, lot_info_apprisal_2,
             lot_info_start_price_1, lot_info_start_price_2;
    TextView bid_info_seconds, bid_info_now_price, bid_info_add_money, bid_info_next_money,
            bid_info_min_money;
    EditText bid_info_input_money;

    private Lot currentLot=null, nextLot=null;
    private String currentLotId="", nextLotId="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScrollView(R.layout.auction_active);
        title.setText("拍卖出价");
        init();
    }

    public void init(){
        main_layout = (LinearLayout)findViewById(R.id.main_layout);
        lot_info_layout_1 = (LinearLayout)findViewById(R.id.lot_info_layout_1);
        lot_info_layout_2 = (LinearLayout)findViewById(R.id.lot_info_layout_2);
        lot_info_image_1 = (ImageView)findViewById(R.id.lot_info_image_1);
        lot_info_image_2 = (ImageView)findViewById(R.id.lot_info_image_2);
        bid_info_input_money = (EditText)findViewById(R.id.bid_info_input_money);

        tips = (TextView)findViewById(R.id.tips);

        lot_info_no_1 = (TextView)findViewById(R.id.lot_info_no_1);
        lot_info_no_2 = (TextView)findViewById(R.id.lot_info_no_2);
        lot_info_name_1 = (TextView)findViewById(R.id.lot_info_name_1);
        lot_info_name_2 = (TextView)findViewById(R.id.lot_info_name_2);
        lot_info_apprisal_1 = (TextView)findViewById(R.id.lot_info_apprisal_1);
        lot_info_apprisal_2 = (TextView)findViewById(R.id.lot_info_apprisal_2);
        lot_info_start_price_1 = (TextView)findViewById(R.id.lot_info_start_price_1);
        lot_info_start_price_2 = (TextView)findViewById(R.id.lot_info_start_price_2);

        bid_info_seconds = (TextView)findViewById(R.id.bid_info_seconds);
        bid_info_now_price = (TextView)findViewById(R.id.bid_info_now_price);
        bid_info_add_money = (TextView)findViewById(R.id.bid_info_add_money);
        bid_info_next_money = (TextView)findViewById(R.id.bid_info_next_money);
        bid_info_min_money = (TextView)findViewById(R.id.bid_info_min_money);

        if(Variable.currentAuction.type.equals("网络")){
            lot_info_layout_2.setVisibility(View.GONE);
            // 获取网络拍卖实时信息
            task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() { // UI thread
                        @Override
                        public void run() {
                            HttpClient conn = new HttpClient();
                            conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                            conn.setParam("auctionMainId", Variable.currentAuction.id);
                            conn.setParam("auctionId", Variable.currentLot.id);
                            conn.setUrl(Constant.url + "pAuctionInfoAction!getNetAuctionBiddingInfo.htm");
                            new Thread(new HttpPostRunnable(conn, new DispalyHandler())).start();
                        }
                    });
                }
            };
        } else if (Variable.currentAuction.type.equals("同步")){
            lot_info_layout_2.setVisibility(View.VISIBLE);
            // 获取同步拍卖实时信息
            task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() { // UI thread
                        @Override
                        public void run() {
                            HttpClient conn = new HttpClient();
                            conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                            conn.setParam("auctionMainId", Variable.currentAuction.id);
                            conn.setUrl(Constant.url + "pAuctionInfoAction!getBothAuctionBiddingInfo.htm");
                            new Thread(new HttpPostRunnable(conn, new DispalyHandler())).start();
                        }
                    });
                }
            };
        } else {
            finish(); // 拍卖会类型错误，回退页面
        }

        int[] ids = {R.id.lot_info_moreinfo_1, R.id.lot_info_moreinfo_2,
                     R.id.bid_info_next_money, R.id.bid_info_enter_money};
        listen(ids);
    }

    @Override
    public void onResume(){
        super.onResume();
        // new Timer().schedule(task, 0, 5000); // 立刻启动间隔5秒的task
        checkApplyStatus(); // 判断 同步拍卖会 或 网络拍卖会拍品 参拍状态
        showListView();
    }

    // 判断 同步拍卖会 或 网络拍卖会拍品 参拍状态
    private void checkApplyStatus(){
        title_right.setText("申请参拍");
        title_right.setVisibility(View.VISIBLE);

        if(Variable.isLogin == true) {
            HttpClient conn = new HttpClient();
            conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
            conn.setParam("auctionId", "");
            conn.setParam("auctionMainId", Variable.currentAuction.id);
            conn.setUrl(Constant.url + "pJoinMainAction!getApplyInfoById.htm");
            new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                @Override
                public void handlerData(int code, JSONObject data) {
                    super.handlerData(code, data);
                    authStatus = false;
                    biddingStatus = false;
                    switch (code){
                        case 0:
                            authStatus = true;
                            try {
                                status = data.getString("status");
                                name = data.getString("name");
                                money = data.getString("money");
                                mobile = data.getString("mobile");
                                biddingNo = data.getString("biddingNo");
                                identityNumber = data.getString("identityNumber");

                                // status: 0-申请已支付 1-申请未支付 2-未申请
                                if( status.equals("0") ) {
                                    biddingStatus = true;
                                    title_right.setVisibility(View.INVISIBLE);
                                } else {
                                    title_right.setVisibility(View.VISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            authStatus = false;
                            title_right.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            })).start();
        }
    }

    // 展示出价记录初始化
    public void showListView(){
        listview = (XListView) findViewById(R.id.record_list);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(false);
        list = new ArrayList<Record>();
        adapter = new AuctionRecordAdapter(this, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                if(Variable.isLogin == false) {
                    Utility.gotoLogin();
                    break;
                } else if(authStatus == false){
                    Utility.alertMessage("请先进行账户认证");
                    break;
                }

                // 申请参拍
                String[] applyInfo = new String[]{"参拍人姓名：" + name, "手机号码  ：" + mobile, "身份证号码：" + identityNumber};
                new AlertDialog.Builder(this).setTitle("申请参拍").setItems(applyInfo, null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                HttpClient conn = new HttpClient();
                                conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                                conn.setParam("auctionId", "");
                                conn.setParam("auctionMainId", Variable.currentAuction.id);
                                conn.setUrl(Constant.url + "pTraceAction!askPayDeposit.htm");

                                new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                                    @Override
                                    public void handlerData(int code, JSONObject data) {
                                        super.handlerData(code, data);
                                        switch (code) {
                                            case 0:
                                                try {
                                                    payInfo = data.getString("info");
                                                    payMoney = data.getString("money");
                                                    payBalance = data.getString("balance");

                                                    Intent intent = new Intent(context, PayBailActivity.class);
                                                    intent.putExtra("info", payInfo);
                                                    intent.putExtra("money", payMoney);
                                                    intent.putExtra("balance", payBalance);
                                                    startActivity(intent);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })).start();
                            }
                        }).show();
                break;

            case  R.id.lot_info_moreinfo_1:
                if(currentLot != null) {
                    Variable.currentLot = currentLot;
                    startActivity(new Intent(this, LotInfoActivity.class));
                }
                break;

            case  R.id.lot_info_moreinfo_2:
                if(nextLot != null){
                    Variable.currentLot = nextLot;
                    startActivity(new Intent(this, LotInfoActivity.class));
                }
                break;

            case R.id.bid_info_enter_money:
                if(biddingStatus == false){
                    Utility.alertMessage("请先申请参拍！");
                    break;
                }
                // 自主出价
                break;

            case R.id.bid_info_next_money:
                if(biddingStatus == false){
                    Utility.alertMessage("请先申请参拍！");
                    break;
                }
                // 推荐出价
                break;
            default:
            break;
        }
    }

    class DispalyHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    // 当前竞拍状态: "拍卖会未开始" "未开始" "开始" "倒计时已结束" "暂停" "正常" "成交" "流拍"
                    try {
                        switch ( data.getString("status") ){
                            case "拍卖会未开始":
                                main_layout.setVisibility(View.GONE);
                                tips.setText("当前状态：拍卖会未开始 !!!");
                                break;
                            case "未开始":
                                break;
                            case "开始":
                                break;
                            case "倒计时已结束":
                                break;
                            case "暂停":
                                break;
                            case "正常":

                                int count = Integer.parseInt(data.getString("countdown"));
                                int nowTime = Integer.parseInt(data.getString("nowTime"));
                                count = count - ( (int)((new Date()).getTime()/1000 ) - nowTime);
                                new Timer().schedule(new CountTask(bid_info_seconds, count), 0, 0); // 更新倒计时信息
                                bid_info_now_price.setText(data.getString("currentPriceForRMB"));   // 当前价格
                                bid_info_min_money.setText(data.getString("minIncrement"));  // 最小加价幅度
                                bid_info_add_money.setText(data.getString("nextIncrement")); // 下一推荐出价的加价幅度
                                bid_info_next_money.setText(data.getString("nextPrice"));    // 下一推荐出价

                                // 更新当前出价记录
                                JSONArray records = data.getJSONArray("records");
                                list.clear();
                                for(int i=0; i<records.length(); ++i){
                                    JSONArray recObj = records.getJSONArray(i);

                                    Record record = new Record();
                                    record.time = recObj.getString(0);
                                    record.no = recObj.getString(1);
                                    record.type = recObj.getString(2);
                                    record.price = recObj.getString(3);
                                    list.add(record);
                                }
                                adapter.notifyDataSetChanged();

                                String auctionId = data.getString("auctionId");
                                String nextAuctionId = data.getString("nextAuctionId");

                                if(!auctionId.equals(currentLotId)){
                                    // 获取当前拍品的详细信息
                                    HttpClient con = new HttpClient();
                                    con.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+auctionId);
                                    new Thread(new HttpPostRunnable(con, new HttpJsonHandler() {
                                        @Override
                                        public void handlerData(int code, JSONObject data) {
                                            super.handlerData(code, data);
                                            switch (code){
                                                case 0:
                                                    Lot lot = Lot.parseJson(data);
                                                    currentLot = lot;
                                                    lot_info_name_1.setText(lot.name);
                                                    lot_info_no_1.setText(lot.no);
                                                    lot_info_apprisal_1.setText("￥"+lot.appraisal1+" - ￥"+lot.appraisal2);
                                                    lot_info_start_price_1.setText("￥"+lot.startPrice);
                                                    Tasks.showImage(lot.imageUrl, lot_info_image_1, 5); // 显示图片
                                                    break;
                                                default:
                                                    break;
                                            }

                                        }
                                    })).start();
                                }

                                if(!nextAuctionId.equals(nextLotId)){
                                    // 获取下一拍品的详细信息
                                    HttpClient conn = new HttpClient();
                                    conn.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+auctionId);
                                    new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                                        @Override
                                        public void handlerData(int code, JSONObject data) {
                                            super.handlerData(code, data);
                                            switch (code){
                                                case 0:
                                                    Lot lot = Lot.parseJson(data);
                                                    nextLot = lot;
                                                    lot_info_name_2.setText(lot.name);
                                                    lot_info_no_2.setText(lot.no);
                                                    lot_info_apprisal_2.setText("￥"+lot.appraisal1+" - ￥"+lot.appraisal2);
                                                    lot_info_start_price_2.setText("￥"+lot.startPrice);
                                                    Tasks.showImage(lot.imageUrl, lot_info_image_2, 5); // 显示图片
                                                    break;
                                                default:
                                                    break;
                                            }

                                        }
                                    })).start();
                                }
                                break;
                            case "成交":
                                break;
                            case "流拍":
                                break;
                            default:
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // 简单的倒计时TASK:  用 TextView 和倒计时数值 count 初始化
    class CountTask extends TimerTask {
        private int count;
        private TextView tv;
        public CountTask(TextView tv, int count){
            this.tv = tv;
            this.count = count;
        }
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    if (count <= 0) {
                        tv.setText("0");
                        cancel();
                    } else {
                        tv.setText("" + count);
                    }
                    count --;
                }
            });
        }
    };

}
