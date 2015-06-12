package com.boguzhai.activity.me.info;

import android.app.ProgressDialog;
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
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.thread.UploadImageHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.ImageApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

public class AccountInfoEditActivity extends BaseActivity {

    // 以下变量在实现“省市区选择器之间的联动”时使用
    private ArrayList<Pair<String,String>> mapAddress1 = Variable.mapProvince;
    private ArrayList<Pair<String,String>> mapAddress2 = new ArrayList< Pair<String,String> >();
    private ArrayList<Pair<String,String>> mapAddress3 = new ArrayList< Pair<String,String> >();
    private StringBuffer address_1 =new StringBuffer("");
    private StringBuffer address_2 =new StringBuffer("");
    private StringBuffer address_3 =new StringBuffer("");
    private Address_1 currentAddress1;
    private Address_2 currentAddress2;
    boolean index1boost=true, index2boost=true;
    private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_edit);
        title.setText("编辑个人信息");
        init();
	}

	private void init(){
        dialog = Utility.getProgressDialog("正在提交个人信息，请等待...");
        fillAccountInfo();

        // 省市区选择器之间的联动
        Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_1), Utility.getValueList(mapAddress1),
            new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    currentAddress1 = Variable.mapZone.get(arg2);
                    address_1.replace(0, address_1.length(), mapAddress1.get(arg2).second);
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
                                address_2.replace(0, address_2.length(), mapAddress2.get(arg2).second);
                                address_3.replace(0, address_3.length(), "");

                                mapAddress3.clear();
                                for (Address_3 address_3 : currentAddress2.child) {
                                    mapAddress3.add(new Pair<String, String>(address_3.id, address_3.name));
                                }

                                Utility.setSpinner(baseActivity, (Spinner) findViewById(R.id.address_3), Utility.getValueList(mapAddress3),
                                    new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                            address_3.replace(0, address_3.length(), mapAddress3.get(arg2).second);
                                        }
                                        public void onNothingSelected(AdapterView<?> arg0) {}
                                    });

                                if (index2boost && arg2 == Variable.account.addressIndex2()) {
                                    ((Spinner) findViewById(R.id.address_3)).setSelection(Variable.account.addressIndex3());
                                }
                                index2boost = false;
                            }
                            public void onNothingSelected(AdapterView<?> arg0) {}
                        });
                    if (index1boost && arg2 == Variable.account.addressIndex1()) {
                        ((Spinner) findViewById(R.id.address_2)).setSelection(Variable.account.addressIndex2());
                    }
                    index1boost = false;
                }
                public void onNothingSelected(AdapterView<?> arg0) {}
            });
        ((Spinner) findViewById(R.id.address_1)).setSelection(Variable.account.addressIndex1());

		int[] ids = { R.id.my_email, R.id.my_mobile, R.id.ok, R.id.name_clear,
                      R.id.nickname_clear, R.id.my_photo, R.id.telephone_clear,
                      R.id.fax_clear, R.id.qq_clear, R.id.address_clear };
		this.listen(ids);
	}

    // 修改之前先填充账户信息
    private void fillAccountInfo(){
        Tasks.showImage(Variable.account.imageUrl, (ImageView) findViewById(R.id.image), 2);

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
            if(address_1.toString().equals("") || address_1.toString().equals("不限") ||
                    address_2.toString().equals("") || address_2.toString().equals("不限") ||
                    address_3.toString().equals("") || address_3.toString().equals("不限") ){
                Utility.alertMessage("请完整填写地区信息");
                break;
            }

            HttpClient conn = new HttpClient();
            conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
            conn.setParam("address_1", address_1.toString());
            conn.setParam("address_2", address_2.toString());
            conn.setParam("address_3", address_3.toString());
            conn.setParam("nickname",  ((EditText)findViewById(R.id.nickname)).getText().toString());
            conn.setParam("address",   ((EditText)findViewById(R.id.address)).getText().toString());
            conn.setParam("telephone", ((EditText)findViewById(R.id.telephone)).getText().toString());
            conn.setParam("fax",       ((EditText)findViewById(R.id.fax)).getText().toString());
            conn.setParam("qq",        ((EditText)findViewById(R.id.qq)).getText().toString());
            conn.setUrl(Constant.url+"pClientInfoAction!setAccountInfo.htm");
            new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();
            dialog.show();
            break;

        case R.id.my_email:  Utility.gotoActivity(AccountBindEmailActivity.class);     break;
        case R.id.my_mobile: Utility.gotoActivity(AccountBindMobileActivity.class);    break;
        case R.id.my_photo:       ImageApi.showUpdateImageDialog();                    break;
        case R.id.nickname_clear: ((EditText)findViewById(R.id.nickname)).setText(""); break;
        case R.id.telephone_clear:((EditText)findViewById(R.id.telephone)).setText("");break;
        case R.id.fax_clear:      ((EditText)findViewById(R.id.fax)).setText("");      break;
        case R.id.qq_clear:       ((EditText)findViewById(R.id.qq)).setText("");       break;
        case R.id.address_clear:  ((EditText)findViewById(R.id.address)).setText("");  break;
        default: break;
		};
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode: 1 相机取照片; 2 4.4以下版本相册取照片; 3 4.4及4.4以上版本相册取照片;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {  return;}
        
        // 得到压缩过的照片,小于50KB
        Bitmap bitmap = ImageApi.getBitmap(requestCode, data);
        if(bitmap != null) {
            Tasks.uploadImage("当前图像", bitmap, new UploadImageHandler((ImageView)findViewById(R.id.image), bitmap));
        }
    }

    class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            dialog.dismiss();
            super.handlerData(code,data);
            switch(code){
                case 0: Utility.alertMessage("修改个人信息成功!");break;
                case 1: Utility.alertMessage("修改个人信息失败!");break;
            }
        }
    }

}


