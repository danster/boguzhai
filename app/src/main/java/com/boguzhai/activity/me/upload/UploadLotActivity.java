package com.boguzhai.activity.me.upload;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.utils.Utility;

import java.util.ArrayList;

public class UploadLotActivity extends BaseActivity {


    private Lottype_1 currentType1;
    private Lottype_2 currentType2;
    private Auction currentAuction;

    // 需要上传的值
    private StringBuffer lottypeId1=new StringBuffer("");
    private StringBuffer lottypeId2=new StringBuffer("");
    private StringBuffer lottypeId3=new StringBuffer("");

    private ArrayList<Pair<String,String>> mapLottype1 = Variable.mapLottype1;
    private ArrayList<Pair<String,String>> mapLottype2 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapLottype3 = new ArrayList< Pair<String,String> >();

    private TextView me_upload_lot_name;
    private TextView me_upload_bottom_price;
    private TextView me_upload_commission;
    private TextView me_upload_contact;
    private TextView me_upload_contact_number;
    private TextView me_upload_remark;

    private ImageView me_upload_lot_name_delete;
    private ImageView me_upload_bottom_price_delete;
    private ImageView me_upload_contact_delete;
    private ImageView me_upload_contact_number_delete;

    private CheckBox me_upload_agree;

    private Button me_upload_commit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_upload);
        title.setText("上传拍品");
        init();
	}

	protected void init(){
        setSpinners();

        me_upload_lot_name = (TextView) findViewById(R.id.me_upload_lot_name);
        me_upload_bottom_price = (TextView) findViewById(R.id.me_upload_bottom_price);
        me_upload_commission = (TextView) findViewById(R.id.me_upload_commission);
        me_upload_contact = (TextView) findViewById(R.id.me_upload_contact);
        me_upload_contact_number = (TextView) findViewById(R.id.me_upload_contact_number);
        me_upload_remark = (TextView) findViewById(R.id.me_upload_remark);

        me_upload_lot_name_delete = (ImageView) findViewById(R.id.me_upload_lot_name_delete);
        me_upload_bottom_price_delete = (ImageView) findViewById(R.id.me_upload_bottom_price_delete);
        me_upload_contact_delete = (ImageView) findViewById(R.id.me_upload_contact_delete);
        me_upload_contact_number_delete = (ImageView) findViewById(R.id.me_upload_contact_number_delete);

        me_upload_agree = (CheckBox) findViewById(R.id.me_upload_agree);

        me_upload_commit = (Button) findViewById(R.id.me_upload_commit);

        listen(me_upload_commit);
        listen(me_upload_lot_name_delete);
        listen(me_upload_bottom_price_delete);
        listen(me_upload_contact_delete);
        listen(me_upload_contact_number_delete);


        me_upload_agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    me_upload_commit.setEnabled(true);
                }else {
                    me_upload_commit.setEnabled(false);
                }
            }
        });

	}


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.me_upload_commit:
                String lot_name = me_upload_lot_name.getText().toString().trim();
                String bottom_price = me_upload_bottom_price.getText().toString().trim();
                String commission = me_upload_commission.getText().toString().trim();
                String contact = me_upload_contact.getText().toString().trim();
                String contact_number = me_upload_contact_number.getText().toString().trim();
                String remark = me_upload_remark.getText().toString().trim();


                if(TextUtils.isEmpty(lot_name)) {
                    Toast.makeText(Variable.app_context, "请输入拍品名称", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(bottom_price)){
                    Toast.makeText(Variable.app_context, "请输入底价", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(contact)) {
                    Toast.makeText(Variable.app_context, "请输入联系人", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(contact_number)) {
                    Toast.makeText(Variable.app_context, "请输入联系方式", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Variable.app_context, "正在上传拍品信息", Toast.LENGTH_SHORT).show();
                }



                break;
            case R.id.me_upload_lot_name_delete:
                me_upload_lot_name.setText("");
                break;
            case R.id.me_upload_bottom_price_delete:
                me_upload_bottom_price.setText("");
                break;
            case R.id.me_upload_contact_delete:
                me_upload_contact.setText("");
                break;
            case R.id.me_upload_contact_number_delete:
                me_upload_contact_number.setText("");
                break;
        }


    }










    public void setSpinners(){

        // 拍品类型选择器之间的联动
        Utility.setSpinner(this, (Spinner) findViewById(R.id.me_upload_type1), Utility.getValueList(mapLottype1),
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        currentType1 = Variable.mapLottype.get(arg2);
                        lottypeId1.replace(0, lottypeId1.length(), mapLottype1.get(arg2).first);
                        // 重置
                        lottypeId2.replace(0, lottypeId2.length(), "");
                        lottypeId3.replace(0, lottypeId3.length(), "");

                        mapLottype2.clear();
                        for (Lottype_2 lottype_2 : currentType1.child) {
                            mapLottype2.add(new Pair<String, String>(lottype_2.id, lottype_2.name));
                        }

                        Utility.setSpinner(UploadLotActivity.this, (Spinner) findViewById(R.id.me_upload_type2), Utility.getValueList(mapLottype2),
                                new AdapterView.OnItemSelectedListener() {
                                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                        currentType2 = currentType1.child.get(arg2);
                                        lottypeId2.replace(0, lottypeId2.length(), mapLottype2.get(arg2).first);
                                        // 先重置
                                        lottypeId3.replace(0, lottypeId3.length(), "");

                                        mapLottype3.clear();
                                        for (Lottype_3 lottype_3 : currentType2.child) {
                                            mapLottype3.add(new Pair<String, String>(lottype_3.id, lottype_3.name));
                                        }

                                        Utility.setSpinner(UploadLotActivity.this, (Spinner) findViewById(R.id.me_upload_type3), Utility.getValueList(mapLottype3),
                                                new AdapterView.OnItemSelectedListener() {
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
    }



}
