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

    // 竞拍大厅信息，当前拍品，下一拍品，出价页面
    LinearLayout ly_main, ly_lotinfo_1, ly_lotinfo_2, ly_price;
    ImageView lot_info_image_1, lot_info_image_2;

    TextView lot_info_name_1, lot_info_name_2, lot_info_no_1, lot_info_no_2, lot_info_apprisal_1,
            lot_info_apprisal_2, lot_info_start_price_1, lot_info_start_price_2;

    TextView tips;
    TextView bid_info_seconds, bid_info_now_price, bid_info_add_money, bid_info_next_money,
             bid_info_min_money;
    EditText bid_info_input_money;

    private Lot currentLot=null, nextLot=null;
    private String currentLotId="", nextLotId="";

    // 获取同步拍卖实时信息
    private Timer timer = null;
    private TimerTask task = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScrollView(R.layout.auction_active);
        title.setText("拍卖出价");
        init();
    }

    public void init(){
        ly_main = (LinearLayout)findViewById(R.id.main_layout);
        tips = (TextView)findViewById(R.id.tips);

        ly_lotinfo_1 = (LinearLayout)findViewById(R.id.lot_info_layout_1);
        ly_lotinfo_2 = (LinearLayout)findViewById(R.id.lot_info_layout_2);
        ly_price = (LinearLayout)findViewById(R.id.price);
        ly_price.setVisibility(View.GONE);

        lot_info_image_1 = (ImageView)findViewById(R.id.lot_info_image_1);
        lot_info_image_2 = (ImageView)findViewById(R.id.lot_info_image_2);
        lot_info_no_1 = (TextView)findViewById(R.id.lot_info_no_1);
        lot_info_no_2 = (TextView)findViewById(R.id.lot_info_no_2);
        lot_info_name_1 = (TextView)findViewById(R.id.lot_info_name_1);
        lot_info_name_2 = (TextView)findViewById(R.id.lot_info_name_2);
        lot_info_apprisal_1 = (TextView)findViewById(R.id.lot_info_apprisal_1);
        lot_info_apprisal_2 = (TextView)findViewById(R.id.lot_info_apprisal_2);
        lot_info_start_price_1 = (TextView)findViewById(R.id.lot_info_start_price_1);
        lot_info_start_price_2 = (TextView)findViewById(R.id.lot_info_start_price_2);

        bid_info_input_money = (EditText)findViewById(R.id.bid_info_input_money);
        bid_info_seconds = (TextView)findViewById(R.id.bid_info_seconds);
        bid_info_now_price = (TextView)findViewById(R.id.bid_info_now_price);
        bid_info_add_money = (TextView)findViewById(R.id.bid_info_add_money);
        bid_info_next_money = (TextView)findViewById(R.id.bid_info_next_money);
        bid_info_min_money = (TextView)findViewById(R.id.bid_info_min_money);

        int[] ids = {R.id.bid_info_next_money, R.id.bid_info_enter_money};
        listen(ids);
        showListView();
    }

    @Override
    public void onResume() {
        super.onResume();

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        HttpClient conn = new HttpClient();
                        conn.setParam("auctionMainId", Variable.currentAuction.id);
                        conn.setParam("auctionId", "");
                        conn.setUrl(Constant.url + "pSynchronizationAction!getBothAuctionBiddingInfo.htm");
                        new Thread(new HttpPostRunnable(conn, new DispalyHandler())).start();
                    }
                });
            }
        };
        timer.schedule(task, 0, 3000); // 立刻启动间隔N秒的task

        checkApplyStatus(); // 判断 同步拍卖会 或 网络拍卖会拍品 参拍状态
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onRestart() {
        super.onRestart();
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
                    Utility.alertDialog("请先进行账户认证", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utility.gotoActivity(IdentityVerifyActivity.class);
                        }
                    });
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
            case R.id.bid_info_enter_money:
                if(biddingStatus == false){
                    Utility.alertMessage("请先申请参拍！");
                    break;
                }
                // 自主出价
                // .....
                break;

            case R.id.bid_info_next_money:
                if(biddingStatus == false){
                    Utility.alertMessage("请先申请参拍！");
                    break;
                }
                // 推荐出价
                // .....
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
                    // 当前拍卖会状态: "拍卖会未开始" "未开始" "开始" "倒计时已结束"
                    // 当前拍品状态: "暂停" "正常" "成交" "流拍"
                    try {
                        String status = data.getString("status"); // 拍卖会状态
                        String lotStatus = data.getString("auctionStatus"); // 拍品状态

                        // 倒计时信息与当前实时价格显示
                        int count = 0;
                        if(!data.getString("countdown").equals("") && !data.getString("nowTime").equals("")){
                            count = Integer.parseInt(data.getString("countdown"));
                            int nowTime = Integer.parseInt(data.getString("nowTime"));
                            count -= ( (int)((new Date()).getTime()/1000 ) - nowTime);
                        }
                        new Timer().schedule(new CountTask(bid_info_seconds, count), 0, 1000); // 更新倒计时信息
                        bid_info_now_price.setText(data.getString("currentPriceForRMB"));   // 当前价格

                        // 出价页面信息展示
                        bid_info_min_money.setText("最小加价幅度:￥"+data.getString("minIncrement"));  // 最小加价幅度
                        bid_info_add_money.setText("+￥"+data.getString("nextIncrement")); // 下一推荐出价的加价幅度
                        bid_info_next_money.setText("￥"+data.getString("nextPrice"));    // 下一推荐出价

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

                        if(auctionId.equals("")){
                            currentLotId = "";
                            ly_main.setVisibility(View.GONE);
                        } else {
                            ly_main.setVisibility(View.VISIBLE);
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
                                                Lot lot = null;
                                                try {
                                                    lot = Lot.parseJson(data.getJSONObject("auctionInfo"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                currentLot = lot;
                                                lot_info_name_1.setText(lot.name);
                                                lot_info_no_1.setText("图录号:"+lot.no);
                                                lot_info_apprisal_1.setText("预估价:￥"+lot.appraisal1+" - ￥"+lot.appraisal2);
                                                lot_info_start_price_1.setText("起拍价:￥" + lot.startPrice);
                                                Tasks.showImage(lot.imageUrl, lot_info_image_1, 5); // 显示图片

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
                            ly_lotinfo_2.setVisibility(View.GONE);
                        } else {
                            ly_lotinfo_2.setVisibility(View.VISIBLE);
                            if(!nextAuctionId.equals(nextLotId)){
                                nextLotId = nextAuctionId;
                                // 获取下一拍品的详细信息
                                HttpClient conn = new HttpClient();
                                conn.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+auctionId);
                                new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                                    @Override
                                    public void handlerData(int code, JSONObject data) {
                                        super.handlerData(code, data);
                                        switch (code){
                                            case 0:
                                                Lot lot = null;
                                                try {
                                                    lot = Lot.parseJson(data.getJSONObject("auctionInfo"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                nextLot = lot;
                                                lot_info_name_2.setText(lot.name);
                                                lot_info_no_2.setText("图录号:"+lot.no);
                                                lot_info_apprisal_2.setText("预估价:￥"+lot.appraisal1+" - ￥"+lot.appraisal2);
                                                lot_info_start_price_2.setText("起拍价:￥"+lot.startPrice);
                                                Tasks.showImage(lot.imageUrl, lot_info_image_2, 5); // 显示图片

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

                        tips.setText("当前拍卖会:"+status+" 当前拍品:"+lotStatus);

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
