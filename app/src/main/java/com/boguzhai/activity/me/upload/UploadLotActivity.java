package com.boguzhai.activity.me.upload;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_upload);
        title.setText("上传拍品");
        init();
	}

	protected void init(){
        setSpinners();

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


	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
