package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.FileApi;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountInfoEditActivity extends BaseActivity {

    private ImageView image;
    private Bitmap newImage=null;
    private String image_url="";

    private ArrayList<Pair<String,String>> mapAddress1 = Variable.mapProvince;
    private ArrayList<Pair<String,String>> mapAddress2 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapAddress3 = new ArrayList< Pair<String,String> >();
    private StringBuffer addressId_1 =new StringBuffer("");
    private StringBuffer addressId_2 =new StringBuffer("");
    private StringBuffer addressId_3 =new StringBuffer("");
    private Address_1 currentAddress1;
    private Address_2 currentAddress2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_edit);
        title.setText("编辑个人信息");
        Variable.currentActivity = this;

        init();
	}

	private void init(){
        image = (ImageView)findViewById(R.id.image);
        // 省市区选择器之间的联动
        Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_1), Utility.getValueList(mapAddress1),
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        currentAddress1 = Variable.mapZone.get(arg2);
                        addressId_1.replace(0, addressId_1.length(), mapAddress1.get(arg2).first);
                        addressId_2.replace(0, addressId_2.length(), "");
                        addressId_3.replace(0, addressId_3.length(), "");

                        mapAddress2.clear();
                        for (Address_2 address_2 : currentAddress1.child) {
                            mapAddress2.add(new Pair<String, String>(address_2.id, address_2.name));
                        }

                        Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_2), Utility.getValueList(mapAddress2),
                                new AdapterView.OnItemSelectedListener() {
                                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                        currentAddress2 = currentAddress1.child.get(arg2);
                                        addressId_2.replace(0, addressId_2.length(), mapAddress2.get(arg2).first);
                                        addressId_3.replace(0, addressId_3.length(), "");

                                        mapAddress3.clear();
                                        for (Address_3 address_3 : currentAddress2.child) {
                                            mapAddress3.add(new Pair<String, String>(address_3.id, address_3.name));
                                        }

                                        Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_3), Utility.getValueList(mapAddress3),
                                                new AdapterView.OnItemSelectedListener() {
                                                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                                        addressId_3.replace(0, addressId_3.length(), mapAddress3.get(arg2).first);
                                                    }

                                                    public void onNothingSelected(AdapterView<?> arg0) {
                                                        addressId_3.replace(0, addressId_3.length(), "");
                                                    }
                                                });

                                    }

                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        addressId_2.replace(0, addressId_2.length(), "");
                                        addressId_3.replace(0, addressId_3.length(), "");
                                    }
                                });
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        addressId_1.replace(0, addressId_1.length(), "");
                        addressId_2.replace(0, addressId_2.length(), "");
                        addressId_3.replace(0, addressId_3.length(), "");
                    }
                });

        fillAccountInfo();
		int[] ids = { R.id.my_email, R.id.my_mobile, R.id.ok, R.id.name_clear,
                      R.id.nickname_clear, R.id.my_photo, R.id.telephone_clear,
                      R.id.fax_clear, R.id.qq_clear, R.id.address_clear };
		this.listen(ids);
	}

    private void fillAccountInfo(){
        image.setImageBitmap(Variable.account.image);
        ((TextView)findViewById(R.id.name)).setText(Variable.account.name);
        ((EditText)findViewById(R.id.nickname)).setText(Variable.account.nickname);
        ((EditText)findViewById(R.id.address)).setText(Variable.account.address);
        ((EditText)findViewById(R.id.telephone)).setText(Variable.account.telephone);
        ((EditText)findViewById(R.id.fax)).setText(Variable.account.fax);
        ((EditText)findViewById(R.id.qq)).setText(Variable.account.qq);
        ((TextView)findViewById(R.id.email)).setText(Variable.account.email);
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);

    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.ok:
            String nickname = ((EditText)findViewById(R.id.nickname)).getText().toString();
            String address = ((EditText)findViewById(R.id.address)).getText().toString();
            String telephone = ((EditText)findViewById(R.id.telephone)).getText().toString();
            String fax = ((EditText)findViewById(R.id.fax)).getText().toString();
            String qq = ((EditText)findViewById(R.id.qq)).getText().toString();

            HttpClient conn = new HttpClient();
            conn.setParam("sessionid", Variable.account.sessionid);
            conn.setParam("nickname", nickname);
            conn.setParam("address_1", addressId_1.toString());
            conn.setParam("address_2", addressId_2.toString());
            conn.setParam("address_3", addressId_3.toString());
            conn.setParam("address", address);
            conn.setParam("telephone", telephone);
            conn.setParam("fax", fax);
            conn.setParam("qq", qq);
            conn.setParam("photo", image_url);
            conn.setUrl(Constant.url+"pClientInfoAction!setAccountInfo.htm");
            new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();
            break;
        case R.id.my_photo:
            Utility.showUpdateImageDialog();
            break;
        case R.id.my_email:
            startActivity(new Intent(this, AccountBindEmailActivity.class));
            break;
        case R.id.my_mobile:
            startActivity(new Intent(this, AccountBindMobileActivity.class));
            break;
        case R.id.nickname_clear: ((EditText)findViewById(R.id.nickname)).setText(""); break;
        case R.id.telephone_clear:((EditText)findViewById(R.id.telephone)).setText("");break;
        case R.id.fax_clear:      ((EditText)findViewById(R.id.fax)).setText("");      break;
        case R.id.qq_clear:       ((EditText)findViewById(R.id.qq)).setText("");       break;
        case R.id.address_clear:  ((EditText)findViewById(R.id.address)).setText("");  break;

        default: break;
		};
	}


    private void updateImage(Bitmap bitmap){
        if(bitmap.equals(null)){
            return;
        }
        newImage = FileApi.compressBitmap(bitmap);

        HttpClient conn = new HttpClient();
        conn.setParam("sessionid", Variable.account.sessionid);
        conn.setParam("type","当前头像");
        conn.setParamBitmap("file", newImage);
        conn.setUrl(Constant.url + "fileUploadAction!uploadImage.htm");
        new Thread(new HttpPostRunnable(conn,new UpdateImageHandler())).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        updateImage(Utility.getBitmap(requestCode, data));
    }

    class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch(code){
                case 0:
                    baseActivity.alertMessage("修改个人信息成功");
                    break;

                case -1:
                    Variable.isLogin = false;
                    startActivity(new Intent(baseActivity, LoginActivity.class));

                case 1:
                    baseActivity.alertMessage("修改个人信息失败");
                    break;
            }
        }
    }

    class UpdateImageHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch(code){
                case 0:
                    baseActivity.toastMessage("更新图像成功");
                    try {
                        image_url = data.getString("filepath");
                        image.setImageBitmap(newImage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    baseActivity.toastMessage("更新图像失败");
                    break;
            }
        }
    }


}


