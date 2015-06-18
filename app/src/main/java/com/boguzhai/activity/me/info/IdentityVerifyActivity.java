package com.boguzhai.activity.me.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.thread.UploadImageHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.ImageApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

public class IdentityVerifyActivity extends BaseActivity {
    private TextView property;
    private int currId=0;

    private String[] propertyList = {"个人","单位"};
    private int propertyIndex = 0;
    private String[] credentialTypeList = {"二代身份证","三代身份证","港澳台身份证","护照","其它"};
    private int credentialTypeIndex = 0, legalTypeIndex=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_identity_verify);
        title.setText("身份认证");
        init();
    }

	protected void init(){
        property = (TextView)findViewById(R.id.property);
        int[] ids = { R.id.submit, R.id.my_property, R.id.my_credential_type, R.id.my_legal_person_type,
                      R.id.image1, R.id.image2, R.id.image3, R.id.image_legal_person,
                      R.id.image_unit1, R.id.image_unit2, R.id.image_unit3 };
        this.listen(ids);
        this.fillAuthInfo();
    }

    private void fillAuthInfo(){
        findViewById(R.id.layout_person).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_unit).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.status)).setText(Variable.account.authInfo.getStatusStr());
        ((TextView)findViewById(R.id.property)).setText(Variable.account.authInfo.getPropertyStr());

        if(Variable.account.authInfo.property.equals("1")){
            ((TextView)findViewById(R.id.my_name)).setText("真实姓名");
        } else {
            ((TextView)findViewById(R.id.my_name)).setText("真实单位名称");
        }
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
        switch (view.getId()) {
            case R.id.image1:  currId=R.id.image1; ImageApi.showUpdateImageDialog();          break;
            case R.id.image2:  currId=R.id.image2; ImageApi.showUpdateImageDialog();          break;
            case R.id.image3:  currId=R.id.image3; ImageApi.showUpdateImageDialog();          break;
            case R.id.image_unit1: currId=R.id.image_unit1; ImageApi.showUpdateImageDialog(); break;
            case R.id.image_unit2: currId=R.id.image_unit2; ImageApi.showUpdateImageDialog(); break;
            case R.id.image_unit3: currId=R.id.image_unit3; ImageApi.showUpdateImageDialog(); break;
            case R.id.image_legal_person: currId=R.id.image_legal_person; ImageApi.showUpdateImageDialog(); break;

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
                            ((TextView)findViewById(R.id.type)).setText(credentialTypeList[index]);
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
                conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
                if(((TextView)findViewById(R.id.my_name)).getText().equals("个人")){
                    conn.setParam("property", "1");
                } else {
                    conn.setParam("property", "2");
                }
                conn.setParam("name", ((TextView)findViewById(R.id.name)).getText().toString());

                if(property.getText().toString().equals("个人")){
                    conn.setParam("type", ((TextView)findViewById(R.id.type)).getText().toString());
                    conn.setParam("number", ((TextView)findViewById(R.id.number)).getText().toString());

                }else if(property.getText().toString().equals("单位")){
                    conn.setParam("licenseNumber", ((TextView)findViewById(R.id.no_unit1)).getText().toString());
                    conn.setParam("taxNumber", ((TextView)findViewById(R.id.no_unit2)).getText().toString());
                    conn.setParam("organizationNumber", ((TextView)findViewById(R.id.no_unit3)).getText().toString());
                    conn.setParam("legalPersonName", ((TextView)findViewById(R.id.legal_person_name)).getText().toString());
                    conn.setParam("legalPersonType", ((TextView)findViewById(R.id.legal_person_type)).getText().toString());
                    conn.setParam("legalPersonNumber", ((TextView)findViewById(R.id.legal_person_number)).getText().toString());
                }

                conn.setUrl(Constant.url+"pClientInfoAction!setAuthInfo.htm");
                new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();
                Utility.showLoadingDialog("正在提交认证信息，请稍后...");

                break;
        default: break;
		};
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode: 1 相机取照片; 2 4.4以下版本相册取照片; 3 4.4及4.4以上版本相册取照片;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        // 得到压缩过的照片,小于50KB
        Bitmap bm = ImageApi.getBitmap(requestCode, data);
        if(bm != null) {
            String imageType = "";
            switch (currId){
                case R.id.image1:       imageType="证件信息";            break;
                case R.id.image2:       imageType="证件信息";            break;
                case R.id.image3:       imageType="证件信息";            break;
                case R.id.image_unit1:  imageType="营业执照";            break;
                case R.id.image_unit2:  imageType="税务登记证";          break;
                case R.id.image_unit3:  imageType="组织机构代码";         break;
                case R.id.image_legal_person:   imageType="法人代表证件"; break;
                default:                                                return;
            }
            Tasks.uploadImage(imageType, bm, new UploadImageHandler((ImageView)findViewById(currId), bm));
        }
    }

    class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            Utility.dismissLoadingDialog();
            super.handlerData(code, data);
            switch(code){
                case 0:
                    Utility.alertDialog("提交认证信息成功", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    break;
                default:
                    Utility.alertDialog("提交认证信息失败",null);
                    break;
            }
        }
    }



}
