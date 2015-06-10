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
import com.boguzhai.activity.me.info.IdentityVerifyActivity;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.Record;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.LoadImageTask;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionActiveActivity extends BaseActivity {

    private ArrayList<Record> list;
    private ListViewForScrollView listview;
    private AuctionRecordAdapter adapter;

    // 参拍信息: 姓名 身份证 保证金 手机 号牌 支付状态
    private String name="", identityNumber="", money="", mobile="", biddingNo="", status="";
    // 账户是否认证  是否可以出价
    private boolean authStatus = false, biddingStatus = false;
    // 支付信息: 提示，金额，暂存款
    private String payInfo="", payMoney="", payBalance="";

    // 竞拍大厅信息，当前拍品，下一拍品，出价页面
    LinearLayout ly_lot1, ly_lot2, ly_price;
    ImageView lot_image1, lot_image2;

    TextView lot_status, lot_name1, lot_name2, lot_no1, lot_no2, lot_apprisal1, lot_apprisal2,
             lot_startprice1, lot_startprice2;

    TextView tips;
    TextView bid_seconds, bid_now_price, bid_next_price, bid_min_inc, bid_biddingNo;
    EditText bid_input_money;

    private Lot currentLot=null, nextLot=null;
    private String currentLotId="", nextLotId="", nextPrice="";

    // 获取同步拍卖实时信息,倒计时
    private TimerTask timerTask = null ;
    private CountTask countTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScrollView(R.layout.auction_active);
        title.setText("拍卖出价");
        init();
    }

    public void init(){
        ly_price = (LinearLayout)findViewById(R.id.price);
        tips = (TextView)findViewById(R.id.tips);
        ly_lot1 = (LinearLayout)findViewById(R.id.lot_info_layout_1);
        ly_lot2 = (LinearLayout)findViewById(R.id.lot_info_layout_2);
        findViewById(R.id.ly_main).setVisibility(View.GONE);
        ly_lot2.setVisibility(View.GONE);

        lot_image1 = (ImageView)findViewById(R.id.lot_info_image_1);
        lot_image2 = (ImageView)findViewById(R.id.lot_info_image_2);
        lot_status = (TextView)findViewById(R.id.lot_status);
        lot_no1 = (TextView)findViewById(R.id.lot_info_no_1);
        lot_no2 = (TextView)findViewById(R.id.lot_info_no_2);
        lot_name1 = (TextView)findViewById(R.id.lot_info_name_1);
        lot_name2 = (TextView)findViewById(R.id.lot_info_name_2);
        lot_apprisal1 = (TextView)findViewById(R.id.lot_info_apprisal_1);
        lot_apprisal2 = (TextView)findViewById(R.id.lot_info_apprisal_2);
        lot_startprice1 = (TextView)findViewById(R.id.lot_info_start_price_1);
        lot_startprice2 = (TextView)findViewById(R.id.lot_info_start_price_2);

        bid_input_money = (EditText)findViewById(R.id.bid_info_input_money);
        bid_seconds = (TextView)findViewById(R.id.bid_info_seconds);
        bid_now_price = (TextView)findViewById(R.id.bid_info_now_price);
        bid_next_price = (TextView)findViewById(R.id.bid_info_next_money);
        bid_min_inc = (TextView)findViewById(R.id.bid_info_min_money);
        bid_biddingNo = (TextView)findViewById(R.id.bid_info_number);

        int[] ids = {R.id.bid_info_next_money, R.id.bid_info_enter_money};
        listen(ids);
        showListView();
    }

    @Override
    public void onResume() {
        super.onResume();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        HttpClient conn = new HttpClient();
                        conn.setParam("auctionMainId", Variable.currentAuction.id);
                        conn.setParam("auctionId", "");
                        conn.setUrl(Constant.url +
                                "pSynchronizationAction!getBothAuctionBiddingInfo.htm");
                        new Thread(new HttpPostRunnable(conn, new DispalyHandler())).start();
                    }
                });
            }
        };
        new Timer().schedule(timerTask, 0, 3000); // 立刻启动间隔N秒的task

        checkApplyStatus(); // 判断 同步拍卖会 或 网络拍卖会拍品 参拍状态
    }

    @Override
    public void onPause() {
        super.onPause();
        timerTask.cancel();
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
                        case 0: // 返回信息成功,返回账户参拍信息
                            authStatus = true;
                            try {
                                status = data.getString("status");
                                name = data.getString("name");
                                money = data.getString("money");
                                mobile = data.getString("mobile");
                                biddingNo = data.getString("biddingNo");
                                identityNumber = data.getString("identityNumber");
                                bid_biddingNo.setText("我的号牌: " + biddingNo);

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
                        case 1: // 未认证，返回信息失败
                            authStatus = false;
                            title_right.setVisibility(View.VISIBLE);
                            break;
                        default: // 其它原因，返回信息失败
                            break;
                    }
                }
            })).start();
        }
    }

    // 展示出价记录初始化
    public void showListView(){
        listview = (ListViewForScrollView) findViewById(R.id.record_list);
        list = new ArrayList<Record>();
        adapter = new AuctionRecordAdapter(this, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                applyForBidding();
                break;
            case R.id.bid_info_next_money:
                giveMyPrice(nextPrice);
                break;

            case R.id.bid_info_enter_money:
                String price = bid_input_money.getText().toString();
                if(price.equals("")){
                    Utility.toastMessage("出价不能为空");
                } else {
                    giveMyPrice(price);
                }
                break;
            default:
            break;
        }
    }

    // 申请参拍
    private void applyForBidding(){
        if(Variable.isLogin == false) {
            Utility.gotoLogin();
        } else if(authStatus == false){
            Utility.alertDialog("请先进行账户认证", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Utility.gotoActivity(IdentityVerifyActivity.class);
                }
            });
        } else if(biddingStatus == false){
            String[] applyInfo = new String[]{"参拍人姓名:" + name, "手机号码  :" + mobile,
                    "身份证号码:" + identityNumber};
            new AlertDialog.Builder(this).setTitle("申请参拍").setItems(applyInfo, null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            HttpClient conn = new HttpClient();
                            conn.setHeader("cookie", "JSESSIONID="+Variable.account.sessionid);
                            conn.setParam("auctionId", "");
                            conn.setParam("auctionMainId", Variable.currentAuction.id);
                            conn.setUrl(Constant.url + "pTraceAction!askPayDeposit.htm");

                            new Thread(new HttpPostRunnable(conn, new HttpJsonHandler(){
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
        }

    }

    // 出价
    private void giveMyPrice(String price){
        applyForBidding();
        if(Variable.isLogin && authStatus && biddingStatus && !currentLotId.equals("")
            && !price.equals("") && !biddingNo.equals("")
            && !Variable.currentAuction.id.equals("") && !Variable.currentSession.id.equals("")){

            HttpClient conn = new HttpClient();
            conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
            conn.setParam("auctionMainId", Variable.currentAuction.id);
            conn.setParam("auctionSessionId", Variable.currentSession.id);
            conn.setParam("auctionId", currentLotId);
            conn.setParam("price", price);
            conn.setParam("biddingNo", biddingNo);
            conn.setUrl(Constant.url + "pSynchronizationAction!biddingLot.htm");

            new Thread(new HttpPostRunnable(conn, new HttpJsonHandler(){
                @Override
                public void handlerData(int code, JSONObject data) {
                    super.handlerData(code, data);
                    switch (code) {
                        case 0:
                            Utility.alertDialog("出价成功",null);
                            break;
                        case 1:
                            Utility.alertDialog("您的出价有误,出价失败",null);
                            break;
                        default:
                            Utility.alertDialog("出价失败",null);
                            break;
                    }
                }
            })).start();
        }
    }

    // 实时更新页面动态数据
    class DispalyHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
            case 0:
                try {
                    String status = data.getString("status"); // 拍卖会状态

                    // 倒计时信息与当前实时价格显示
                    int count = 0;
                    if(!data.getString("countdown").equals("")){
                        count = Integer.parseInt(data.getString("countdown"));
                    }
                    if(countTask!=null) {
                        countTask.cancel();
                    }
                    countTask = new CountTask(bid_seconds, count);
                    new Timer().schedule(countTask, 0, 1000); // 更新倒计时信息
                    bid_now_price.setText(data.getString("currentPriceForRMB")); // 显示当前价格

                    // 出价页面信息展示
                    bid_min_inc.setText("最小加价幅度:￥" + data.getString("minIncrement")); // 显示最小加价幅度
                    nextPrice = data.getString("nextPrice"); // 设置下一推荐出价
                    bid_next_price.setText("￥" + data.getString("nextPrice")); // 显示下一推荐出价

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

                    // 展示当前拍品与下一拍品的信息
                    String auctionId = data.getString("auctionId");
                    String nextAuctionId = data.getString("nextAuctionId");
                    lot_status.setText("拍品状态: "+data.getString("auctionStatus"));

                    if(auctionId.equals("")){
                        currentLotId = "";
                        findViewById(R.id.ly_main).setVisibility(View.GONE);
                    } else {
                        if(!auctionId.equals(currentLotId)){
                            currentLotId = auctionId;
                            // 获取当前拍品的详细信息
                            HttpClient con = new HttpClient();
                            con.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+auctionId);
                            new Thread(new HttpPostRunnable(con, new HttpJsonHandler() {
                                @Override
                                public void handlerData(int code, JSONObject data) {
                                    super.handlerData(code, data);
                                    switch (code){
                                        case 0:
                                            findViewById(R.id.ly_main).setVisibility(View.VISIBLE);
                                            Lot lot = null;
                                            try {
                                                lot = Lot.parseJson(data.getJSONObject("auctionInfo"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            currentLot = lot;
                                            lot_name1.setText(lot.name);
                                            lot_no1.setText("图录号:" + lot.no);
                                            lot_apprisal1.setText("预估价:" + lot.appraisal1 + "-" + lot.appraisal2);
                                            lot_startprice1.setText("起拍价:" + lot.startPrice);
                                            new LoadImageTask(lot_image1,4).execute(lot.imageUrl); // 显示缩略图
                                            Tasks.showBigImage(lot.imageUrl, lot_image1, 1); // 点击缩略图时显示大图
                                            findViewById(R.id.lot_info_moreinfo_1).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Variable.currentLot = currentLot;
                                                    Utility.gotoActivity(LotInfoActivity.class);
                                                }
                                            });

                                            break;
                                        default:
                                            break;
                                    }

                                }
                            })).start();
                        }
                    }

                    if(nextAuctionId.equals("")){
                        nextLotId = "";
                        ly_lot2.setVisibility(View.GONE);
                    } else {
                        if(!nextAuctionId.equals(nextLotId)){
                            nextLotId = nextAuctionId;
                            // 获取下一拍品的详细信息
                            HttpClient conn = new HttpClient();
                            conn.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+nextLotId);
                            new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                                @Override
                                public void handlerData(int code, JSONObject data) {
                                    super.handlerData(code, data);
                                    switch (code){
                                        case 0:
                                            ly_lot2.setVisibility(View.VISIBLE);
                                            Lot lot = null;
                                            try {
                                                lot = Lot.parseJson(data.getJSONObject("auctionInfo"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            nextLot = lot;
                                            lot_name2.setText(lot.name);
                                            lot_no2.setText("图录号:" + lot.no);
                                            lot_apprisal2.setText("预估价:" + lot.appraisal1 + "-" + lot.appraisal2);
                                            lot_startprice2.setText("起拍价:" + lot.startPrice);
                                            new LoadImageTask(lot_image2,4).execute(lot.imageUrl); // 显示缩略图
                                            Tasks.showBigImage(lot.imageUrl, lot_image2, 1); // 点击缩略图时显示大图
                                            findViewById(R.id.lot_info_moreinfo_2).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Variable.currentLot = nextLot;
                                                    Utility.gotoActivity(LotInfoActivity.class);
                                                }
                                            });

                                            break;
                                        default:
                                            break;
                                    }

                                }
                            })).start();
                        }
                    }

                    // 当前拍卖会状态: "拍卖会未开始" "倒计时未开始" "倒计时开始" "倒计时已结束"
                    switch (status){
                        case "拍卖会未开始":
                            tips.setText("拍卖会未开始");
                            findViewById(R.id.ly_main).setVisibility(View.GONE);
                            break;

                        case "倒计时未开始":
                            findViewById(R.id.ly_tips).setVisibility(View.GONE);
                            findViewById(R.id.ly_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.ly_seconds).setVisibility(View.GONE);
                            break;

                        case "倒计时开始":
                            findViewById(R.id.ly_tips).setVisibility(View.GONE);
                            findViewById(R.id.ly_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.ly_seconds).setVisibility(View.VISIBLE);
                            break;

                        case "倒计时已结束":
                            findViewById(R.id.ly_tips).setVisibility(View.GONE);
                            findViewById(R.id.ly_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.ly_seconds).setVisibility(View.GONE);
                            break;
                        default:
                            findViewById(R.id.ly_tips).setVisibility(View.GONE);
                            findViewById(R.id.ly_main).setVisibility(View.GONE);
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
