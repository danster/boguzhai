package com.boguzhai.activity.me.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
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
import com.boguzhai.logic.utils.DensityUtils;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.ImageApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadLotActivity extends BaseActivity {


    private Lottype_1 currentType1;
    private Lottype_2 currentType2;

    private String typeId = "";

    // 需要上传的值
    private StringBuffer lottypeId1 = new StringBuffer("");
    private StringBuffer lottypeId2 = new StringBuffer("");
    private StringBuffer lottypeId3 = new StringBuffer("");

    private ArrayList<Pair<String, String>> mapLottype1 = Variable.mapLottype1;
    private ArrayList<Pair<String, String>> mapLottype2 = new ArrayList<Pair<String, String>>();
    private ArrayList<Pair<String, String>> mapLottype3 = new ArrayList<Pair<String, String>>();

    private EditText me_upload_lot_name;
    private EditText me_upload_bottom_price;
    private TextView me_upload_contact;
    private TextView me_upload_contact_number;
    private TextView me_upload_protocol;
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

    private int[] imageViews = {R.id.me_upload_img1, R.id.me_upload_img2, R.id.me_upload_img3};

    private Bitmap[] images = new Bitmap[3];
    private int image = 0;

    private String[] image_urls = new String[3];

    private HttpClient conn;


    private String[] extra_info;//附加信息内容
    private String[] extra_info_id;//附加信息id

    private LinearLayout me_upload_extra_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!Variable.isLogin) {
            Utility.gotoLogin();
        }
        super.onCreate(savedInstanceState);
        if (!Variable.isLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        this.setScrollView(R.layout.me_upload);
        title.setText("上传拍品");
        init();
    }

    protected void init() {

        for(int i = 0; i < image_urls.length; i++) {
            image_urls[i] = "";
        }

        me_upload_protocol = (TextView) findViewById(R.id.me_upload_protocol);

        me_upload_extra_info = (LinearLayout) findViewById(R.id.me_upload_extra_info);

        setSpinners();

        me_upload_lot_name = (EditText) findViewById(R.id.me_upload_lot_name);
        me_upload_bottom_price = (EditText) findViewById(R.id.me_upload_bottom_price);
        me_upload_contact = (TextView) findViewById(R.id.me_upload_contact);
        me_upload_contact_number = (TextView) findViewById(R.id.me_upload_contact_number);
        me_upload_remark = (EditText) findViewById(R.id.me_upload_remark);
        me_upload_img1_info = (EditText) findViewById(R.id.me_upload_img1_info);
        me_upload_img2_info = (EditText) findViewById(R.id.me_upload_img2_info);
        me_upload_img3_info = (EditText) findViewById(R.id.me_upload_img3_info);

        me_upload_lot_name_delete = (ImageView) findViewById(R.id.me_upload_lot_name_delete);
        me_upload_bottom_price_delete = (ImageView) findViewById(R.id.me_upload_bottom_price_delete);

        me_upload_agree = (CheckBox) findViewById(R.id.me_upload_agree);

        me_upload_commit = (Button) findViewById(R.id.me_upload_commit);

        me_upload_img1 = (ImageView) findViewById(R.id.me_upload_img1);
        me_upload_img2 = (ImageView) findViewById(R.id.me_upload_img2);
        me_upload_img3 = (ImageView) findViewById(R.id.me_upload_img3);


        listen(me_upload_commit);
        listen(me_upload_lot_name_delete);
        listen(me_upload_bottom_price_delete);
        listen(me_upload_protocol);
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
                if (isChecked) {
                    me_upload_commit.setEnabled(true);
                } else {
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
                String image_info_1 = me_upload_img1_info.getText().toString().trim();
                String image_info_2 = me_upload_img2_info.getText().toString().trim();
                String image_info_3 = me_upload_img3_info.getText().toString().trim();
                if (TextUtils.isEmpty(lot_name)) {
                    Toast.makeText(Variable.app_context, "请输入拍品名称", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(typeId)) {
                    Toast.makeText(Variable.app_context, "请选择拍品分类", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(bottom_price)) {
                    Toast.makeText(Variable.app_context, "请输入底价", Toast.LENGTH_SHORT).show();
                } else if (!me_upload_agree.isChecked()) {
                    Toast.makeText(Variable.app_context, "请接受《在线拍品征集规则》", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Variable.app_context, "开始上传拍品", Toast.LENGTH_SHORT).show();
                    conn = new HttpClient();
                    conn.setUrl(Constant.url + "pAuctionInfoAction!uploadAuction.htm");
                    conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                    conn.setParam("typeId", typeId);
                    conn.setParam("name", lot_name);
                    conn.setParam("basePrice", bottom_price);
                    conn.setParam("mobile", Variable.account.mobile);
                    conn.setParam("remark", remark);
                    if(extra_info != null) {
                        for(int k = 0; k < extra_info.length; k++) {
                            conn.setParam("exinfo" + extra_info_id[k], extra_info[k]);
                        }
                    }

                    Log.i(TAG, image_urls[0]);
                    conn.setParam("image1_url", image_urls[0]);
                    conn.setParam("image1_info", image_info_1);
                    conn.setParam("image2_url", image_urls[1]);
                    conn.setParam("image2_info", image_info_2);
                    conn.setParam("image3_url", image_urls[2]);
                    conn.setParam("image3_info", image_info_3);
                    new Thread(new HttpPostRunnable(conn, new UploadAuctionHandler())).start();
                }
                break;
            case R.id.me_upload_lot_name_delete:
                me_upload_lot_name.setText("");
                break;
            case R.id.me_upload_bottom_price_delete:
                me_upload_bottom_price.setText("");
                break;
            case R.id.me_upload_img1:
                image = 0;
                ImageApi.showUpdateImageDialog();
                break;
            case R.id.me_upload_img2:
                if (!TextUtils.isEmpty(image_urls[0])) {
                    image = 1;
                    ImageApi.showUpdateImageDialog();
                }
                break;
            case R.id.me_upload_img3:
                if (!TextUtils.isEmpty(image_urls[1])) {
                    image = 2;
                    ImageApi.showUpdateImageDialog();
                }
                break;
            case R.id.me_upload_protocol:
                //打开浏览器页面
                Utility.openUrl("http://www.shbgz.com/otherAction!autionrule.htm?target=3_3_8");
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
        if (bitmap != null) {
            Log.i(TAG, "第" + image + "张图片");
            images[image] = bitmap;
            Tasks.uploadAuctionImage(images[image], new UploadAuctionImageHandler(image, (ImageView) findViewById(imageViews[image]), images[image]));
        }
    }


    public class UploadAuctionHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 0:
                    Utility.toastMessage("上传成功");
                    finish();
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

        public UploadAuctionImageHandler(int i, ImageView iv, Bitmap bitmap) {
            super();
            this.i = i;
            this.iv = iv;
            this.bitmap = bitmap;
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 0:
                    Utility.toastMessage("上传图片成功");
                    Log.i(TAG, data.toString());
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

    public void setSpinners() {

        // 拍品类型选择器之间的联动
        Utility.setSpinner(this, (Spinner) findViewById(R.id.me_upload_type1), Utility.getValueList(mapLottype1),
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        currentType1 = Variable.mapLottype.get(arg2);
                        typeId = currentType1.id;
                        Log.i(TAG, "typeid1:" + currentType1.id);
                        if(TextUtils.isEmpty(typeId)) {
                            me_upload_extra_info.removeAllViews();
                        }else {
                            requestExtraInfo();
                        }
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
                                        if (arg2 != 0) {
                                            typeId = currentType2.id;
                                            Log.i(TAG, "typeid2:" + currentType2.id);
                                            requestExtraInfo();
                                            Log.i(TAG, "name:" + currentType2.name);
                                        } else {
                                            typeId = currentType1.id;
                                            Log.i(TAG, "typeid3:" + currentType1.id);
                                            if(!TextUtils.isEmpty(typeId)) {
                                                requestExtraInfo();
                                            }
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
                                                        if (arg2 != 0) {
                                                            typeId = currentType2.child.get(arg2).id;
                                                            Log.i(TAG, "typeid4:" + currentType2.child.get(arg2).id);
                                                            requestExtraInfo();
                                                            Log.i(TAG, "name:" + currentType2.child.get(arg2).name);
                                                        } else {
                                                            typeId = currentType2.id;
                                                            Log.i(TAG, "typeid5:" + currentType2.id);
                                                            if(!TextUtils.isEmpty(typeId)) {
                                                                requestExtraInfo();
                                                            }
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


    private void requestExtraInfo() {
        HttpClient conn = new HttpClient();
        conn.setParam("typeId", typeId);
        conn.setUrl(Constant.url + "pCommonAction!getSpecialAttrByClass.htm");
        new Thread(new HttpPostRunnable(conn, new ExtraInfoHandler())).start();

    }

    private class ExtraInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            super.handlerData(code, data);
            switch (code) {
                case 0:
                    try {
                        me_upload_extra_info.removeAllViews();
                        JSONArray infos = data.getJSONArray("classList");
                        int count = infos.length();
                        extra_info = new String[count];
                        for(int i = 0; i < count; i++) {
                            extra_info[i] = "";
                        }
                        extra_info_id = new String[count];
                        String value;
                        String type;
                        for (int i = 0; i < count; i++) {
                            JSONObject info = (JSONObject) infos.get(i);
                            extra_info_id[i] = info.getString("id");
                            value = info.getString("value");
                            type = info.getString("editType");
                            LinearLayout ll = generateLinearLayout();
                            ll.addView(generateTextView(value));
                            if ("输入框".equals(type)) {//edittext填值
                                final EditText et = generateEditText();
                                final int finalI = i;
                                et.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if(TextUtils.isEmpty(et.getText().toString().trim())) {
                                            extra_info[finalI] = "";
                                        }else {
                                            extra_info[finalI] = et.getText().toString().trim();
                                        }
                                        Log.i(TAG, extra_info[finalI]);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                ll.addView(et);
                                ll.addView(generateImageView(et));
                            } else {//spinner单选
                                JSONArray jsonArray = info.getJSONArray("choice");
                                final ArrayList<String> choices = new ArrayList<>();
                                Spinner sp = generateSpinner(choices, jsonArray);
                                final int finalI1 = i;
                                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        extra_info[finalI1] = choices.get(position);
                                        Log.i(TAG, extra_info[finalI1]);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                ll.addView(sp);
                                ImageView iv = new ImageView(context);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.height = DensityUtils.dip2px(context, 20);
                                params.width = DensityUtils.dip2px(context, 12);
                                params.leftMargin = DensityUtils.dip2px(context, 5);
                                params.rightMargin = DensityUtils.dip2px(context, 8);
                                iv.setLayoutParams(params);
                                iv.setImageResource(R.drawable.base_arrow_right_small);
                                ll.addView(iv);

                            }
                            me_upload_extra_info.addView(ll);
                            if(i < (count - 1)) {
                                View view = new View(context);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.height = DensityUtils.dip2px(context, 0.8f);
                                view.setLayoutParams(params);
                                view.setBackgroundResource(R.color.single_line);
                                me_upload_extra_info.addView(view);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case 1:
                    Log.i(TAG, "获取extrainfo失败");
                    break;
            }

        }
    }


    private LinearLayout generateLinearLayout() {
        LinearLayout ll = new LinearLayout(context);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = DensityUtils.dip2px(context, 50);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(DensityUtils.dip2px(context, 10), DensityUtils.dip2px(context, 5),
                DensityUtils.dip2px(context, 10), DensityUtils.dip2px(context, 5));
        ll.setGravity(Gravity.CENTER + Gravity.LEFT);
        ll.setBackgroundColor(Color.WHITE);
        return ll;
    }


    private TextView generateTextView(String text) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextColor(context.getResources().getColor(R.color.dark_black));
        tv.setTextSize(15);
        tv.setSingleLine(true);
        tv.setWidth(DensityUtils.dip2px(context, 100));
        tv.setGravity(Gravity.CENTER_VERTICAL + Gravity.LEFT);
        return tv;
    }

    private EditText generateEditText() {
        EditText et = new EditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER_VERTICAL;
        et.setLayoutParams(params);
        et.setTextColor(context.getResources().getColor(R.color.dark_black));
        et.setTextSize(15);
        et.setHint("选填");
        et.setSingleLine(true);
        et.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        et.setGravity(Gravity.CENTER_VERTICAL + Gravity.LEFT);
        return et;
    }

    private ImageView generateImageView(final EditText et) {
        ImageView iv = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = DensityUtils.dip2px(context, 15);
        params.width = DensityUtils.dip2px(context, 15);
        params.leftMargin = DensityUtils.dip2px(context, 5);
        params.rightMargin = DensityUtils.dip2px(context, 5);
        params.gravity = Gravity.CENTER;
        iv.setLayoutParams(params);
        iv.setImageResource(R.drawable.base_clear);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });
        return iv;
    }


    private Spinner generateSpinner(ArrayList<String> choices, JSONArray jsonArray) {
        Spinner sp = new Spinner(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        sp.setLayoutParams(params);
        for (int k = 0; k < jsonArray.length(); k++) {
            try {
                choices.add(jsonArray.getString(k));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Map<String, String>> items = new ArrayList<Map<String, String>>();
        HashMap<String, String> map;

        for (String choice : choices) {
            map = new HashMap<String, String>();
            map.put("choice", choice);
            items.add(map);
        }
        sp.setAdapter(new SimpleAdapter(
                context, items,
                android.R.layout.simple_list_item_1,
                new String[]{"choice"},
                new int[]{android.R.id.text1}));
        return sp;
    }

}
