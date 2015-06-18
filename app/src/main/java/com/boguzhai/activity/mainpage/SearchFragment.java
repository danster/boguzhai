package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.listener.SpinnerListener;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private static String TAG = "SearchFragment";
    private View view;
    private MainActivity context;
    private MyOnClickListener listener = new MyOnClickListener();

    private ArrayList<Pair<String,String>> mapLottype1 = Variable.mapLottype1;
    private ArrayList<Pair<String,String>> mapLottype2 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapLottype3 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapAuction = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapSession = new ArrayList< Pair<String,String> >();

    private String[] list_status={"不限", "预展中","拍卖中","已结束"};
    private String[] list_lot_status={"不限","已上拍","未上拍","成交","流拍","撤拍"};
    private String[] list_lot_deal_type={"不限", "现场", "网络"};

    private ArrayList<Auction> list_auction_all = new ArrayList<Auction>();
    private ArrayList<Auction> list_auction = new ArrayList<Auction>();
    private ArrayList<Session> list_session = new ArrayList<Session>();

    private Lottype_1 currentType1;
    private Lottype_2 currentType2;
    private Auction currentAuction;

    // 需要上传的值
    private StringBuffer lottypeId1=new StringBuffer("");
    private StringBuffer lottypeId2=new StringBuffer("");
    private StringBuffer lottypeId3=new StringBuffer("");
    private StringBuffer auctionId=new StringBuffer("");
    private StringBuffer sessionId=new StringBuffer("");
    private StringBuffer auction_status=new StringBuffer("");
    private StringBuffer lot_status=new StringBuffer("");
    private StringBuffer lot_deal_type=new StringBuffer("");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_search, null);
        context = (MainActivity)getActivity();

        init();
        return view;
    }

    public void init(){

        HttpClient conn = new HttpClient();
        conn.setUrl(Constant.url + "pMainAction!getAuctionMainList.htm?status=");
        new Thread(new HttpPostRunnable(conn,new AuctionListHandler())).start();

        ((TextView)view.findViewById(R.id.title_center)).setText("查询");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
        setSpinners();
        listen(R.id.search);
    }

    public void setSpinners(){

        // 拍品类型选择器之间的联动
        Utility.setSpinner(context, (Spinner)view.findViewById(R.id.type1), Utility.getValueList(mapLottype1),
                new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                currentType1 = Variable.mapLottype.get(arg2);
                lottypeId1.replace(0, lottypeId1.length(), mapLottype1.get(arg2).first);
                // 重置
                lottypeId2.replace(0, lottypeId2.length(),"");
                lottypeId3.replace(0, lottypeId3.length(),"");

                mapLottype2.clear();
                for(Lottype_2 lottype_2:currentType1.child){
                    mapLottype2.add(new Pair<String, String>(lottype_2.id, lottype_2.name));
                }

                Utility.setSpinner(context, (Spinner)view.findViewById(R.id.type2), Utility.getValueList(mapLottype2),
                        new AdapterView.OnItemSelectedListener(){
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        currentType2 = currentType1.child.get(arg2);
                        lottypeId2.replace(0, lottypeId2.length(), mapLottype2.get(arg2).first);
                        // 先重置
                        lottypeId3.replace(0, lottypeId3.length(),"");

                        mapLottype3.clear();
                        for(Lottype_3 lottype_3:currentType2.child){
                            mapLottype3.add(new Pair<String, String>(lottype_3.id, lottype_3.name));
                        }

                        Utility.setSpinner(context, (Spinner)view.findViewById(R.id.type3), Utility.getValueList(mapLottype3),
                                new AdapterView.OnItemSelectedListener(){
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                lottypeId3.replace(0, lottypeId3.length(), mapLottype3.get(arg2).first);
                            }
                            public void onNothingSelected(AdapterView<?> arg0) {
                                lottypeId3.replace(0, lottypeId3.length(), "");
                            }
                        });
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {
                        lottypeId2.replace(0, lottypeId2.length(), "");
                        lottypeId3.replace(0, lottypeId3.length(), "");
                    }
                });
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                lottypeId1.replace(0, lottypeId1.length(), "");
                lottypeId2.replace(0, lottypeId2.length(), "");
                lottypeId3.replace(0, lottypeId3.length(), "");
            }
        });

        // 设置拍卖会的状态与类型以及拍品的状态与成交方式的选择器
        Utility.setSpinner(context, (Spinner)view.findViewById(R.id.auction_status), list_status,
                new AuctionChooseListener(list_status, auction_status));
        Utility.setSpinner(context, (Spinner)view.findViewById(R.id.lot_status), list_lot_status,
                new SpinnerListener(list_lot_status, lot_status));
        Utility.setSpinner(context, (Spinner)view.findViewById(R.id.lot_deal_type), list_lot_deal_type,
                new SpinnerListener(list_lot_deal_type, lot_deal_type));

    }


    /*************************** View Listener ****************************/
    public void listen( View v){ if(v!=null){ v.setOnClickListener(listener);}} // 监听一个 VieW
    public void listen( int id){ this.listen(view.findViewById(id));}           // 监听一个 View Id
    public void listen( int[] ids){ for(int id: ids){ this.listen(id);}}        // 监听一组 View Ids

    private ArrayList<Pair<String,String>> getAuctionKeyValueList(ArrayList<Auction> auctions){
        ArrayList<Pair<String,String>> pairList = new ArrayList<Pair<String,String>>();
        pairList.add(new Pair<String, String>("", "不限"));
        for(Auction auction: auctions){
            pairList.add(new Pair<String, String>(auction.id, auction.name));
        }
        return pairList;
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.search:
                    String keyword = ((EditText)view.findViewById(R.id.keyword)).getText().toString();
                    String url = Constant.url+"pAuctionInfoAction!searchAuction.htm?";
                    url += "keyword="+keyword;
                    url += "&type1="+lottypeId1.toString();
                    url += "&type2="+lottypeId2.toString();
                    url += "&type3="+lottypeId3.toString();
                    url += "&auctionMainId="+auctionId.toString();
                    url += "&auctionSeesionId="+auctionId.toString();
                    url += "&status="+sessionId.toString();
                    url += "&type="+lot_deal_type.toString();
                    Intent intent = new Intent( context,  SearchResultActivity.class);
                    intent.putExtra("url",url);
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    class AuctionListHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    list_auction_all.clear();
                    list_auction_all.addAll(JsonApi.getAuctionList(data));
                    list_auction.clear();
                    for(Auction auction: list_auction_all){
                        list_auction.add(auction);
                    }
                    mapAuction = getAuctionKeyValueList(list_auction);
                    Utility.setSpinner(context, (Spinner)view.findViewById(R.id.auction),
                            Utility.getValueList(mapAuction), new AuctionListener());
                    break;
                default:
                    break;
            }
        }
    }

    class AuctionListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            sessionId.replace(0,sessionId.length(),"");//重置
            mapSession.clear();
            if(arg2 == 0){
                currentAuction = null;
            } else {
                currentAuction = list_auction.get(arg2-1);
                for(Session session: currentAuction.sessionList){
                    mapSession.add(new Pair<String, String>(session.id,session.name));
                }
            }
            Utility.setSpinner(context, (Spinner)view.findViewById(R.id.session), Utility.getValueList(mapSession),
                new AdapterView.OnItemSelectedListener(){
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        sessionId.replace(0,sessionId.length(),mapSession.get(arg2).first);
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {
                        sessionId.replace(0,sessionId.length(),"");
                    }
                });
            auctionId.replace(0,auctionId.length(),mapAuction.get(arg2).first);
        }
        public void onNothingSelected(AdapterView<?> arg0) {
            auctionId.replace(0,auctionId.length(),"");
            sessionId.replace(0,sessionId.length(),"");
        }
    }

    class AuctionChooseListener implements AdapterView.OnItemSelectedListener {
        private String[] list;
        private StringBuffer result;

        public AuctionChooseListener(String[] list, StringBuffer result){
            this.list=list;
            this.result=result;
        }

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            auctionId.replace(0,auctionId.length(),"");
            sessionId.replace(0, sessionId.length(), "");

            if(list[arg2].equals("不限")){
                result.replace(0,result.length(),"");
            }else {
                result.replace(0,result.length(),list[arg2]);
            }

            list_auction.clear();
            for(Auction auction: list_auction_all){
                if(auction_status.toString().equals("") )
                    list_auction.add(auction);
                else if(auction.status.equals(auction_status.toString()))
                    list_auction.add(auction);
            }
            mapAuction = getAuctionKeyValueList(list_auction);
            Utility.setSpinner(context, (Spinner)view.findViewById(R.id.auction), Utility.getValueList(mapAuction), new AuctionListener());

        }
        public void onNothingSelected(AdapterView<?> arg0) {
            auctionId.replace(0,auctionId.length(),"");
            sessionId.replace(0,sessionId.length(),"");
        }
    }

}