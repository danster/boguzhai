package com.boguzhai.activity.me.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class IdentityVerifyActivity extends BaseActivity {
    protected TextView property, credential_type;
    protected EditText name, mobile, credential_number;
    protected ImageView credential_image;

    private String[] propertyList = {"个人","单位"};
    private int propertyIndex = 0;
    private String[] credentialTypeList = {"二代身份证","三代身份证","港澳台身份证","护照","其它"};
    private int credentialTypeIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_identity_verify);
        title.setText("身份认证");

        name = (EditText)findViewById(R.id.name);
        mobile = (EditText)findViewById(R.id.mobile);
        credential_number = (EditText)findViewById(R.id.credential_number);
        property = (TextView)findViewById(R.id.property);
        credential_type = (TextView)findViewById(R.id.credential_type);
        credential_image = (ImageView)findViewById(R.id.credential_image);

        int[] ids = { R.id.submit, R.id.my_name_clear, R.id.my_mobile_clear, R.id.my_property,
                      R.id.my_credential_type, R.id.my_credential_number_clear,
                      R.id.my_credential_image };
        this.listen(ids);
        setBaseEnv();
	}

	protected void setBaseEnv(){
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            case R.id.my_name_clear:
                name.setText("");
            break;
            case R.id.my_mobile_clear:
                mobile.setText("");
                break;
            case R.id.my_credential_number_clear:
                credential_number.setText("");
                break;
            case R.id.my_property:
                new AlertDialog.Builder(context).setSingleChoiceItems(propertyList, propertyIndex,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int index) {
                            propertyIndex = index;
                            property.setText(propertyList[index]);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
            break;
            case R.id.my_credential_type:
                new AlertDialog.Builder(context).setSingleChoiceItems(credentialTypeList, credentialTypeIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                credentialTypeIndex = index;
                                credential_type.setText(credentialTypeList[index]);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
                break;
        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }


}
