package com.boguzhai.activity.me.info;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class DeliveryAddressEditActivity extends BaseActivity {

    private static final String[] list_addr_1={"不限","北京","上海","江苏","浙江","其他"};
    private static final String[] list_addr_2={"不限","南京","镇江","无锡","苏州","其他"};
    private static final String[] list_addr_3={"不限","玄武","鼓楼","江宁","雨花","其他"};

    private StringBuffer addr_1=new StringBuffer();
    private StringBuffer addr_2=new StringBuffer();
    private StringBuffer addr_3=new StringBuffer();

    EditText name, address, mobile, telephone, zip;
    CheckBox isDefault;
    Boolean is_default=false;

    DeliveryAddress oldAddress, newAddress;
    Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_addr_edit);
        init();
	}

	protected void init(){
        intent = this.getIntent();
        Bundle bundle=intent.getExtras();
        oldAddress = (DeliveryAddress) bundle.getSerializable("address");

        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        mobile = (EditText) findViewById(R.id.mobile);
        telephone = (EditText) findViewById(R.id.telephone);
        zip = (EditText) findViewById(R.id.zip);
        isDefault = (CheckBox)findViewById(R.id.is_default);




        if(oldAddress == null){
            title.setText("增加地址");
            addr_1.replace(0,addr_1.length(),"不限");
            addr_2.replace(0,addr_2.length(),"不限");
            addr_3.replace(0,addr_3.length(),"不限");
        }else{
            title.setText("修改地址");
            title_right.setText("删除地址");
            title_right.setVisibility(View.VISIBLE);

            name.setText(oldAddress.receiver);
            address.setText(oldAddress.address);
            mobile.setText(oldAddress.mobile);
            telephone.setText(oldAddress.telephone);
            zip.setText(oldAddress.zip);
            if(oldAddress.isDefault){
                isDefault.setChecked(true);
            }else{
                isDefault.setChecked(false);
            }

            addr_1.replace(0,addr_1.length(),oldAddress.addr_1);
            addr_2.replace(0,addr_2.length(),oldAddress.addr_2);
            addr_3.replace(0,addr_3.length(),oldAddress.addr_3);
        }


        utility.setSpinner(this, R.id.addr_1, list_addr_1, addr_1);
        utility.setSpinner(this, R.id.addr_2, list_addr_2, addr_2);
        utility.setSpinner(this, R.id.addr_3, list_addr_3, addr_3);

        int[] ids = {R.id.ok, R.id.name_clear, R.id.address_clear, R.id.mobile_clear,
                R.id.telephone_clear, R.id.zip_clear};
        this.listen(ids);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
        case R.id.ok:
            newAddress = new DeliveryAddress();
            newAddress.isDefault = is_default;
            newAddress.receiver = name.getText().toString();
            newAddress.addr_1 = addr_1.toString();
            newAddress.addr_2 = addr_2.toString();
            newAddress.addr_3 = addr_3.toString();
            newAddress.address = address.getText().toString();
            newAddress.mobile = mobile.getText().toString();
            newAddress.telephone = telephone.getText().toString();
            newAddress.zip = zip.getText().toString();
            Log.i("new address", newAddress.toString());

            if(newAddress.receiver=="" || newAddress.address == "" || newAddress.mobile==""){
                tips.setMessage("必填项不能为空！");
                tips.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                tips.create().show();
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("address", newAddress);
            intent.replaceExtras(bundle);

            if(oldAddress == null)
                this.setResult(1, intent); //新增
            else {
                intent.putExtra("id", oldAddress.id);
                this.setResult(2, intent); //修改
            }

            this.finish();
            break;

        case R.id.title_right:
            tips.setMessage("确定删除该收货地址？");
            tips.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    intent.putExtra("id", oldAddress.id);
                    setResult(0, intent); //删除
                    finish();
                }
            });
            tips.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            tips.create().show();
            break;

        case R.id.name_clear: name.setText(""); break;
        case R.id.address_clear: address.setText(""); break;
        case R.id.mobile_clear: mobile.setText(""); break;
        case R.id.telephone_clear: telephone.setText(""); break;
        case R.id.zip_clear: zip.setText(""); break;
        default: break;
		};
	}


    // checkbox 的响应函数
    public void itemClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            is_default=true;
        }else{
            is_default=false;
        }
    }

}
