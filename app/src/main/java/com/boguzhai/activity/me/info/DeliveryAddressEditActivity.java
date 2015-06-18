package com.boguzhai.activity.me.info;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

public class DeliveryAddressEditActivity extends BaseActivity {

    private EditText name, address, mobile, telephone, zip;
    private CheckBox isDefault;
    private DeliveryAddress oldAddress;
    private String tipsStr = "";

    // 以下变量在实现“省市区选择器之间的联动”时使用
    private ArrayList<Pair<String,String>> mapAddress1 = Variable.mapProvince;
    private ArrayList<Pair<String,String>> mapAddress2 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapAddress3 = new ArrayList< Pair<String,String> >();
    private StringBuffer address_1 =new StringBuffer("");
    private StringBuffer address_2 =new StringBuffer("");
    private StringBuffer address_3 =new StringBuffer("");
    private Address_1 currentAddress1;
    private Address_2 currentAddress2;
    private int index1=-1, index2=-1,index3=-1;
    private boolean index1boost=true, index2boost=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_addr_edit);
        init();
	}

	protected void init(){
        oldAddress = Variable.currentDeliveryAddress;
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        mobile = (EditText) findViewById(R.id.mobile);
        telephone = (EditText) findViewById(R.id.telephone);
        zip = (EditText) findViewById(R.id.zip);
        isDefault = (CheckBox)findViewById(R.id.is_default);
        isDefault.setChecked(false);

        if(oldAddress == null){
            title.setText("增加地址");
        }else{
            title.setText("修改地址");
            title_right.setText("删除地址");
            title_right.setVisibility(View.VISIBLE);

            name.setText(oldAddress.receiver);
            address.setText(oldAddress.address);
            mobile.setText(oldAddress.mobile);
            telephone.setText(oldAddress.telephone);
            zip.setText(oldAddress.zip);

            index1 = Utility.getAddressIndex(oldAddress.address_1);
            index2 = Utility.getAddressIndex(oldAddress.address_1,oldAddress.address_2);
            index3 = Utility.getAddressIndex(oldAddress.address_1,oldAddress.address_2,oldAddress.address_3);

            if(oldAddress.isDefault){
                isDefault.setChecked(true);
            }else{
                isDefault.setChecked(false);
            }
        }

        // 省市区选择器之间的联动
        Utility.setSpinner(baseActivity, (Spinner)findViewById(R.id.address_1), Utility.getValueList(mapAddress1),
            new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    currentAddress1 = Variable.mapZone.get(arg2);
                    address_1.replace(0, address_1.length(), mapAddress1.get(arg2).first);
                    address_2.replace(0, address_2.length(), "");
                    address_3.replace(0, address_3.length(), "");
                    mapAddress2.clear();
                    for (Address_2 address_2 : currentAddress1.child) {
                        mapAddress2.add(new Pair<String, String>(address_2.id, address_2.name));
                    }
                    Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_2), Utility.getValueList(mapAddress2),
                            new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    currentAddress2 = currentAddress1.child.get(arg2);
                                    address_2.replace(0, address_2.length(), mapAddress2.get(arg2).first);
                                    address_3.replace(0, address_3.length(), "");

                                    mapAddress3.clear();
                                    for (Address_3 address_3 : currentAddress2.child) {
                                        mapAddress3.add(new Pair<String, String>(address_3.id, address_3.name));
                                    }
                                    Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_3), Utility.getValueList(mapAddress3),
                                        new AdapterView.OnItemSelectedListener() {
                                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                                address_3.replace(0, address_3.length(), mapAddress3.get(arg2).first);
                                            }

                                            public void onNothingSelected(AdapterView<?> arg0){}
                                        });
                                    if(index2boost) {
                                        ((Spinner) findViewById(R.id.address_3)).setSelection(index3);
                                        index2boost=false;
                                    }
                                }

                                public void onNothingSelected(AdapterView<?> arg0) {}
                            });
                    if(index1boost) {
                        ((Spinner) findViewById(R.id.address_2)).setSelection(index2);
                        index1boost=false;
                    }
                }
                public void onNothingSelected(AdapterView<?> arg0) {}
            });
        ((Spinner) findViewById(R.id.address_1)).setSelection(index1);

        int[] ids = {R.id.ok, R.id.name_clear, R.id.address_clear, R.id.mobile_clear,
                     R.id.telephone_clear, R.id.zip_clear};
        this.listen(ids);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
        case R.id.ok:

            if(name.getText().toString().equals("")   || address.getText().toString().equals("") ||
               mobile.getText().toString().equals("") || address_1.toString().equals("") ||
               address_2.toString().equals("")        || address_3.toString().equals("") ){
                Utility.alertDialog("必填项不能为空！");
                break;
            }

            HttpClient conn = new HttpClient();
            conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
            conn.setParam("receiver", name.getText().toString());
            conn.setParam("address_1", address_1.toString());
            conn.setParam("address_2", address_2.toString());
            conn.setParam("address_3", address_3.toString());
            conn.setParam("address", address.getText().toString());
            conn.setParam("mobile", mobile.getText().toString());
            conn.setParam("telephone", telephone.getText().toString());
            conn.setParam("zip", zip.getText().toString());
            conn.setParam("isDefault", isDefault.isChecked()?"1":"0");

            if(oldAddress == null){ //新增收货信息
                tipsStr = "增加收货信息";
                conn.setUrl(Constant.url+"pClientInfoAction!addDeliveryAddress.htm");
                new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();
                Utility.showLoadingDialog("正在上传新增信息 ...");
            } else { //修改收货信息
                tipsStr = "修改收货信息";
                conn.setParam("addressId", Variable.currentDeliveryAddress.id);
                conn.setUrl(Constant.url + "pClientInfoAction!updateDeliveryAddress.htm");
                new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();
                Utility.showLoadingDialog("正在上传修改信息 ...");
            }
            break;

        case R.id.ly_title_right:
            Utility.alertDialog("确定删除该收货地址？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tipsStr = "删除收货信息";
                    dialog.dismiss();
                    HttpClient conn = new HttpClient();
                    conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
                    conn.setParam("addressId", Variable.currentDeliveryAddress.id);
                    conn.setUrl(Constant.url + "pClientInfoAction!removeDeliveryAddress.htm");
                    new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();
                    Utility.showLoadingDialog("正在上传删除信息 ...");
                }
            }, null);
            break;

        case R.id.name_clear:      name.setText("");      break;
        case R.id.address_clear:   address.setText("");   break;
        case R.id.mobile_clear:    mobile.setText("");    break;
        case R.id.telephone_clear: telephone.setText(""); break;
        case R.id.zip_clear:       zip.setText("");       break;
        default: break;
		};
	}

    class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            Utility.dismissLoadingDialog();
            super.handlerData(code, data);
            switch(code){
                case 0: Utility.alertDialog(tipsStr+"成功", Variable.toFinish);  break;
                case 1: Utility.alertDialog(tipsStr+"失败");                     break;
            }
        }
    }

}
