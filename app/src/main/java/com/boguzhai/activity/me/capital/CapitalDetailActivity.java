package com.boguzhai.activity.me.capital;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CapitalDetailActivity extends BaseActivity implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {
    String type = ""; // 区分 账户余额明细 和 支付保证金明细
    //账户余额明细 "Zc15902130841,支出保证金,2014-08-20 16:24,0,200,3213.90"
    //保证金明细 "21号博古斋春季拍卖会,2014-08-20 16:24,线下,2000"

    private static final String[] list_balance={};
    private static final String[] list_bail={};
    private ArrayList<String> type_list = new ArrayList<String>();

    private StringBuffer balance_type=new StringBuffer();
    private StringBuffer bail_from=new StringBuffer();

    private XListView listview;
    private ArrayList<BalanceDetail> balanceAllList, balanceList;
    private ArrayList<BailDetail> bailAllList, bailList;
    private CaptialDetailListAdapter adapter;

    private TextView left_date, right_date;

    private SwipeRefreshLayout swipe_layout;
    private MyInt order = new MyInt(1);
    private String httpUrl="";

    private Date lDate=null,rDate=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_capital_detail);
        type = getIntent().getStringExtra("type");
        init();
	}

	protected void init(){
        left_date = (TextView)findViewById(R.id.left_date);
        right_date = (TextView)findViewById(R.id.right_date);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        Date r_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(r_date);
        cal.add(Calendar.DAY_OF_YEAR, -10);
        Date l_date= cal.getTime();
        left_date.setText("请选择日期");
        right_date.setText("请选择日期");
        setDateListener();

        switch (type){
            case "balance":
                title.setText("暂存款明细");
                httpUrl = "pClientInfoAction!getBalanceDetail.htm";
                ((TextView)findViewById(R.id.type_name)).setText("业务类别");
                balance_type.replace(0,balance_type.length(),"不限");
                Utility.setSpinner(this, R.id.type_list, list_balance, balance_type);
                break;
            case  "bail":
                title.setText("保证金明细");
                httpUrl = "pClientInfoAction!getBailDetail.htm";
                ((TextView)findViewById(R.id.type_name)).setText("资金来源");
                bail_from.replace(0,bail_from.length(),"不限");
                Utility.setSpinner(this, R.id.type_list, list_bail, bail_from);
                break;
            default:
                finish();
                break;
        }

        listview = (XListView) findViewById(R.id.detail_list);
        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);
        balanceAllList = new ArrayList<BalanceDetail>();
        balanceList = new ArrayList<BalanceDetail>();
        bailAllList = new ArrayList<BailDetail>();
        bailList = new ArrayList<BailDetail>();
        adapter = new CaptialDetailListAdapter(this, balanceList,bailList, type);
        listview.setAdapter(adapter);

        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);

        listen(R.id.search);

        order.value = 1;
        httpConnect();
	}

    private void setSpinner(){
        Utility.setSpinner(this, (Spinner) findViewById(R.id.type_list), type_list,
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String str = type_list.get(arg2);
                        if(str.equals("全部")){
                            return;
                        }
                        switch (type){
                            case "balance":
                                balanceList.clear();
                                balanceList.addAll(BalanceDetail.filter(balanceAllList, str));
                                adapter.notifyDataSetChanged();
                                break;

                            case  "bail":
                                bailList.clear();
                                bailList.addAll(BailDetail.filter(bailAllList, str));
                                adapter.notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
    }

    @Override
    public void onRefresh() {
        order.value = 1;
        swipe_layout.setRefreshing(true);
        httpConnect();
    }

    @Override
    public void onLoadMore() {
        httpConnect();
    }

    private void httpConnect(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + httpUrl + "?number=" + order.value);
        new Thread(new HttpPostRunnable(conn, new GetDetailHandler())).start();
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            case R.id.search:
                if(left_date.getText().toString().equals("请选择日期") ||
                        right_date.getText().toString().equals("请选择日期")){
                    break;
                }

                if(lDate.compareTo(rDate)>=0){
                    Utility.alertMessage("请正确选择日期范围！");
                    break;
                }
                switch (type){
                    case "balance":
                        balanceList.clear();
                        balanceList.addAll(BalanceDetail.filter(balanceAllList, lDate, rDate));
                        adapter.notifyDataSetChanged();
                        break;

                    case  "bail":
                        bailList.clear();
                        bailList.addAll(BailDetail.filter(bailAllList, lDate, rDate));
                        adapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
		};
	}

    class GetDetailHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            swipe_layout.setRefreshing(false);
            listview.stopLoadMore();
            super.handlerData(code, data);

            switch(code){
                case 0:
                    if(order.value == -1) {
                        Utility.toastMessage("已无更多信息");
                        break;
                    } else if(order.value == 1)  {
                        bailAllList.clear();
                        balanceAllList.clear();
                    }

                    try {
                        int count = Integer.parseInt(data.getString("count"));
                        int size = Integer.parseInt(data.getString("size"));

                        if ((order.value-1)*size == count ) {
                            order.value = -1;
                            Utility.toastMessage("已无更多信息");
                            break;
                        } else if (order.value*size > count ) {
                            order.value = -1;
                        } else {
                            order.value ++;
                        }


                        type_list.clear();
                        type_list.add("全部");
                        if(type.equals("balance")){
                            ArrayList<BalanceDetail> list1 = BalanceDetail.parseJson(data);
                            balanceAllList.addAll(list1);
                            type_list.addAll(BalanceDetail.uniqType(balanceAllList));
                        } else if (type.equals("bail")){
                            ArrayList<BailDetail> list2 = BailDetail.parseJson(data);
                            bailAllList.addAll(list2);
                            type_list.addAll(BailDetail.uniqFrom(bailAllList));
                        }

                        left_date.setText("请选择日期");
                        right_date.setText("请选择日期");
                        setSpinner();
                        balanceList.addAll(balanceAllList);
                        bailList.addAll(bailAllList);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Utility.alertMessage("网络数据错误");
                    break;
            }
        }
    }

    // 设置日期选择器
    public void setDateListener(){
        left_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(CapitalDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        lDate = cal.getTime();
                        left_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        right_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(CapitalDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        rDate = cal.getTime();
                        right_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }



}


