package com.boguzhai.activity.me.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.ImageApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UploadLotActivity extends BaseActivity {


    private Lottype_1 currentType1;
    private Lottype_2 currentType2;

    private String typeId = "";

    // 需要上传的值
    private StringBuffer lottypeId1=new StringBuffer("");
    private StringBuffer lottypeId2=new StringBuffer("");
    private StringBuffer lottypeId3=new StringBuffer("");

    private ArrayList<Pair<String,String>> mapLottype1 = Variable.mapLottype1;
    private ArrayList<Pair<String,String>> mapLottype2 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapLottype3 = new ArrayList< Pair<String,String> >();

    private EditText me_upload_lot_name;
    private EditText me_upload_bottom_price;
    private TextView me_upload_contact;
    private TextView me_upload_contact_number;
    private EditText me_upload_remark;
    private EditText me_upload_time;
    private EditText me_upload_size;
    private EditText me_upload_style;
    private EditText me_upload_img1_info;
    private EditText me_upload_img2_info;
    private EditText me_upload_img3_info;

    private ImageView me_upload_lot_name_delete;
    private ImageView me_upload_bottom_price_delete;
    private ImageView me_upload_time_delete;
    private ImageView me_upload_style_delete;
    private ImageView me_upload_size_delete;
    private ImageView me_upload_img1;
    private ImageView me_upload_img2;
    private ImageView me_upload_img3;

    private CheckBox me_upload_agree;

    private Button me_upload_commit;

    private int[] imageViews = {R.id.me_upload_img1, R.id.me_upload_img2 ,R.id.me_upload_img3};

    private Bitmap[] images = new Bitmap[3];
    private int image = 0;

    private String[] image_urls = {"", "", ""};

    private HttpClient conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if(!Variable.isLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        this.setScrollView(R.layout.me_upload);
        title.setText("上传拍品");
        init();
	}

	protected void init(){


        setSpinners();

        me_upload_lot_name = (EditText) findViewById(R.id.me_upload_lot_name);
        me_upload_bottom_price = (EditText) findViewById(R.id.me_upload_bottom_price);
        me_upload_contact = (TextView) findViewById(R.id.me_upload_contact);
        me_upload_contact_number = (TextView) findViewById(R.id.me_upload_contact_number);
        me_upload_remark = (EditText) findViewById(R.id.me_upload_remark);
        me_upload_time = (EditText) findViewById(R.id.me_upload_time);
        me_upload_style = (EditText) findViewById(R.id.me_upload_style);
        me_upload_size = (EditText) findViewById(R.id.me_upload_size);
        me_upload_img1_info = (EditText) findViewById(R.id.me_upload_img1_info);
        me_upload_img2_info = (EditText) findViewById(R.id.me_upload_img2_info);
        me_upload_img3_info = (EditText) findViewById(R.id.me_upload_img3_info);

        me_upload_lot_name_delete = (ImageView) findViewById(R.id.me_upload_lot_name_delete);
        me_upload_bottom_price_delete = (ImageView) findViewById(R.id.me_upload_bottom_price_delete);
        me_upload_time_delete = (ImageView) findViewById(R.id.me_upload_time_delete);
        me_upload_style_delete = (ImageView) findViewById(R.id.me_upload_style_delete);
        me_upload_size_delete = (ImageView) findViewById(R.id.me_upload_size_delete);

        me_upload_agree = (CheckBox) findViewById(R.id.me_upload_agree);

        me_upload_commit = (Button) findViewById(R.id.me_upload_commit);

        me_upload_img1 = (ImageView) findViewById(R.id.me_upload_img1);
        me_upload_img2 = (ImageView) findViewById(R.id.me_upload_img2);
        me_upload_img3 = (ImageView) findViewById(R.id.me_upload_img3);


        listen(me_upload_commit);
        listen(me_upload_lot_name_delete);
        listen(me_upload_bottom_price_delete);
        listen(me_upload_time_delete);
        listen(me_upload_style_delete);
        listen(me_upload_size_delete);
        listen(me_upload_img1);
        listen(me_upload_img2);
        listen(me_upload_img3);


        me_upload_contact.setText(Variable.account.name);
        me_upload_contact_number.setText(Variable.account.mobile);

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
                String remark = me_upload_remark.getText().toString().trim();
                String age = me_upload_time.getText().toString().trim();
                String type = me_upload_style.getText().toString().trim();
                String size = me_upload_size.getText().toString().trim();

                String image_info_1 = me_upload_img1_info.getText().toString().trim();
                String image_info_2 = me_upload_img2_info.getText().toString().trim();
                String image_info_3 = me_upload_img3_info.getText().toString().trim();
                if(!me_upload_agree.isChecked()) {
                    Toast.makeText(Variable.app_context, "请接受《在线拍品征集规则》", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(lot_name)) {
                    Toast.makeText(Variable.app_context, "请输入拍品名称", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(bottom_price)){
                    Toast.makeText(Variable.app_context, "请输入底价", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(remark)) {
                    Toast.makeText(Variable.app_context, "请填写备注", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Variable.app_context, "开始上传拍品", Toast.LENGTH_SHORT).show();
                    conn = new HttpClient();
                    conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                    conn.setParam("typeId", typeId);
                    conn.setParam("name", lot_name);
                    conn.setParam("basePrice", bottom_price);
                    conn.setParam("mobile", Variable.account.mobile);
                    conn.setParam("remark", lottypeId3.toString());
                    conn.setParam("age", age);
                    conn.setParam("type", type);
                    conn.setParam("size", size);
                    conn.setParam("image1_url", image_urls[0]);
                    conn.setParam("image1_info", image_info_1);
                    conn.setParam("image2_url", image_urls[1]);
                    conn.setParam("image2_info", image_info_2);
                    conn.setParam("image3_url", image_urls[2]);
                    conn.setParam("image3_info", image_info_3);
                    conn.setUrl(Constant.url.replace("/phones/", "/") + "fileUploadAction!uploadAucitonImage.htm");

                    new Thread(new HttpPostRunnable(conn, new UploadAuctionHandler())).start();
                }



                break;
            case R.id.me_upload_lot_name_delete:
                me_upload_lot_name.setText("");
                break;
            case R.id.me_upload_bottom_price_delete:
                me_upload_bottom_price.setText("");
                break;
            case R.id.me_upload_time_delete:
                me_upload_time.setText("");
                break;
            case R.id.me_upload_style_delete:
                me_upload_style.setText("");
                break;
            case R.id.me_upload_size_delete:
                me_upload_size.setText("");
                break;
            case R.id.me_upload_img1:
                image = 0;
                ImageApi.showUpdateImageDialog();
                break;
            case R.id.me_upload_img2:
                if(!TextUtils.isEmpty(image_urls[0])) {
                    image = 1;
                    ImageApi.showUpdateImageDialog();
                }
                break;
            case R.id.me_upload_img3:
                if(!TextUtils.isEmpty(image_urls[1])) {
                    image = 2;
                    ImageApi.showUpdateImageDialog();
                }
                break;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode: 1 相机取照片; 2 4.4以下版本相册取照片; 3 4.4及4.4以上版本相册取照片;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        // 得到压缩过的照片,小于50KB
        Bitmap bitmap = ImageApi.getBitmap(requestCode, data);
        if(bitmap != null) {
            Log.i(TAG, "第" + image + "张图片");
            images[image] = bitmap;
            Tasks.uploadAuctionImage(images[image], new UploadAuctionImageHandler(image, (ImageView) findViewById(imageViews[image]), images[image]));
        }
    }


    public class UploadAuctionHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            switch(code){
                case 0:
                    Utility.toastMessage("上传成功");
                    break;
                case -1:
                    Utility.gotoLogin();
                    break;
                default:
                    Utility.toastMessage("上传失败");
                    break;
            }
        }
    }



    public class UploadAuctionImageHandler extends HttpJsonHandler {
        private ImageView iv = null;
        private Bitmap bitmap = null;
        private int i;//第i张图片
        public UploadAuctionImageHandler(int i, ImageView iv, Bitmap bitmap){
            super();
            this.i = i;
            this.iv = iv;
            this.bitmap = bitmap;
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            switch(code){
                case 0:
                    Utility.toastMessage("上传图片成功");
                    iv.setImageBitmap(bitmap);
                    try {
                        image_urls[i] = data.getString("url");
                        Log.i(TAG, "第" + i + "张图片的url:" + image_urls[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case -1:
                    Utility.gotoLogin();
                    break;
                default:
                    Utility.toastMessage("上传图片失败");
                    break;
            }
        }

    }

    public void setSpinners(){

        // 拍品类型选择器之间的联动
        Utility.setSpinner(this, (Spinner) findViewById(R.id.me_upload_type1), Utility.getValueList(mapLottype1),
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        currentType1 = Variable.mapLottype.get(arg2);
                        typeId = currentType1.id;
                        Log.i(TAG, "typeid:" + currentType1.id);
                        Log.i(TAG, "name:" + currentType1.name);
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
                                        if(arg2 != 0) {
                                            typeId = currentType2.id;
                                            Log.i(TAG, "typeid:" + currentType2.id);
                                            Log.i(TAG, "name:" + currentType2.name);
                                        }else {
                                            typeId = currentType1.id;
                                            Log.i(TAG, "typeid:" + currentType1.id);
                                            Log.i(TAG, "name:" + currentType1.name);
                                        }
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
                                                        if(arg2 != 0) {
                                                            typeId = currentType2.child.get(arg2).id;
                                                            Log.i(TAG, "typeid:" + currentType2.child.get(arg2).id);
                                                            Log.i(TAG, "name:" + currentType2.child.get(arg2).name);
                                                        }else {
                                                            typeId = currentType2.id;
                                                            Log.i(TAG, "typeid:" + currentType2.id);
                                                            Log.i(TAG, "name:" + currentType2.name);
                                                        }
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


//    private parseLotTypeId() {
//
//    }


}
