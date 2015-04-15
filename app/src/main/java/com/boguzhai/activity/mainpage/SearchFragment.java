package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.StaticData;
import com.boguzhai.activity.search.SearchResultActivity;
import com.boguzhai.logic.utils.Utility;

public class SearchFragment extends Fragment {
    private static String TAG = "SearchFragment";
    private View view;
    private MainActivity context;
    public Utility utility = new Utility();
    private MyOnClickListener listener = new MyOnClickListener();

    private static final String[] list_type1={"不限","古籍书刊","中国字画","钱币邮品"};
    private static final String[] list_type2={"不限","历代字画","历代书法","扇面成扇"};
    private static final String[] list_type3={"不限","笔墨纸砚","书房雅玩"};
    private static final String[] list_auction={"不限","2015新年艺术品大拍","2014博古斋秋季拍卖会"};
    private static final String[] list_session={"不限","字画专场","古董专场","服饰专场"};

    private StringBuffer type1=new StringBuffer();
    private StringBuffer type2=new StringBuffer();
    private StringBuffer type3=new StringBuffer();
    private StringBuffer auction_status=new StringBuffer();
    private StringBuffer auction_type=new StringBuffer();
    private StringBuffer auction=new StringBuffer();
    private StringBuffer session=new StringBuffer();
    private StringBuffer lot_status=new StringBuffer();
    private StringBuffer lot_deal_type=new StringBuffer();

    private String keyword = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_search, null);
        context = (MainActivity)getActivity();

        init();
        return view;
    }

    public void init(){
        ((TextView)view.findViewById(R.id.title_center)).setText("查询");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
        setSpinners();
        listen(R.id.search);
    }

    public void setSpinners(){
        type1.replace(0,type1.length(),"不限");
        type2.replace(0,type2.length(),"不限");
        type3.replace(0,type3.length(),"不限");
        auction_status.replace(0,auction_status.length(),"不限");
        auction_type.replace(0,auction_type.length(),"不限");
        lot_status.replace(0,lot_status.length(),"不限");
        lot_deal_type.replace(0,lot_deal_type.length(),"不限");

        utility.setSpinner(context, view, R.id.type1, list_type1, type1);
        utility.setSpinner(context, view, R.id.type2, list_type2, type2);
        utility.setSpinner(context, view, R.id.type3, list_type3, type3);

        utility.setSpinner(context, view, R.id.auction_status, StaticData.auction_status, auction_status);
        utility.setSpinner(context, view, R.id.auction_type, StaticData.auction_type, auction_type);
        utility.setSpinner(context, view, R.id.lot_status, StaticData.lot_status, lot_status);
        utility.setSpinner(context, view, R.id.lot_deal_type, StaticData.lot_deal_type, lot_deal_type);

        utility.setSpinner(context, view, R.id.auction, list_auction, auction);
        utility.setSpinner(context, view, R.id.session, list_session, session);
    }

    // 监听一个 ViewId
    public void listen( int id){
        View v = view.findViewById(id);
        if ( v != null ){
            v.setOnClickListener(listener);
        }
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.search:
                    keyword = ((EditText)view.findViewById(R.id.keyword)).getText().toString();
                    context.startActivity(new Intent( context,  SearchResultActivity.class));
                    break;
                default:
                    break;
            }
        }
    }

    // 使用数组形式操作spinner
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        private String[] list;
        StringBuffer result;

        public SpinnerSelectedListener(String[] list, StringBuffer result){
            this.list=list; this.result=result;
        }

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            result.replace(0,result.length(),list[arg2]);
        }
        public void onNothingSelected(AdapterView<?> arg0) {}
    }
}