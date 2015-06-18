package com.boguzhai.activity.auction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.LoadImageTask;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class LotInfoActivity extends BaseActivity {

    private TextView collectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_lot_info);
        title.setText("拍品信息");
        init();
    }

    private void init(){
        this.listen(R.id.favor);
        this.listen(R.id.session_info);
        this.listen(R.id.lot_image);
        collectText = (TextView)findViewById(R.id.favor);
        findViewById(R.id.ly_auction_data).setVisibility(View.GONE);

        checkCollectInfo();

        ImageView image = (ImageView)findViewById(R.id.lot_image);
        new LoadImageTask(image, 4).execute(Variable.currentLot.imageUrl); // 显示缩略图
        Tasks.showBigImage(Variable.currentLot.imageUrl, image, 1); // 添加listener: 点击缩略图时显示大图

        // 获取当前拍品的详细信息
        HttpClient con = new HttpClient();
        con.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+Variable.currentLot.id);
        new Thread(new HttpPostRunnable(con,new ShowLotInfoHandler())).start();
    }

    // 检查拍品的收藏信息
    public void checkCollectInfo(){
        if(Variable.isLogin == true){
            HttpClient conn = new HttpClient();
            conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
            conn.setUrl(Constant.url+"pCommonAction!checkAuctionIsCollected.htm?auctionId="+Variable.currentLot.id);
            new Thread(new HttpPostRunnable(conn,new CheckCollectInfoHandler())).start();
        }
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.favor:
                if(Variable.isLogin == false ){
                    Utility.gotoLogin();
                } else if(collectText.getText().toString().equals("收藏")){
                    HttpClient conn = new HttpClient();
                    conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
                    conn.setUrl(Constant.url+"pCommonAction!collectAuction.htm?auctionId="+Variable.currentLot.id);
                    new Thread(new HttpPostRunnable(conn,new CollectInfoHandler())).start();
                } else {
                }
                break;
            case R.id.session_info:
                startActivity(new Intent(context, AuctionDisplayActivity.class));
                break;
            default: break;
        }
    }

    public void showLotInfo(){
        Lot lot = Variable.currentLot;
        ((TextView)findViewById(R.id.lot_name)).setText(lot.name);
        ((TextView)findViewById(R.id.description)).setText("  " + lot.description);
        String info = "";
        info += "拍品图录号: "+lot.no;
        info += "\n拍品编号: "+lot.id;
        info += "\n拍品名称: "+lot.name;
        info += "\n拍品分类: ";
        info += lot.type1.equals("")?"":Utility.getLottype1(lot.type1);
        info += lot.type2.equals("") ? "" : " > " + Utility.getLottype2(lot.type1,lot.type2);
        info += lot.type3.equals("")?"":" > "+Utility.getLottype3(lot.type1,lot.type2,lot.type3);

        info += "\n预估价: "+lot.appraisal1+"元 ~ "+lot.appraisal2+"元";
        info += "\n起拍价: "+lot.startPrice+"元";

        for(Pair<String, String> pair: lot.specials){
            info += "\n"+pair.first+": "+pair.second;
        }

        ((TextView)findViewById(R.id.lot_info)).setText(info);
    }

    class CollectInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch (code){
                case 0:
                    collectText.setText("已收藏");
                    Utility.toastMessage("收藏成功");
                    break;
                case 1:
                    collectText.setText("收藏");
                    Utility.toastMessage("收藏失败");
                    break;
                case 2:
                    collectText.setText("已收藏");
                    Utility.toastMessage("该拍品已被收藏");
                    break;
                default: break;
            }
        }
    }

    class CheckCollectInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch (code){
                case 0:
                    collectText.setText("已收藏");
                    break;
                case 1:
                    collectText.setText("收藏");
                    break;
                default: break;
            }
        }
    }

    class ShowLotInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch (code){
                case 0:
                    try {
                        Variable.currentLot = Lot.parseJson(data.getJSONObject("auctionInfo"));

                        // 获取拍品所在拍卖会信息
                        HttpClient conn = new HttpClient();
                        conn.setUrl(Constant.url + "pMainAction!getAuctionMainById.htm?auctionMainId=" + Variable.currentLot.auctionId);
                        new Thread(new HttpPostRunnable(conn,new ShowAuctionInfoHandler())).start();

                        showLotInfo();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default: break;
            }
        }
    }

    class ShowAuctionInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch (code){
                case 0:
                    try {
                        Variable.currentAuction = Auction.parseJson(data.getJSONObject("auctionMain"));
                        Variable.currentSession = null;
                        for( Session session : Variable.currentAuction.sessionList){
                            if(session.id.equals(Variable.currentLot.sessionId)){
                                Variable.currentSession = session;
                                break;
                            }
                        }
                        Utility.showAuctionInfo(Variable.currentActivity, Variable.currentAuction, Variable.currentSession);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
