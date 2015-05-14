package com.boguzhai.activity.auction;

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
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class LotInfoActivity extends BaseActivity {

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

        // 获取当前拍品的详细信息
        HttpClient con = new HttpClient();
        con.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoById.htm?auctionId="+Variable.currentLot.id);
        new Thread(new HttpPostRunnable(con,new ShowLotInfoHandler())).start();

        // 获取拍品所在拍卖会信息
        HttpClient conn = new HttpClient();
        conn.setUrl(Constant.url+"pMainAction!getAuctionMainById.htm?auctionMainId="+Variable.currentLot.auctionId);
        new Thread(new HttpPostRunnable(conn,new ShowAuctionInfoHandler())).start();
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.favor:
                //收藏该拍品
                break;
            case R.id.session_info:
                Utility.gotoAuction(baseActivity, Variable.currentSession.status);
                break;

            default: break;
        }
    }

    public void showLotInfo(){
        Lot lot = Variable.currentLot;
        ((TextView)findViewById(R.id.lot_name)).setText(lot.name);
        ((TextView)findViewById(R.id.description)).setText("    "+lot.description);
        Tasks.showImage(lot.imageUrl, (ImageView)findViewById(R.id.lot_image));

        String info = "";
        info += "拍品分类: ";
        info += lot.type1.equals("")?"":Utility.getLottype1(lot.type1);
        info += lot.type2.equals("")?"":" > "+Utility.getLottype2(lot.type1,lot.type2);
        info += lot.type3.equals("")?"":" > "+Utility.getLottype3(lot.type1,lot.type2,lot.type3);
        info += "\n拍品名称: "+lot.name;
        info += "\n拍品编号: "+lot.id;
        info += "\n拍品图录号: "+lot.no;

        info += "\n预估价: "+lot.appraisal1+"元 ~ "+lot.appraisal2+"元";
        info += "\n起拍价: "+lot.startPrice+"元";

        for(Pair<String, String> pair: lot.specials){
            info += "\n"+pair.first+": "+pair.second;
        }

        ((TextView)findViewById(R.id.lot_info)).setText(info);
    }

    class ShowLotInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    try {
                        Variable.currentLot = Lot.parseJson(data.getJSONObject("auctionInfo"));
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
            switch (code){
                case 0:
                    try {
                        Variable.currentAuction = Auction.parseJson(data.getJSONObject("auctionMain"));
                        Variable.currentSession = null;
                        for( Session s : Variable.currentAuction.sessionList){
                            if(s.id.equals(Variable.currentLot.sessionId)){
                                Variable.currentSession = s;
                                break;
                            }
                        }
                        Utility.showAuctionInfo(baseActivity, Variable.currentAuction, Variable.currentSession);
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
