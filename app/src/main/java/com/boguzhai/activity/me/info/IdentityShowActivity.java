package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;

public class IdentityShowActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_identity_show);
        title.setText("身份认证");
        init();
    }

    protected void init() {
//        if(Variable.account.authInfo.status.equals("1")){
//            findViewById(R.id.reVerify).setVisibility(View.VISIBLE);
//            this.listen(R.id.reVerify);
//        }else{
//            findViewById(R.id.reVerify).setVisibility(View.GONE);
//        }

        findViewById(R.id.reVerify).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.status)).setText(Variable.account.authInfo.getStatusStr());
        ((TextView)findViewById(R.id.property)).setText(Variable.account.authInfo.property.equals("1") ? "个人" : "单位");
        ((TextView)findViewById(R.id.name)).setText(Variable.account.authInfo.name);
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);

        if(Variable.account.authInfo.property.equals("1")){
            findViewById(R.id.layout_person).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_unit).setVisibility(View.GONE);

            ((TextView)findViewById(R.id.my_name)).setText("真实姓名");
            ((TextView)findViewById(R.id.credential_type)).setText(Variable.account.authInfo.type);
            ((TextView)findViewById(R.id.credential_number)).setText(Variable.account.authInfo.number);

        } else if(Variable.account.authInfo.property.equals("2")){
            findViewById(R.id.layout_person).setVisibility(View.GONE);
            findViewById(R.id.layout_unit).setVisibility(View.VISIBLE);

            ((TextView)findViewById(R.id.my_name)).setText("真实单位名称");
            ((TextView)findViewById(R.id.no_unit1)).setText(Variable.account.authInfo.licenseNumber);
            ((TextView)findViewById(R.id.no_unit2)).setText(Variable.account.authInfo.taxNumber);
            ((TextView)findViewById(R.id.no_unit3)).setText(Variable.account.authInfo.organizationNumber);
            ((TextView)findViewById(R.id.legal_person_name)).setText(Variable.account.authInfo.legalPersonName);
            ((TextView)findViewById(R.id.legal_person_type)).setText(Variable.account.authInfo.legalPersonType);
            ((TextView)findViewById(R.id.legal_person_number)).setText(Variable.account.authInfo.legalPersonNumber);

        } else {
            findViewById(R.id.layout_person).setVisibility(View.GONE);
            findViewById(R.id.layout_unit).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.reVerify:  startActivity(new Intent(this, IdentityVerifyActivity.class));  break;
            default: break;
        };
    }

}