package com.boguzhai.activity.me.mylot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.me.info.DeliveryAddress;
import com.boguzhai.activity.me.order.PayOrderActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.StringApi;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class EditOrderActivity extends BaseActivity {

    private double lotPrice=0.0, freight=0.0, supportPrice =0.0; // 拍品总价， 运费，保费
    public  ArrayList<MylotItem> mylots; // 需要提交结算的拍品
    public static String addressInfo="", addressId="", pickCode="";

    private int time = 30;
    private TimerTask task;
    private TextView get_check_code, delivery_address;
    private EditText dp_name, dp_mobile, credentialNumber,support_price,invoiceText,remarkText ;

    // 供用户选择的项目
    private String[] invoiceTypeList = {"个人","单位"}; // 发票抬头类型
    private String[] credentialTypeList = {"二代身份证","三代身份证","港澳台身份证","护照","其它"}; // 证件类型
    private String[] invoiceContentList = {}; // 发票内容
    private String[] isSupportList = {"是", "否"}; // 是否需要保价
    private String[] deliveryTypeList = {"自提", "快递"}; // 是否需要保价
    private int invoiceTypeIndex=0, credentialTypeIndex=0, invoiceContentIndex=0, isSupportIndex=0, deliveryTypeIndex=0;
    private TextView deliType, invoiceType, invoiceContent, credentialType, isSupport;

    // http 提交的订单信息
    private String deliveryType="1", auctionId ="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_pay_edit_order);
        title.setText("编辑订单");
        init();
	}

    @Override
    public void onResume() {
        super.onResume();
        getFreight();
        delivery_address.setText(addressInfo);
    }

	private void init(){
        mylots = (ArrayList<MylotItem>)getIntent().getSerializableExtra("mylots");
        lotPrice=0.0;
        auctionId="";
        for(MylotItem lot: mylots){
            auctionId += lot.id+",";
            lotPrice += Double.parseDouble(lot.sum);
        }
        if(auctionId.length()>0){
            auctionId = auctionId.substring(0, auctionId.length()-1); //去除最后的逗号
        }

        ListViewForScrollView listview = (ListViewForScrollView) findViewById(R.id.list);
        MylotListAdapter adapter = new MylotListAdapter(this, mylots, true);
        listview.setAdapter(adapter);

        int ids[] = {R.id.ly_delivery_address, R.id.ly_delivery_type, R.id.ly_invoice_type,
                     R.id.ly_invoice_content,  R.id.ly_credential_type, R.id.ly_is_support,
                     R.id.submit, R.id.get_check_code};
        listen(ids);
        init_view();
	}

    // 界面初始化
    private void init_view(){
        deliType = (TextView)findViewById(R.id.delivery_type);
        invoiceType = (TextView)findViewById(R.id.invoice_type);
        invoiceContent = (TextView)findViewById(R.id.invoice_content);
        credentialType = (TextView)findViewById(R.id.credential_type);
        isSupport = (TextView)findViewById(R.id.is_support);

        delivery_address = (TextView)findViewById(R.id.delivery_address);
        get_check_code = (TextView)findViewById(R.id.get_check_code);


        dp_name = (EditText)findViewById(R.id.dp_name);
        dp_mobile = (EditText)findViewById(R.id.dp_mobile);
        credentialNumber = (EditText)findViewById(R.id.number); // 有效证件号码
        support_price = (EditText)findViewById(R.id.support_price);
        invoiceText = (EditText)findViewById(R.id.invoice);
        remarkText = (EditText)findViewById(R.id.remark);

        getDeliveryAddress();
        getInvoiceContent();
        setOrderPrice();

    }

    // 获取收货地址
    private void getDeliveryAddress(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientInfoAction!getDeliveryAddress.htm");
        new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
            @Override
            public void handlerData(int code, JSONObject data){
                super.handlerData(code, data);
                switch(code){
                    case 0:
                        // 解析收货地址信息（列表）
                        try {
                            JSONArray deliveryAddressInfo = null;
                            deliveryAddressInfo = data.getJSONArray("deliveryAddressInfo");
                            Variable.account.deliveryAddressList.clear();
                            for(int i=0; i<deliveryAddressInfo.length(); ++i){
                                JSONObject auctionObj = deliveryAddressInfo.getJSONObject(i);
                                Variable.account.deliveryAddressList.add(DeliveryAddress.parseJson(auctionObj));
                            }

                            if(Variable.account.deliveryAddressList.size()>0){
                                addressInfo = Variable.account.deliveryAddressList.get(0).toString();
                                addressId = Variable.account.deliveryAddressList.get(0).id;
                            }else {
                                addressInfo = "新建收货地址";
                                addressId = "";
                            }
                            getFreight();
                            delivery_address.setText(addressInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Utility.alertMessage("网络数据错误");
                        break;
                }
            }
        })).start();
    }

    // 获取发票内容
    private void getInvoiceContent(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pTraceAction!getInvoiceContent.htm");
        new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
            @Override
            public void handlerData(int code, JSONObject data) {
                super.handlerData(code, data);
                try {
                    ArrayList<String> invoiceContent = new ArrayList<String>();
                    JSONArray list = data.getJSONArray("list");
                    for(int i=0; i<list.length(); ++i){
                        invoiceContent.add(list.getString(i));
                    }
                    invoiceContentList = invoiceContent.toArray(new String[invoiceContent.size()]);
                    EditOrderActivity.this.invoiceContent.setText(invoiceContentList[invoiceContentIndex]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    // 获取运费
    private void getFreight(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("addressId", addressId);
        conn.setParam("auctionId", auctionId);
        conn.setUrl(Constant.url + "pTraceAction!getFreight.htm");
        new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
            @Override
            public void handlerData(int code, JSONObject data) {
                super.handlerData(code, data);
                try {
                    String money = data.getString("freight");
                    freight = money.equals("")?0:Double.parseDouble(money);
                    ((TextView)findViewById(R.id.delivery_price)).setText("￥"+freight);
                    setOrderPrice();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    // 计算保费
    private void getSupportPrice(String price){
		HttpClient conn = new HttpClient();
		conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
		conn.setParam("price", price);
		conn.setUrl(Constant.url + "pTraceAction!getSupportPrice.htm");
		new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
			@Override
			public void handlerData(int code, JSONObject data) {
				super.handlerData(code, data);
                try {
                    String money = data.getString("supportPrice");
                    supportPrice = money.equals("")?0:Double.parseDouble(money);
                    setOrderPrice();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
			}
		})).start();
    }

    // 动态变更结算价格：总价以及运费、保费
    private void setOrderPrice(){
        TextView sum1 = (TextView)findViewById(R.id.sum1);
        TextView sum2 = (TextView)findViewById(R.id.sum2);
        if(deliType.getText().toString().equals("自提")){
            deliveryType="1";
            sum1.setText("￥" + lotPrice);
            sum2.setText("(含运费:0.0 保费:0.0)");

        }else if(deliType.getText().toString().equals("快递")){
            deliveryType="2";
            if(isSupport.getText().toString().equals("是")){
                sum1.setText("￥" + (lotPrice+freight+ supportPrice));
                sum2.setText("(含运费:" + freight + " 保费:" + supportPrice + ")");
            }else if(isSupport.getText().toString().equals("否")){
                sum1.setText("￥" + (lotPrice+freight));
                sum2.setText("(含运费:" + freight + " 保费:0.0)");
            }
        }
    }


	@Override
	public void onClick(View view) {
        super.onClick(view);

        String s_price = support_price.getText().toString();
        if(!s_price.equals("")){
            getSupportPrice(s_price);
        }

		switch (view.getId()){
			case R.id.submit:
                submitOrderInfo();
				break;
            case R.id.ly_is_support:
                new AlertDialog.Builder(context).setSingleChoiceItems(isSupportList, isSupportIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                dialog.dismiss();
                                isSupportIndex = index;
                                isSupport.setText(isSupportList[index]);
                                if(isSupport.getText().toString().equals("是")){
                                    findViewById(R.id.ly_support_price).setVisibility(View.VISIBLE);
                                }else if(isSupport.getText().toString().equals("否")){
                                    findViewById(R.id.ly_support_price).setVisibility(View.GONE);
                                }
                                setOrderPrice();
                            }
                        }).setNegativeButton("取消", null).show();

                break;
            case R.id.ly_delivery_address:
                startActivity(new Intent(this, ChooseAddressActivity.class));
                break;
            case R.id.ly_delivery_type:
                new AlertDialog.Builder(context).setSingleChoiceItems(deliveryTypeList, deliveryTypeIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                dialog.dismiss();
                                deliveryTypeIndex = index;
                                deliType.setText(deliveryTypeList[index]);
                                if(deliType.getText().toString().equals("自提")){
                                    findViewById(R.id.ly_myself).setVisibility(View.VISIBLE);
                                    findViewById(R.id.ly_express).setVisibility(View.GONE);
                                } else if(deliType.getText().toString().equals("快递")){
                                    findViewById(R.id.ly_myself).setVisibility(View.GONE);
                                    findViewById(R.id.ly_express).setVisibility(View.VISIBLE);
                                }
                                setOrderPrice();
                            }
                        }).setNegativeButton("取消", null).show();

                break;
            case R.id.ly_invoice_type:
                new AlertDialog.Builder(context).setSingleChoiceItems(invoiceTypeList, invoiceTypeIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                invoiceTypeIndex = index;
                                invoiceType.setText(invoiceTypeList[index]);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
                break;
            case R.id.ly_invoice_content:
                new AlertDialog.Builder(context).setSingleChoiceItems(invoiceContentList, invoiceContentIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                invoiceContentIndex = index;
                                invoiceContent.setText(invoiceContentList[index]);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();

                break;
            case R.id.ly_credential_type:
                new AlertDialog.Builder(context).setSingleChoiceItems(credentialTypeList, credentialTypeIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                credentialTypeIndex = index;
                                credentialType.setText(credentialTypeList[index]);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
                break;

            case R.id.get_check_code:
                if(! StringApi.checkPhoneNumber(dp_mobile.getText().toString())){
                    Utility.alertMessage(StringApi.tips);
                    break;
                }
                get_check_code.setEnabled(false);
                task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() { // UI thread
                            @Override
                            public void run() {
                                if (time <= 0) {
                                    get_check_code.setEnabled(true);
                                    get_check_code.setText("获取验证码");
                                    time=30;
                                    task.cancel();
                                } else {
                                    get_check_code.setText("获取验证码(" + time+")");
                                }
                                time--;
                            }
                        });
                    }
                };

                HttpClient conn = new HttpClient();
                conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                conn.setParam("mobile", dp_mobile.getText().toString());
                conn.setUrl(Constant.url+"pLoginAction!getMobileCheckCode.htm");
                new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                    @Override
                    public void handlerData(int code, JSONObject data) {
                        super.handlerData(code, data);
                        switch (code){
                            case 0:
                                try {
                                    pickCode = data.getString("pickCode");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Utility.toastMessage("发送提货码成功，请注意查收");
                                break;
                            case 1:
                                Utility.toastMessage("发送提货码失败，请重新获取提货码");
                            default:
                                break;

                        }
                    }
                })).start();

                new Timer().schedule(task, 0, 1000);
                break;

			default:
				break;
		}
    }

    // 提交订单信息,获取订单编号
    private void getOrderId(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("addressId", addressId);
        conn.setParam("auctionId", auctionId);
        conn.setParam("deliveryType", deliveryType);
        conn.setParam("deliveryPersonName", dp_name.getText().toString());
        conn.setParam("deliveryPersonMobile", dp_mobile.getText().toString());
        conn.setParam("deliveryPersonType", credentialType.getText().toString());
        conn.setParam("deliveryPersonNumber", credentialNumber.getText().toString());
        conn.setParam("checkCode", pickCode);
        conn.setParam("expressSupport", isSupport.getText().toString().equals("是")?"1":"0");
        conn.setParam("supportPrice", supportPrice+"");
        conn.setParam("invoiceHeaderType", invoiceType.getText().toString());
        conn.setParam("invoiceHeader", invoiceText.getText().toString());
        conn.setParam("invoiceContent", invoiceContent.getText().toString());
        conn.setParam("remark", remarkText.getText().toString());
        conn.setUrl(Constant.url + "pTraceAction!submitOrderInfo.htm");
        new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
            @Override
            public void handlerData(int code, JSONObject data) {
                super.handlerData(code, data);
                switch (code){
                    case 0:
                        try {
                            Intent intent = new Intent(Variable.currentActivity, PayOrderActivity.class);
                            intent.putExtra("orderId",data.getString("orderId"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        Utility.alertDialog("网络错误",null);
                        break;
                    default:
                        break;
                }

            }
        })).start();
    }

    // 检查填写的订单信息
    private void submitOrderInfo(){
        if(addressId.equals("")){
            Utility.alertDialog("请选择收货地址",null);  return;
        }

        if(deliveryType.equals("1")){
            if( dp_name.getText().toString().equals("")){
                Utility.alertDialog("请填写提货人地址",null);
            } else if( dp_mobile.getText().toString().equals("")){
                Utility.alertDialog("请填写提货人手机号码",null);
            } else if( credentialType.getText().toString().equals("")){
                Utility.alertDialog("请选择提货人有效证件类型",null);
            } else if( credentialNumber.getText().toString().equals("")){
                Utility.alertDialog("请填写提货人有效证件号码",null);
            } else if( pickCode.equals("")){
                Utility.alertDialog("请获取提货码",null);
            } else {
                getOrderId();
            }

        }else if(deliveryType.equals("2")){
            if( isSupport.getText().toString().equals("")){
                Utility.alertDialog("请选择是否保价",null);
            } else if( isSupport.getText().toString().equals("是")){
                if( support_price.getText().toString().equals("")){
                    Utility.alertDialog("请填写声明价值",null);
                } else {
                    HttpClient conn = new HttpClient();
                    conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                    conn.setParam("price", support_price.getText().toString());
                    conn.setUrl(Constant.url + "pTraceAction!getSupportPrice.htm");
                    new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                        @Override
                        public void handlerData(int code, JSONObject data) {
                            super.handlerData(code, data);
                            try {
                                String money = data.getString("supportPrice");
                                supportPrice = money.equals("")?0:Double.parseDouble(money);
                                setOrderPrice();
                                getOrderId();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })).start();
                }
            } else {
                getOrderId();
            }

        } else {
            Utility.alertDialog("请选择配送方式",null);
        }
    }
}
