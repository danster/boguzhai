package com.boguzhai.activity.me.capital;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CapitalDetailActivity extends BaseActivity {
    String type = ""; // 区分 账户余额明细 和 支付保证金明细
    //账户余额明细 "Zc15902130841,支出保证金,2014-08-20 16:24,0,200,3213.90"
    //保证金明细 "21号博古斋春季拍卖会,2014-08-20 16:24,线下,2000"

    private static final String[] list_balance={"不限","支付保证金"};
    private static final String[] list_bail={"不限","现场","网络","自动生成"};

    private StringBuffer balance_type=new StringBuffer();
    private StringBuffer bail_from=new StringBuffer();

    private ListViewForScrollView listview;
    private ArrayList<String> list;
    private CaptialDetailListAdapter adapter;

    private TextView left_date, right_date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_capital_detail);
        type = getIntent().getStringExtra("type");

        if(type.equals("balance") || type.equals("bail")) {
            toastMessage(type,1);
            init();
        }

	}

	protected void init(){
        left_date = (TextView)findViewById(R.id.left_date);
        right_date = (TextView)findViewById(R.id.right_date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        Date r_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(r_date);
        cal.add(Calendar.DAY_OF_YEAR,-10);
        Date l_date= cal.getTime();
        left_date.setText(dateFormat.format(l_date));
        right_date.setText(dateFormat.format(r_date));
        setDateListener();

        switch (type){
            case "balance":
                title.setText("我的余额明细");
                ((TextView)findViewById(R.id.type_name)).setText("业务类别");
                balance_type.replace(0,balance_type.length(),"不限");
                utility.setSpinner(this, R.id.type_list, list_balance, balance_type);
                break;
            case  "bail":
                title.setText("我的保证金明细");
                ((TextView)findViewById(R.id.type_name)).setText("资金来源");
                bail_from.replace(0,bail_from.length(),"不限");
                utility.setSpinner(this, R.id.type_list, list_bail, bail_from);
                break;
            default:
                break;
        }

        listview = (ListViewForScrollView) findViewById(R.id.detail_list);
        list = new ArrayList<String>();
        for(int i=0; i<8; i++){
            if(type.equals("balance"))
                list.add("Zc15902130841,支出保证金,2014.8.20 16:24,0,200,3213.90");
            if(type.equals("bail"))
                list.add("21号博古斋春季拍卖会,2014.8.20 16:24,线下,2000");
        }
        adapter = new CaptialDetailListAdapter(this, list, type);
        listview.setAdapter(adapter);

        listen(R.id.search);
	}


    public void setDateListener(){
//        // 如果生日输入为EditView时用以下代码调出日期选择器
//        birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {

        left_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(CapitalDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        left_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        right_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(CapitalDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        right_date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }



    public void showListView(){
        list.clear();
        for(int i=0; i<4; i++){
            if(type.equals("balance"))
                list.add("Zc15902130841,支出保证金,2014.8.20 16:24,0,200,3213.90");
            if(type.equals("bail"))
                list.add("21号博古斋春季拍卖会,2014.8.20 16:24,线下,2000");
        }
        adapter.notifyDataSetChanged();
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
            case R.id.search:
                showListView();
                break;
            default:
                break;
		};
	}

}


