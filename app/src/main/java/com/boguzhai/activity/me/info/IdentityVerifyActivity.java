package com.boguzhai.activity.me.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.utils.HttpClient;

import org.json.JSONObject;

public class IdentityVerifyActivity extends BaseActivity {
    protected TextView property;
    protected ImageView image1,image2,image3;

    private String[] propertyList = {"个人","单位"};
    private int propertyIndex = 0;
    private String[] credentialTypeList = {"二代身份证","三代身份证","港澳台身份证","护照","其它"};
    private int credentialTypeIndex = 0, legalTypeIndex=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_identity_verify);
        title.setText("身份认证");

        property = (TextView)findViewById(R.id.property);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);

        int[] ids = { R.id.submit, R.id.my_property, R.id.my_credential_type,
                      R.id.image1, R.id.image3, R.id.image3};
        this.listen(ids);
        init();
	}

	protected void init(){
        findViewById(R.id.layout_person).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_unit).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.my_name)).setText("真实姓名");
        ((EditText)findViewById(R.id.mobile)).setText(Variable.account.mobile);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            case R.id.my_property: //选择个人或者单位
                new AlertDialog.Builder(context).setSingleChoiceItems(propertyList, propertyIndex,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int index) {
                            dialog.dismiss();
                            propertyIndex = index;
                            property.setText(propertyList[index]);
                            if(property.getText().toString().equals("个人")){
                                findViewById(R.id.layout_person).setVisibility(View.VISIBLE);
                                findViewById(R.id.layout_unit).setVisibility(View.GONE);
                                ((TextView)findViewById(R.id.my_name)).setText("真实姓名");
                            } else if(property.getText().toString().equals("单位")){
                                findViewById(R.id.layout_person).setVisibility(View.GONE);
                                findViewById(R.id.layout_unit).setVisibility(View.VISIBLE);
                                ((TextView)findViewById(R.id.my_name)).setText("真实单位名称");
                            }
                        }
                    }).setNegativeButton("取消", null).show();
            break;
            case R.id.my_credential_type: //选择个人证件类型
                new AlertDialog.Builder(context).setSingleChoiceItems(credentialTypeList, credentialTypeIndex,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int index) {
                            credentialTypeIndex = index;
                            ((TextView)findViewById(R.id.credential_type)).setText(credentialTypeList[index]);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                break;
            case R.id.my_legal_person_type: //选择法人证件类型
                new AlertDialog.Builder(context).setSingleChoiceItems(credentialTypeList, legalTypeIndex,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int index) {
                            legalTypeIndex = index;
                            ((TextView)findViewById(R.id.legal_person_type)).setText(credentialTypeList[index]);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                break;
            case R.id.submit:
                HttpClient conn = new HttpClient();
                conn.setParam("sessionid", Variable.account.sessionid);
                if(property.getText().toString().equals("个人")){
                    conn.setParam("property", "1");
                } else if(property.getText().toString().equals("单位")){
                    conn.setParam("property", "2");
                }



                //conn.setUrl(Constant.url+"pClientInfoAction!setAccountInfo.htm");
                //new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();

                break;
        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch(code){
                case 0:
                    baseActivity.getAlert("操作成功")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            baseActivity.startActivity(new Intent(baseActivity,AccountInfoActivity.class));
                            }
                        }).show();
                    break;
                default:
                    baseActivity.alertMessage("操作失败");
                    break;
            }
        }
    }

}
