package com.boguzhai.activity.me.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

public class AddAdviceActivity extends BaseActivity {

    private EditText et_advice_title;
    private ImageView iv_advice_delete_title;
    private Spinner sp_advice_type;
    private EditText et_advice_order_no;
    private ImageView iv_advice_delete_order_no;
    private EditText et_advice;
    private Button btn_advice_commit;
    public String[] types = {"产品质量", "其他"};
    private String type = "产品质量";

    private HttpClient conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_add_advice);
        init();
    }

    private void init() {
        et_advice_title = (EditText) findViewById(R.id.et_advice_title);
        iv_advice_delete_title = (ImageView) findViewById(R.id.iv_advice_delete_title);
        sp_advice_type = (Spinner) findViewById(R.id.sp_advice_type);
        et_advice_order_no = (EditText) findViewById(R.id.et_advice_order_no);
        iv_advice_delete_order_no = (ImageView) findViewById(R.id.iv_advice_delete_order_no);
        btn_advice_commit = (Button) findViewById(R.id.btn_advice_commit);
        et_advice = (EditText) findViewById(R.id.et_advice);
        et_advice_title.setText("");


        listen(iv_advice_delete_title);
        listen(iv_advice_delete_order_no);
        listen(btn_advice_commit);

        Utility.setSpinner(this, (Spinner) findViewById(R.id.sp_advice_type), types, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = types[position];
                Log.i(TAG, "type:" + type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_advice_delete_title:
                et_advice_title.setText("");
                break;
            case R.id.iv_advice_delete_order_no:
                et_advice_order_no.setText("");
                break;
            case R.id.btn_advice_commit:
                if(TextUtils.isEmpty(et_advice_title.getText().toString().trim())) {
                    Toast.makeText(Variable.app_context, "请输入标题", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(et_advice.getText().toString().trim())) {
                    Toast.makeText(Variable.app_context, "请输入详细问题", Toast.LENGTH_SHORT).show();
                }else {
                    //网络请求
                    conn = new HttpClient();
                    conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                    conn.setUrl(Constant.url + "pClientInfoAction!uploadAdvice.htm");
                    conn.setParam("title", et_advice_title.getText().toString().trim());
                    conn.setParam("type", type);
                    conn.setParam("orderId", et_advice_title.getText().toString().trim());
                    conn.setParam("info", et_advice.getText().toString().trim());
                    new Thread(new HttpPostRunnable(conn, new AddAdviceHandler())).start();
                }
                break;
        }


    }

    private class AddAdviceHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(AddAdviceActivity.this, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(AddAdviceActivity.this, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddAdviceActivity.this, LoginActivity.class));
                    break;
                case 0:
                    Toast.makeText(AddAdviceActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    }

}
